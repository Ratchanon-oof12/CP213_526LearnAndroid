package com.example.financialassistant.ai

import android.util.Log
import com.example.financialassistant.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.net.URL
import java.util.Collections

class AiClient(
    private val baseUrl: String = BuildConfig.AI_PROXY_BASE_URL,
    private val geminiApiKey: String = BuildConfig.GEMINI_API_KEY,
    private val geminiModel: String = BuildConfig.GEMINI_MODEL
) {
    data class CachedInsights(val response: AiInsightsResponse, val fetchedAtMs: Long)

    fun isConfigured(): Boolean = baseUrl.isNotBlank() || geminiApiKey.isNotBlank()

    suspend fun getInsights(request: AiInsightsRequest): Result<AiInsightsResponse> {
        return runCatching {
            withContext(Dispatchers.IO) {
                val now = System.currentTimeMillis()
                val cacheKey = "${request.kind}:${request.userName}:${request.snapshot.yearMonth}"
                val last = cache[cacheKey]
                if (last != null && now - last.fetchedAtMs < REQUEST_COOLDOWN_MS) {
                    return@withContext last.response
                }

                val useProxy = baseUrl.isNotBlank()
                val url = if (useProxy) {
                    URL(baseUrl.trimEnd('/') + "/insights")
                } else {
                    if (geminiApiKey.isBlank()) {
                        throw IllegalStateException("AI is not configured. Set BuildConfig.AI_PROXY_BASE_URL or BuildConfig.GEMINI_API_KEY.")
                    }
                    val model = geminiModel.ifBlank { "gemini-2.0-flash" }
                    // Google AI Studio / Gemini API (Generative Language API v1beta).
                    URL("https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$geminiApiKey")
                }
                val body = if (useProxy) {
                    request.toJson().toString()
                } else {
                    request.toGeminiGenerateContentJson().toString()
                }

                // One retry for transient timeout/network instability.
                val fresh = executeHttpPost(url, body, useProxy)
                    ?: executeHttpPost(url, body, useProxy, isRetry = true)
                    ?: throw IllegalStateException("AI request failed after retry.")
                cache[cacheKey] = CachedInsights(response = fresh, fetchedAtMs = now)
                fresh
            }
        }
    }

    companion object {
        private const val REQUEST_COOLDOWN_MS = 45_000L
        private val cache = Collections.synchronizedMap(mutableMapOf<String, CachedInsights>())
    }
}

private fun executeHttpPost(
    url: URL,
    body: String,
    useProxy: Boolean,
    isRetry: Boolean = false
): AiInsightsResponse? {
    return try {
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 20_000
            readTimeout = 35_000
            doOutput = true
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
            setRequestProperty("Accept", "application/json")
        }

        conn.outputStream.use { os ->
            os.write(body.toByteArray(Charsets.UTF_8))
        }

        val code = conn.responseCode
        val stream = if (code in 200..299) conn.inputStream else conn.errorStream
        val text = BufferedReader(InputStreamReader(stream)).use { it.readText() }

        if (code !in 200..299) {
            Log.w("AiClient", "Non-2xx from AI: $code $text")
            throw IllegalStateException(mapHttpError(code, text))
        }

        if (useProxy) parseProxyResponse(text) else parseGeminiResponse(text)
    } catch (e: SocketTimeoutException) {
        if (isRetry) throw IllegalStateException("AI request timed out. Check device internet/DNS or try a different network.", e)
        null
    } catch (e: UnknownHostException) {
        throw IllegalStateException("Cannot resolve AI host. Check internet connection or DNS settings.", e)
    }
}

private fun mapHttpError(code: Int, responseBody: String): String {
    return when (code) {
        400 -> "AI request rejected (400). Verify request format."
        401 -> "AI key invalid/unauthorized (401). Check GEMINI_API_KEY."
        403 -> "AI key forbidden (403). Verify key restrictions (package name + SHA-1)."
        404 -> "AI model/endpoint not found (404)."
        429 -> "AI rate limit reached (429). Please wait and retry."
        in 500..599 -> "AI service unavailable ($code). Try again shortly."
        else -> {
            val brief = responseBody.take(220).replace('\n', ' ')
            "AI error ($code): $brief"
        }
    }
}

private fun AiInsightsRequest.toJson(): JSONObject {
    val snap = snapshot
    val snapObj = JSONObject()
        .put("yearMonth", snap.yearMonth)
        .put("incomeTotal", snap.incomeTotal)
        .put("expenseTotal", snap.expenseTotal)
        .put(
            "topExpenseCategories",
            JSONArray().apply {
                snap.topExpenseCategories.forEach {
                    put(JSONObject().put("categoryName", it.categoryName).put("total", it.total))
                }
            }
        )
        .put(
            "dailyExpenses",
            JSONArray().apply {
                snap.dailyExpenses.forEach {
                    put(JSONObject().put("day", it.day).put("total", it.total))
                }
            }
        )

    return JSONObject()
        .put("kind", kind)
        .put("userName", userName)
        .put("assistantName", assistantName)
        .put("extraContext", extraContext)
        .put("snapshot", snapObj)
}

