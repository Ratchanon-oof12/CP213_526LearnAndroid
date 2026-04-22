## FinancialAssistant AI Proxy (Cloudflare Worker)

This is a **minimal proxy** so the Android app can use a hosted Gemma model **without shipping API keys in the APK**.

### What it does

- Android app POSTs to `POST /insights` with an aggregated spending snapshot.
- Worker calls Google’s Generative Language API (Gemma model) using a secret key stored in Worker secrets.
- Worker returns JSON:
  - `title`
  - `summary`
  - `suggestions[]`

### Setup (quick)

1. Install Wrangler and login.

```bash
npm i -g wrangler
wrangler login
```

2. Create the secret:

```bash
wrangler secret put GOOGLE_AI_API_KEY
```

Use the API key from Google AI Studio (Gemini API).

3. Deploy:

```bash
wrangler deploy
```

4. Copy the deployed base URL and set it in `app/build.gradle.kts`:

```kotlin
buildConfigField("String", "AI_PROXY_BASE_URL", "\"https://your-worker.yourname.workers.dev\"")
```

### Notes

- This worker is intentionally small and strict: it only accepts the fields your app sends and only returns the shape the app expects.
- If you want to switch models, update `MODEL` in `wrangler.toml` (default is `gemma-4-26b-a4b-it`).

