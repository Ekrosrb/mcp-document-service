# CLAUDE.md

## 1. Think Before Coding

State assumptions. Surface tradeoffs. Don't hide confusion.

- Make assumptions explicit; ask when uncertain.
- If a request has multiple valid interpretations, present them — don't pick silently.
- If a simpler approach exists, propose it. Push back when warranted.
- If something is unclear, stop and name what's confusing.

## 2. Calibrated Confidence

Distinguish verified knowledge from pattern-matched recall. Confident hallucination is the primary failure mode.

- Treat training-data recall as a hypothesis, not a fact — especially for API signatures, library behavior, config keys, and version-specific syntax.
- Use hedged language ("likely," "I believe," "verify this") when uncertain. Don't bury doubt in confident prose.
- Fabricated functions, invented options, and plausible-but-wrong syntax are common failure modes. If you can't point to a source, you don't know it — search (§8) or ask.
- A correct "I don't know" beats a confident wrong answer.
- Confidence must come from evidence (docs, the codebase, prior messages), not from fluency.

Test: Can you cite a doc, file, or prior message that justifies this claim? If not, flag it or verify before stating it.

## 3. Simplicity First

Minimum code that solves the stated problem. Nothing speculative.

- No features beyond what was asked.
- No abstractions for single-use code.
- No configurability or flexibility that wasn't requested.
- No error handling for impossible scenarios.
- If 200 lines could be 50, rewrite.

Test: "Would a senior engineer call this overcomplicated?" If yes, simplify.

## 4. Surgical Changes

Touch only what's required. Clean up only what your changes broke.

When editing existing code:
- Don't "improve" adjacent code, comments, or formatting.
- Don't refactor what isn't broken.
- Match existing style, even if you'd do it differently.
- If you spot unrelated dead code, mention it — don't delete it.

Orphan cleanup:
- Remove imports/variables/functions made unused **by your changes**.
- Leave pre-existing dead code alone unless explicitly asked.

Test: Every changed line traces directly to the user's request.

## 5. Goal-Driven Execution

Define verifiable success criteria. Loop until they pass.

Convert tasks to checkable goals:
- "Add validation" → write tests for invalid inputs, make them pass.
- "Fix the bug" → write a reproducing test, make it pass.
- "Refactor X" → tests pass before and after.

For multi-step work, state the plan upfront:

[Step] → verify: [check]
[Step] → verify: [check]
[Step] → verify: [check]

Strong criteria enable independent iteration; weak criteria ("make it work") force constant clarification.

## 6. Git Operations: Read-Only

**Read-only git commands are allowed:** `status`, `diff`, `log`, `show`, `blame`, `ls-files`, `grep` and similar commands that only inspect the repository.

**Every git command that writes is forbidden — no exceptions.** This includes `add`, commits, branches, checkouts, merges, rebases, tags, pushes, pulls, fetches, stashes, resets, restores, cherry-picks, `update-index`, and config changes. The user commits.

## 7. Project Context

Spring Boot service storing documents and agent memories in Postgres, exposed over REST and as an MCP server for AI agents.

- **Stack:** Java 26, Spring Boot 4.1.1, Spring AI MCP Server 2.0.0 (WebMVC, streamable HTTP), Hibernate 7.4.5, Liquibase 5.0.3, PostgreSQL 18 (`pgvector/pgvector:pg18`), MapStruct 1.6.3, Lombok 1.18.48, Jackson 3.2.3, JToon 3.0.0, Gradle 9.8.1
- **Type:** HTTP service + MCP server, deployed with Docker Compose
- **Task scope:** `feature` — adding to existing codebase
- Released as `1.0.0`. MCP tool names, parameters and defaults are a contract: additive changes only; renames, removals and default changes are breaking.
- No auth by design (personal homelab use). Do not add auth unless asked.

### 7.1 Architecture

