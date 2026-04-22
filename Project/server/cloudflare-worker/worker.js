/**
 * Minimal AI proxy for FinancialAssistant.
 *
 * Exposes:
 *   POST /insights
 *
 * Env:
 *   - GOOGLE_AI_API_KEY (secret) from Google AI Studio
 *   - MODEL (var) e.g. "gemma-4-26b-a4b-it"
 */

const CORS_HEADERS = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Methods": "POST, OPTIONS",
  "Access-Control-Allow-Headers": "Content-Type, Authorization",
};

export default {
  async fetch(request, env) {
    const url = new URL(request.url);

    if (request.method === "OPTIONS") {
      return new Response(null, { status: 204, headers: CORS_HEADERS });
    }

    if (request.method !== "POST" || url.pathname !== "/insights") {
      return new Response("Not found", { status: 404, headers: CORS_HEADERS });
    }

    let payload;
    try {
      payload = await request.json();
    } catch {
      return json({ error: "Invalid JSON" }, 400);
    }

    const model = env.MODEL || "gemma-4-26b-a4b-it";
    const apiKey = env.GOOGLE_AI_API_KEY;
    if (!apiKey) {
      return json({ error: "Server missing GOOGLE_AI_API_KEY" }, 500);
    }

    const prompt = buildPrompt(payload);

    // Google AI Studio / Gemini API (Generative Language API v1beta).
    // For Gemma 4 26B A4B IT, model id is: gemma-4-26b-a4b-it
    const endpoint = `https://generativelanguage.googleapis.com/v1beta/models/${encodeURIComponent(
      model
    )}:generateContent?key=${encodeURIComponent(apiKey)}`;

    const upstreamResp = await fetch(endpoint, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        contents: [{ role: "user", parts: [{ text: prompt }] }],
        generationConfig: {
          temperature: 0.4,
          maxOutputTokens: 350,
        },
      }),
    });

    if (!upstreamResp.ok) {
      const errText = await upstreamResp.text().catch(() => "");
      return json(
        { error: "Upstream AI error", status: upstreamResp.status, details: errText.slice(0, 4000) },
        502
      );
    }

    const upstreamJson = await upstreamResp.json();
    const text = extractText(upstreamJson);

    // We ask the model to output JSON. If it fails, degrade gracefully.
    const parsed = safeParseJson(text);
    if (!parsed) {
      return json(
        {
          title: "AI Insights",
          summary: text?.slice(0, 500) || "",
          suggestions: [],
        },
        200
      );
    }

    return json(
      {
        title: String(parsed.title || "AI Insights"),
        summary: String(parsed.summary || ""),
        suggestions: Array.isArray(parsed.suggestions)
          ? parsed.suggestions.map((s) => String(s)).filter((s) => s.trim().length > 0).slice(0, 6)
          : [],
      },
      200
    );
  },
};

function json(obj, status = 200) {
  return new Response(JSON.stringify(obj), {
    status,
    headers: { "Content-Type": "application/json; charset=utf-8", ...CORS_HEADERS },
  });
}

function safeParseJson(text) {
  if (!text || typeof text !== "string") return null;
  const trimmed = text.trim();
  try {
    return JSON.parse(trimmed);
  } catch {
    // Try to extract the first JSON object if the model wrapped it in prose
    const start = trimmed.indexOf("{");
    const end = trimmed.lastIndexOf("}");
    if (start >= 0 && end > start) {
      try {
        return JSON.parse(trimmed.slice(start, end + 1));
      } catch {
        return null;
      }
    }
    return null;
  }
}

function extractText(upstreamJson) {
  const candidates = upstreamJson?.candidates;
  if (!Array.isArray(candidates) || candidates.length === 0) return "";
  const parts = candidates[0]?.content?.parts;
  if (!Array.isArray(parts)) return "";
  return parts.map((p) => p?.text || "").join("");
}

function buildPrompt(req) {
  const kind = req?.kind || "analytics";
  const userName = req?.userName || "there";
  const assistantName = req?.assistantName || "Financial Architect";
  const snap = req?.snapshot || {};

  // Very strict, JSON-only output contract.
  return `
You are ${assistantName}, a personal finance analyst.
Write concise, specific guidance.

Return ONLY valid JSON with exactly:
{
  "title": string,
  "summary": string,
  "suggestions": string[]
}

Context:
- kind: ${kind}
- userName: ${userName}
- yearMonth: ${snap.yearMonth || ""}
- incomeTotal: ${snap.incomeTotal ?? 0}
- expenseTotal: ${snap.expenseTotal ?? 0}
- topExpenseCategories: ${JSON.stringify(snap.topExpenseCategories || [])}
- dailyExpenses: ${JSON.stringify(snap.dailyExpenses || [])}

Guidelines:
- If kind="welcome": make it encouraging and include 2-3 action-oriented suggestions.
- If kind="analytics": focus on trends/anomalies and 3-5 suggestions.
- Suggestions must be concrete ("Set Food budget to ฿X", "Move ฿Y to savings", "Reduce category Z by N%").
- No medical/legal claims. No emojis. No extra keys.
`.trim();
}

