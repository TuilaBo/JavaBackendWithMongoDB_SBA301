# Spring AI Learning Roadmap (for Orchid Project)

This roadmap is designed for your current Spring Boot backend and focuses on practical implementation, not theory-heavy study.

## 0) Goal and Ground Rules

- Goal: add AI features into the existing Orchid backend with clean architecture.
- Keep business logic and AI integration separate.
- Do not hardcode API keys in source code or committed properties files.
- Build small, testable increments.

---

## 1) Phase 1 - Core Spring AI Basics (1-2 days)

### Objectives

- Understand the minimum components:
  - `ChatModel`
  - `ChatClient`
  - `Prompt` (system + user)
  - `EmbeddingModel`
  - `VectorStore`
  - Advisors/Tools (advanced, later phase)

### Deliverables

- Can explain difference between:
  - basic chat endpoint
  - structured output endpoint
  - RAG endpoint

### Checklist

- [ ] Read Spring AI reference for your chosen provider.
- [ ] Decide first provider: OpenAI / Gemini / Azure OpenAI.
- [ ] Create a throwaway test endpoint to call model once.

---

## 2) Phase 2 - First Working AI Endpoint (1 day)

### Objective

Add a minimal endpoint in this backend:

- `POST /api/ai/chat`

Input:

```json
{
  "message": "Write a short orchid product description"
}
```

Output:

```json
{
  "status": 200,
  "message": "OK",
  "data": {
    "answer": "..."
  }
}
```

### Suggested Structure

- `controller/AiController.java`
- `service/AiService.java`
- `service/AiServiceImpl.java`
- DTOs:
  - `dto/ai/AiChatRequest.java`
  - `dto/ai/AiChatResponse.java`

### Checklist

- [ ] Add Spring AI dependency.
- [ ] Inject API key via environment variable.
- [ ] Return response wrapped in existing `ApiResponse`.
- [ ] Add basic logging for latency and provider errors.

---

## 3) Phase 3 - Prompt Engineering + Structured Output (1 day)

### Objective

Move from plain text output to strict structure for real business usage.

Example endpoint:

- `POST /api/ai/rewrite-description`

Input:

```json
{
  "orchidName": "Phalaenopsis White",
  "rawDescription": "hoa đẹp, trắng, giá tốt"
}
```

Output DTO:

- title
- shortDescription
- seoKeywords (list)

### Checklist

- [ ] Create strict output DTO.
- [ ] Add prompt rules to force concise format.
- [ ] Validate output fields (non-null / non-empty).

---

## 4) Phase 4 - RAG with Your Data (deep track)

### Objective

Answer questions grounded by your internal data/documents and improve answer quality per prompt by injecting relevant vector context.

### Scope

- Start with simple knowledge sources:
  - Orchid-related docs/markdown
  - Product/category metadata (name, description, tags)
  - Business rules (shipping, return, pricing policies)
- Add a "memory-like" retrieval path:
  - user preferences (tone/language)
  - previous approved outputs (optional)

### Components

- `EmbeddingModel`
- `VectorStore`
- `DocumentReader` / ingest pipeline
- Retrieval + prompt augmentation
- Re-ranking/filtering layer (later optimization)

### RAG Runtime Loop (what happens on each prompt)

1. User sends prompt to `POST /api/ai/qa`
2. Backend rewrites query (optional) to retrieval-friendly query
3. Embed query with `EmbeddingModel`
4. Search `VectorStore` with top-k + metadata filters
5. Build context block from matched chunks
6. Inject context into final prompt (system + context + user question)
7. Call model and return answer + source references
8. (Optional) store approved Q/A in knowledge base for future retrieval

### Ingestion Pipeline (what to build first)

1. Source docs folder (e.g. `src/main/resources/ai/knowledge/`)
2. Chunk strategy:
   - target chunk size: 300-600 tokens
   - overlap: 50-100 tokens
3. Metadata per chunk:
   - `source`
   - `topic`
   - `language`
   - `updatedAt`
4. Embed and upsert into vector store