- Package root `com.ekros.hl.doc`: `controller` (REST, `api/` interfaces, `ExceptionHandlerController`) → `service` → `repository` (Spring Data JPA, `specification/` for search).
- `mcp/tools`: `@McpTool` methods map MCP requests to REST models via `DocumentMapper`, call the same services, return TOON via `ToonConverter`.
- `mcp/McpDescriptions`: every agent-facing tool and parameter text. Server instructions live in `application.yaml` (`spring.ai.mcp.server.instructions`).
- Search: `DocumentSearchSpecifications` — filters, `websearch_to_tsquery('english')` on a trigger-maintained `search_vector` (GIN), `ts_rank` order, OR fallback when all-words matching finds nothing, superseded entries hidden by default, paged (default 10, MCP max 50).
- Recall (MCP tool `recall`, no REST endpoint): `DocumentServiceImpl.recall` — any word of the task text, other projects' entries left out when `projectKey` is given, then relations followed both ways up to `depth` (default 1, max 2); `limit` caps matches and linked entries separately (default 10, max 50); superseded entries left out.
- Relations: `document_relations` (source → target, typed). Write access (`allowMcpWrite`) is checked on the source only.
- Schema: Liquibase formatted SQL in `src/main/resources/db/changelog/changes/`, registered in `db.changelog-master.yaml`.
- Deploy: `Dockerfile` (multi-stage, JDK 26 build → JRE 26 runtime), `deploy/docker-compose.yaml` (Postgres + service), `deploy.sh`. Runtime settings are environment variables with defaults in `application.yaml`.

### 7.2 Conventions

- Tests: Cucumber ITs in `hl-document-service-IT` (`./gradlew :hl-document-service-IT:test`, Docker required; or `hl-document-service-IT/run-it.sh`). Every change ships with IT coverage; the suite must pass.
- IT layout: `tests/<group>/<subgroup>/` with one `.feature`, `params/`, `input/`, `expected/{responses,database}/`. Data stays beside its feature; reuse fixtures within a feature instead of duplicating.
- Fixtures are valid JSON. Variables are `"${name}"` tokens declared with type in `params/params.json`, generated per scenario. No hard-coded ids; every scenario is parallel-safe and cleans up after itself.
- Shared steps only in `CommonSteps`, `DatabaseSteps`, `McpSteps`. IT ports: 43555–43999.
- Every MCP tool and parameter needs a description in `McpDescriptions`; an IT test fails otherwise.
- Optional `@McpToolParam(required = false)` parameters use wrapper types (`Boolean`, `Integer`), never primitives: an omitted parameter arrives as null and a primitive throws a NullPointerException.
- JToon 3.0.0 decodes an empty array inside a list item as the string `"[]"`. When an expected MCP result has such a field, assert the exact TOON text (`.toon` fixture) instead of decoded JSON.
- Dependency versions live only in `gradle/libs.versions.toml`. Reference them via `libs.*`; never inline a version in `build.gradle`.
- New runtime setting: environment variable with a default in `application.yaml`, plus `deploy/docker-compose.yaml`, `deploy/.env.example` and the README configuration table.
- New build input (file or directory the Gradle build reads): whitelist it in `.dockerignore`, or the image build fails.
- `README.md` documents MCP tools, REST endpoints, search and recall behaviour and configuration. Update it when any of these change.
- Ids are `BIGINT GENERATED ALWAYS AS IDENTITY`; Postgres enums map with `@JdbcTypeCode(SqlTypes.NAMED_ENUM)`.
- Comments: class/method Javadoc only, describe what not how. No line comments, no TODOs.
- Line endings: `*.sh` and `gradlew` LF, `*.bat` CRLF (`.gitattributes`).

### 7.3 File-Scope Boundaries

- `deploy/document/` — live Postgres data directory. Never read, modify or delete.
- `deploy/.env` — local deployment settings and credentials. Never read or modify; edit `deploy/.env.example` instead.
- Existing Liquibase changesets in `db/changelog/changes/` — never edit; applied checksums would break startup. Every schema change is a new numbered changeset.
- `build/` — generated output (incl. MapStruct impls). Read for diagnosis only, never edit.

### 7.4 Forbidden Operations

- `./deploy.sh` and any `docker compose` command against `deploy/docker-compose.yaml` — never run. They change the live homelab deployment and its database. Use the IT stack in `hl-document-service-IT/docker/` instead.
- Any command against the deployed database (port 5445): no drops, no manual SQL, no `pg_dump` restores.

## 8. Web Research

**Search instead of relying on memory when the answer concerns:**
- Library/framework versions, API signatures, config options, or syntax
- Error messages, deprecations, or breaking changes
- Anything qualified as "current," "latest," "recommended," or "best practice"
- Third-party service behavior (auth flows, rate limits, response schemas)
- Package availability, maintainership, or fork status

**Source priority:**
1. Official docs for the exact version in use
2. Official release notes / changelog
3. Maintainer's repo (issues, discussions, README)
4. Reputable secondary sources — cross-check only

**Pre-code self-check:**
- Verified signature/option against docs for the target version?
- Still recommended, or superseded?
- Anything changed in the last 12 months I might have missed?

**Default stance:** Treat training-data recall as a hypothesis. If one search would confirm or refute it, search.
