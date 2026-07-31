# Read Me First

### Reference Documentation

For further reference, please consider the following sections:

* [Gemini API key](https://aistudio.google.com/api-keys?project=gen-lang-client-0447965394)
* [Spring Google GenAI](https://docs.spring.io/spring-ai/reference/api/chat/google-genai-chat.html)
* [Gemini models](https://ai.google.dev/gemini-api/docs/models)
* [Spring AI examples](https://github.com/spring-projects/spring-ai/tree/main)

### Prerequisites

Before running the application, you must complete the configuration steps for both the Google
Calendar API and the Gemini AI engine:

* **Configure Google Calendar Credentials**: Go directly to your service account key page on
  the [Google Cloud Console](https://console.cloud.google.com/apis/credentials), and select **Add Key > Create new key (JSON)**.
  Open the downloaded file, copy its entire JSON string contents, and save it as an environment
  variable named `GOOGLE_CALENDAR_CREDENTIALS` on your system.
* **Share your Google Calendar**: Open Google Calendar in your browser. Go to your target calendar's
  **Settings and Sharing**, scroll down to **Share with specific people**, and add your service
  account's email address (e.g., `my-service-account@://gserviceaccount.com`) with **Make changes and manage sharing** permission.
* Also make sure the Time zone setting is set to the correct timezone.
* **Configure Gemini AI Key (when using Ollama, this is not needed)**: Obtain an API key from
  the [Google AI Studio API Keys Page](https://aistudio.google.com/api-keys). Save this key as an
  environment variable named `GEMINI_API_KEY` on your system.
* **Using Ollama or GenAI**: Change the ai to use by setting the 'spring.profiles.active' in the properties file.

### Whatsapp emulator (only needed if running outside docker)
git clone https://github.com/DonnC/wce-emulator
export BOT_WEBHOOK_URL="http://localhost:8081/webhook"

If not run already, run:
```bash
npm install
npm run postinstall
```
Otherwise:
```bash
npm run dev
```

Emulator should be available at http://localhost:8080/

### Ollama docker
See [Ollama](https://ollama.com/) for more info.

```bash
docker compose down
docker compose up -d --force-recreate
```

```bash
docker logs -f ollama-model-puller
```

