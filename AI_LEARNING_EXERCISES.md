# AI Learning Exercises (Orchid Project)

## Phase 1 - Retrieval and Prompt Fundamentals

1. Make `topK` configurable from `application.properties` instead of hardcoding `4`.
2. Log retrieved chunk sources for each QA request.
3. Force QA answer format to include source citations.

## Phase 2 - Retrieval Quality

4. Add relevance filtering (score threshold or fallback heuristic).
5. Compare at least two chunking strategies and record retrieval hit-rate changes.
6. Add duplicate protection in ingest flow.

## Phase 3 - Evaluation and Production Readiness

7. Build a QA benchmark set with at least 20 domain questions.
8. Measure correctness before and after retrieval/prompt tuning.
9. Persist vectors in database and verify data survives app restart.
10. Restrict `/api/ai/rag/ingest` to admin/operator role only.
