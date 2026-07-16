# AI — Chat Gemini (SSE)

API Spring Boot com chat via **Google Gemini**, respondendo em **Server-Sent Events (SSE)**.

Escopo atual: **somente chat**. RAG, embeddings e Postgres ficam para implementação futura (código de referência em `src/future/rag-features.txt`).

## Stack

- Java 21
- Spring Boot 4.1
- Spring AI 2.0 (Google GenAI)
- Spring WebFlux
- Docker

## Pré-requisitos

- Docker Desktop
- Chave da API Gemini ([Google AI Studio](https://aistudio.google.com/apikey))

## Configuração

### 1. Variáveis de ambiente

Crie um arquivo `.env` na raiz do projeto:

```env
GOOGLE_API_KEY=sua_chave_aqui
```

> A cota é **por projeto Google Cloud**, não por chave. Se aparecer erro de quota, crie a chave em um **projeto novo** no AI Studio.

### 2. Subir a API

```powershell
docker compose up -d --build api
```

Após alterar o `.env`:

```powershell
docker compose up -d --force-recreate api
```

### 3. Verificar se subiu

```powershell
docker compose logs api --tail 5
```

Deve aparecer `Started AiApplication` na porta `8080`.

## Endpoint

### `POST /api/chat`

Envia uma mensagem e recebe a resposta do Gemini em stream SSE.

| Item | Valor |
|------|-------|
| URL | `http://localhost:8080/api/chat` |
| Request `Content-Type` | `application/json` |
| Request `Accept` | `text/event-stream` |
| Response `Content-Type` | `text/event-stream` |

#### Body (JSON)

```json
{
  "message": "Diga olá em português"
}
```

#### Eventos SSE

| Evento | Significado |
|--------|-------------|
| `chunk` | Pedaço da resposta do Gemini |
| `done` | Stream finalizado com sucesso |
| `error` | Falha (quota, chave inválida, etc.) |

Exemplo de resposta:

```
event: chunk
data: {"type":"chunk","content":"Olá"}

event: chunk
data: {"type":"chunk","content":"! Como posso ajudar?"}

event: done
data: {"type":"done","content":""}
```

Exemplo de erro (cota esgotada):

```
event: error
data: {"type":"error","content":"Cota do Gemini esgotada para este projeto..."}
```

## Teste no Postman

1. **Method:** `POST`
2. **URL:** `http://localhost:8080/api/chat`
3. **Headers:**
   - `Content-Type`: `application/json`
   - `Accept`: `text/event-stream`
4. **Body (raw / JSON):**

```json
{
  "message": "Diga olá em português"
}
```

Abra **View → Postman Console** para acompanhar os eventos em tempo real.

## Teste no terminal

```powershell
curl.exe -N -X POST http://localhost:8080/api/chat `
  -H "Content-Type: application/json" `
  -H "Accept: text/event-stream" `
  -d "{\"message\":\"Diga ola em portugues\"}"
```

## Desenvolvimento local (sem Docker)

Requer **JDK 21** (o Maven local precisa apontar para Java 21).

```powershell
./mvnw spring-boot:run
```

Defina `GOOGLE_API_KEY` no ambiente ou no `.env` carregado pela sua IDE.

## Estrutura do projeto

```
src/main/java/wrs/ai/
├── controller/ChatController.java   # POST /api/chat (SSE)
├── service/ChatService.java         # stream via ChatModel
├── dto/ChatRequest.java
├── dto/ChatStreamEvent.java
├── config/                          # Google GenAI client + retry
└── exception/                       # tratamento de erros do Gemini

src/future/rag-features.txt          # código comentado para RAG futuro
```

## Problemas comuns

### Erro de cota (`429` / `limit: 0`)

A chave autentica, mas o Google bloqueia a geração. Soluções:

1. Criar API key em **projeto novo** no AI Studio
2. Vincular billing ao projeto (destrava cota em muitos casos)
3. Aguardar reset da cota diária

### Chave inválida (`API_KEY_INVALID`)

Verifique se `GOOGLE_API_KEY` está correta no `.env` e se o container foi recriado após a mudança.

## Roadmap

- [ ] RAG com documentos Markdown
- [ ] Embeddings + pgvector
- [ ] Busca semântica
- [ ] Postgres no `docker-compose.yml`

Código de referência para essas features: `src/future/rag-features.txt`.