private fun parseProxyResponse(text: String): AiInsightsResponse {
    val obj = JSONObject(text)
    val title = obj.optString("title", "AI Insights")
    val summary = obj.optString("summary", "")
    val suggestionsJson = obj.optJSONArray("suggestions") ?: JSONArray()
    val suggestions = buildList {
        for (i in 0 until suggestionsJson.length()) {
            val s = suggestionsJson.optString(i).trim()
            if (s.isNotBlank()) add(s)
        }
    }
    return AiInsightsResponse(title = title, summary = summary, suggestions = suggestions)
}

private fun AiInsightsRequest.toGeminiGenerateContentJson(): JSONObject {
    val prompt = buildPromptForGemini(this)
    val isGoals = kind == "goals"
    return JSONObject()
        .put(
            "contents",
            JSONArray().put(
                JSONObject()
                    .put("role", "user")
                    .put("parts", JSONArray().put(JSONObject().put("text", prompt)))
            )
        )
        .put(
            "generationConfig",
            JSONObject()
                .put("temperature", if (isGoals) 0.25 else 0.4)
                .put("maxOutputTokens", if (isGoals) 140 else 220)
                .put("responseMimeType", "application/json")
                .put(
                    "responseSchema",
                    JSONObject()
                        .put("type", "OBJECT")
                        .put(
                            "properties",
                            JSONObject()
                                .put("title", JSONObject().put("type", "STRING"))
                                .put("summary", JSONObject().put("type", "STRING"))
                                .put(
                                    "suggestions",
                                    JSONObject()
                                        .put("type", "ARRAY")
                                        .put("items", JSONObject().put("type", "STRING"))
                                )
                        )
                        .put("required", JSONArray().put("title").put("summary").put("suggestions"))
                )
        )
}

private fun parseGeminiResponse(text: String): AiInsightsResponse {
    // Gemini API shape: { candidates: [ { content: { parts: [ { text } ] } } ] }
    val obj = JSONObject(text)
    val candidates = obj.optJSONArray("candidates") ?: JSONArray()
    if (candidates.length() == 0) return AiInsightsResponse("AI Insights", "", emptyList())
    val content = candidates.optJSONObject(0)?.optJSONObject("content")
    val parts = content?.optJSONArray("parts") ?: JSONArray()
    val combined = buildString {
        for (i in 0 until parts.length()) {
            append(parts.optJSONObject(i)?.optString("text", "") ?: "")
        }
    }.trim()

    // We ask for JSON-only output; if it fails, fall back to summary text.
    val parsed = runCatching { JSONObject(extractFirstJsonObject(combined)) }.getOrNull()
    if (parsed == null) {
        return AiInsightsResponse(title = "AI Insights", summary = combined.take(500), suggestions = emptyList())
    }
    val title = parsed.optString("title", "AI Insights")
    val summary = parsed.optString("summary", "")
    val suggestionsJson = parsed.optJSONArray("suggestions") ?: JSONArray()
    val suggestions = buildList {
        for (i in 0 until suggestionsJson.length()) {
            val s = suggestionsJson.optString(i).trim()
            if (s.isNotBlank()) add(s)
        }
    }
    return AiInsightsResponse(title = title, summary = summary, suggestions = suggestions)
}

private fun extractFirstJsonObject(text: String): String {
    val trimmed = text.trim()
    val start = trimmed.indexOf('{')
    val end = trimmed.lastIndexOf('}')
    return if (start >= 0 && end > start) trimmed.substring(start, end + 1) else trimmed
}

private fun buildPromptForGemini(req: AiInsightsRequest): String {
    val snap = req.snapshot
    return """
You are ${req.assistantName}, a personal finance analyst.
Write concise, specific guidance for the user.

Return ONLY valid JSON with exactly:
{
  "title": string,
  "summary": string,
  "suggestions": string[]
}

Context:
- kind: ${req.kind}
- userName: ${req.userName}
- yearMonth: ${snap.yearMonth}
- incomeTotal: ${snap.incomeTotal}
- expenseTotal: ${snap.expenseTotal}
- topExpenseCategories: ${snap.topExpenseCategories.joinToString(prefix = "[", postfix = "]") { """{"categoryName":"${it.categoryName}","total":${it.total}}""" }}
- dailyExpenses: ${snap.dailyExpenses.joinToString(prefix = "[", postfix = "]") { """{"day":"${it.day}","total":${it.total}}""" }}
- extraContext: ${req.extraContext.ifBlank { "none" }}

Guidelines:
- If kind="welcome": make it encouraging and include 2-3 action-oriented suggestions.
- If kind="analytics": focus on trends/anomalies and 3-5 suggestions.
- If kind="goals": evaluate feasibility of each goal, suggest realistic monthly steps, and mention risk level.
- Keep summary brief (1-2 sentences) and suggestions concise.
- Suggestions must be concrete ("Set Food budget to ฿X", "Move ฿Y to savings", "Reduce category Z by N%").
- No emojis. No extra keys.
""".trim()
}