### Prompt Strategy for RAG

- System prompt must enforce:
  - answer only from provided context
  - if missing context -> explicitly say insufficient knowledge
  - concise format and citation block
- Recommended prompt frame:
  - role/rules
  - retrieved context
  - user question
  - output contract (JSON or fixed sections)

### Vector Context "memory" strategy

- Do not blindly dump all history into prompt.
- Keep "memory" in vector store as retrievable items:
  - user style preference
  - validated business facts
  - approved templates
- Retrieve only top relevant items per request.

### Checklist

- [ ] Choose vector store strategy (local dev first).
- [ ] Build chunking and ingestion command/job.
- [ ] Build endpoint:
  - `POST /api/ai/qa`
- [ ] Ensure answer cites source chunk titles/ids.
- [ ] Add metadata filtering (language/topic/source).
- [ ] Add fallback behavior when no relevant chunks found.

### Query Optimization for AI Retrieval (must-learn)

- Retrieval tuning:
  - tune top-k (`3`, `5`, `8`) and compare answer quality
  - add score threshold to drop weak chunks
  - filter by metadata before vector search when possible
- Context quality:
  - deduplicate near-identical chunks
  - prefer newer chunks (`updatedAt`)
  - cap context tokens to avoid prompt dilution
- Cost/performance:
  - cache embeddings for repeated docs
  - cache retrieval results for repeated questions
  - use cheaper model for query rewrite; stronger model for final answer if needed
- Safety:
  - block prompt-injection instructions coming from retrieved docs
  - strip suspicious directives from context

---

## 5) Phase 5 - Tool Calling (2 days)

### Objective

Let model call your backend tools/services safely.

Example tools:

- find orchid by category
- search orchids by name
- get price range statistics

### Checklist

- [ ] Expose constrained tool functions only.
- [ ] Validate tool input server-side (never trust model input).
- [ ] Add timeout and max tool-call depth.

---

## 6) Phase 6 - Reliability, Cost, and Security (ongoing)

### Objective

Make AI endpoints production-friendly.

### Must-have Controls

- request timeout
- retry policy (limited, explicit)
- rate limiting
- token/cost logging
- prompt injection defense for RAG
- secret handling via env/secret manager

### Checklist

- [ ] Add per-endpoint timeout.
- [ ] Add provider error mapping in `GlobalExceptionHandler`.
- [ ] Log token usage and request cost metadata.
- [ ] Add redaction for sensitive text before prompt send.
- [ ] Add retrieval metrics dashboard (hit-rate, top-k quality, no-context rate).

---

## 7) Suggested Order for Your Current Project

Implement in this order:

1. `POST /api/ai/chat` (plain answer)
2. `POST /api/ai/rewrite-description` (structured output)
3. `POST /api/ai/qa` (RAG with small local corpus)
4. Tool-calling with existing Orchid services

Do not jump straight to RAG or agents before step 1 and 2 are stable.

---

## 8) Definition of Done per Phase

A phase is done only when:

- endpoint works from Swagger/Postman
- error handling is explicit
- no secrets are hardcoded
- code is separated by controller/service/dto
- one short markdown note documents:
  - what was built
  - how to test
  - known limitations

---

## 9) Quick Testing Script Ideas

- Valid request with expected output
- Empty message input
- Very long input
- Provider down / invalid key
- Timeout simulation

---

## 10) Notes for You (Practical)

- Keep prompts versioned as constants/templates, not scattered strings.
- Keep AI endpoints narrow and purpose-driven.
- Do not let AI directly mutate critical DB state in early phases.
- For any critical business action, AI should suggest; backend should decide.
- RAG quality is mostly a data/chunking/retrieval problem, not just a model problem.
- Improve retrieval first before changing model.

---

## Next Immediate Task

Start RAG Phase 4 in this order:

1. Create initial knowledge corpus folder.
2. Build one ingestion command to chunk + embed + store.
3. Implement `POST /api/ai/qa` with retrieval injection.
4. Return answer + source ids.
5. Tune top-k/threshold and log retrieval quality.

