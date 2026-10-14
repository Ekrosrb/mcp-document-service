# hl-document-service

A shared store of documents and memories for AI agents and people. Agents use it through MCP
(Model Context Protocol), and people through a REST API. Agents can find what they need, read it,
save what they learn and link related entries, so knowledge outlives a single conversation.

Data is kept in Postgres. Search is English full-text search, ranked by relevance.

## What it stores

Every entry has a **kind**:

| Kind       | Meaning                                                                 |
|------------|-------------------------------------------------------------------------|
| `DOCUMENT` | Long-form reference material: a guide, spec or reference. Usually written by people. |
| `MEMORY`   | A short note that should outlive a conversation. Usually written by agents. |

and a **scope**:

| Scope     | Meaning                                                    |
|-----------|------------------------------------------------------------|
| `GLOBAL`  | Applies to every project.                                  |
| `PROJECT` | Applies to one project; `projectKey` is required. An unknown key creates the project. |
| `AGENT`   | Notes of one agent, not tied to a project.                 |

A memory also has a **memory type**, which tells the reader how far to trust it. The default is `OBSERVATION`.

| Memory type   | Meaning                                              |
|---------------|------------------------------------------------------|
| `DECISION`    | A choice made and why. Follow it.                    |
| `RULE`        | How the user or project wants things done. Always apply it. |
| `FACT`        | Verified. Rely on it.                                |
| `OBSERVATION` | Noticed but not verified. Check before relying on it. |
| `PROCEDURE`   | Steps that worked. Reuse them.                       |
| `PITFALL`     | Something that failed or breaks. Avoid it.           |
| `STATUS`      | Where work stands. May be out of date.               |

Entries can be **related**. A relation reads "source `<type>` target":

| Relation type  | Meaning                                                        |
|----------------|----------------------------------------------------------------|
| `DEPENDS_ON`   | The source cannot be understood without the target.            |
| `SUPERSEDES`   | The source replaces the target. Search hides the target by default. |
| `EXTENDS`      | The source adds detail to the target.                          |
| `PART_OF`      | The source is a section of the target.                         |
| `DERIVED_FROM` | The source was produced from the target.                       |
| `RELATED_TO`   | Same topic, no stronger link.                                  |

**Write access from MCP.** Each entry has an `allowMcpWrite` flag.
- Entries created through MCP get `true`, so agents can update and remove them.
- Entries created through REST get `false` unless you set it.
- The flag can only be changed through REST.
- Creating or removing a relation needs write access to the source entry only.

**Deleting.** Deleting an entry only marks it as deleted (soft delete); this is what the MCP tool and
`DELETE /document/{id}` do.
- A deleted entry disappears from search, recall, `getDocument` and project counts. Links to and from it
  are hidden but kept, so a restore brings everything back.
- If a deleted entry replaced another one (`SUPERSEDES`), the replaced entry shows up in search again.
- Only the REST API can list, read, restore or permanently remove deleted entries.

## MCP tools

The MCP endpoint is `http://<host>:55535/mcp` (streamable HTTP). The server also sends short
usage instructions to the client when it connects.

| Tool                     | What it does                                                                 |
|--------------------------|------------------------------------------------------------------------------|
| `recall`                 | Gather context for a task: entries matching the task's words and the entries linked to them. Agents should call it before starting a task. |
| `searchMemories`         | Find memories by filters, memory type and/or a full-text query.              |
| `searchDocuments`        | Find documents by filters and/or a full-text query.                          |
| `getDocument`            | Read one document or memory by id, with its relations and the entries that reference it. |
| `createMemory`           | Store a short note: a decision, rule, fact, pitfall or status.               |
| `createDocument`         | Store a long-form document.                                                  |
| `updateDocument`         | Change fields of an entry; omitted fields stay as they are.                  |
| `removeDocument`         | Delete an entry (soft delete). Only a person can restore or permanently remove it. |
| `createDocumentRelation` | Link two entries.                                                            |
| `removeDocumentRelation` | Unlink two entries.                                                          |
| `getProjectsInfo`        | List project keys with the number of documents in each.                      |

Both create tools also take relations, so an entry can be stored and linked in one call.
Search results and `getDocument` are returned in TOON, a compact format that uses fewer tokens than JSON.
Every tool and parameter carries a description the agent can read.

### How search works

- Filters (scope, author, author name, project key, memory type) are combined with AND.
- The query is English full-text search over name, summary and content. Name matches weigh most,
  then summary, then content.
- It first looks for entries containing all the words. If there are none, it returns entries
  containing any of the words. Quoted phrases, `OR` and `-word` are supported; when you use them,
  that fallback is off.
- Results come most relevant first when a query is given, otherwise most recently updated first.
- Entries replaced by another one (the target of `SUPERSEDES`) are hidden unless `includeSuperseded` is set.
  Deleted entries never appear.
- Results are paged, 10 per page by default. MCP allows at most 50 per page.

### How recall works

- The task is plain text. Entries containing any of its words match, those with more of the words
  first. Search syntax in the task is treated as plain words.
- Matches ranked below 30% of the best match are left out, so an entry that shares only a common
  word with the task does not crowd out the strong matches.
- With a `projectKey`, entries of other projects are left out; global and agent entries stay.
- From the matches, recall follows relations in both directions for `depth` steps (default 1, at
  most 2) and returns those entries as `linked`, nearest first, then most recently updated first.
  Linked entries are not filtered by project, because a link is deliberate.
- Replaced entries (targets of `SUPERSEDES`) and deleted entries are left out of both lists.
- `limit` caps the matches and, separately, the linked entries (default 10, at most 50).
- Every entry comes with its summary and relations; the content only with `includeContent`.

## REST API

The base path is `/api/v1`. Swagger UI is at `http://<host>:55535/swagger-ui/index.html`.

| Method and path                                          | What it does                                    |
|----------------------------------------------------------|-------------------------------------------------|
| `POST /document`                                         | Create a document or memory.                    |
| `GET /document/{id}?includeContent=true`                 | Read one entry.                                 |
| `PUT /document/{id}`                                     | Update an entry, including `allowMcpWrite`.     |
| `DELETE /document/{id}`                                  | Delete an entry (soft delete).                  |
| `DELETE /document/{id}/permanent`                        | Remove an entry, deleted or not, with its content and relations for good. |
| `POST /document/{id}/restore`                            | Restore a deleted entry.                        |
| `GET /document/deleted?page=0&size=10`                   | List deleted entries, most recently deleted first. |
| `GET /document/deleted/{id}?includeContent=true`         | Read one deleted entry.                         |
| `POST /document/search?page=0&size=10`                   | Search; the filters go in the body.             |
| `POST /document/relation`                                | Create a relation.                              |
| `DELETE /document/relation/{documentId}/{relatedDocumentId}` | Remove a relation.                          |
| `GET /project/info?page=0&size=100`                      | List projects with their document counts.       |

There is no authentication. Run the service only on a network you trust.

## Prerequisites

To deploy with the script:
- Docker with BuildKit and Docker Compose v2 (the `docker compose` command). Any Docker Desktop or
  Docker Engine release from 2024 on has both.
- bash. On Windows, use Git Bash or WSL.

To build or run without Docker:
- JDK 26. The Gradle wrapper (`gradlew`) downloads Gradle itself.
- Postgres 18 with the `vector`, `hstore` and `uuid-ossp` extensions available. The
  `pgvector/pgvector:pg18` image has all three.

To run the integration tests: JDK 26 and Docker.

## Runbook

### Deploy

```bash
./deploy.sh
```

That is all a first deployment needs; every setting has a default. The script:
1. Checks that Docker and Docker Compose are available.
2. Ensures Postgres runs: creates it on the first run, starts it when it is stopped, and leaves it
   alone when it already runs. Postgres is never recreated, so its data and settings stay as they are.
3. Builds the service image from the local sources and starts the service. The first build takes a
   few minutes; later builds reuse the Gradle cache.
4. Waits until both containers are healthy and prints the URLs. If the service does not start, it
   prints the service logs and exits with an error.

Run the same command again to deploy a new version of the code.

If `./deploy.sh` reports "Permission denied", run `bash deploy.sh` or `chmod +x deploy.sh`.

### Connect an MCP client

The endpoint is `http://<host>:55535/mcp` (streamable HTTP, no authentication). Replace `localhost` in the
examples below with the host name or IP address of the machine running the service.

The client's name becomes the author name of the entries an agent creates.

**Claude Code, with the CLI.** `--scope user` makes the server available in every project, which suits a
shared memory store; leave it out to add it to the current project only.

```bash
claude mcp add --transport http --scope user hl-document-service http://localhost:55535/mcp
```

**Claude Code, by editing the configuration.** For every project, add the server to the top-level
`mcpServers` of `~/.claude.json` (on Windows `%USERPROFILE%\.claude.json`). For one project, put the same
block in `.mcp.json` in the project root instead; that file can be committed and shared.

```json
{
  "mcpServers": {
    "hl-document-service": {
      "type": "http",
      "url": "http://localhost:55535/mcp"
    }
  }
}
```

Keep the other content of `~/.claude.json` as it is and only add the `hl-document-service` entry. Restart
Claude Code, then run `/mcp` inside it to check that the server is connected and its tools are listed.

**Claude Desktop.** Its configuration file starts local servers as commands, so it reaches an HTTP server
through the [`mcp-remote`](https://github.com/geelen/mcp-remote) bridge, which needs Node.js. Edit
`claude_desktop_config.json` (Windows: `%APPDATA%\Claude\claude_desktop_config.json`, macOS:
`~/Library/Application Support/Claude/claude_desktop_config.json`):

```json
{
  "mcpServers": {
    "hl-document-service": {
      "command": "npx",
      "args": [
        "-y",
        "mcp-remote",
        "http://localhost:55535/mcp",
        "--transport",
        "http-only",
        "--allow-http"
      ]
    }
  }
}
```

`--allow-http` is needed because the service does not use HTTPS; use it only on a network you trust.
Quit Claude Desktop completely (on Windows from the system tray) and start it again to load the change.

### Day-to-day commands

Run these from the repository root.

| Task                     | Command                                                                 |
|--------------------------|-------------------------------------------------------------------------|
| Status                   | `docker compose -f deploy/docker-compose.yaml ps`                       |
| Service logs             | `docker compose -f deploy/docker-compose.yaml logs -f document-service` |
| Stop everything          | `docker compose -f deploy/docker-compose.yaml stop`                     |
| Start again              | `./deploy.sh`                                                           |
| Remove the containers    | `docker compose -f deploy/docker-compose.yaml down` (the data in `deploy/document/postgres` stays) |
| Back up the database     | `docker exec postgres-document-service pg_dump -U admin document-db > backup.sql` |
| Restore a backup         | `docker exec -i postgres-document-service psql -U admin -d document-db < backup.sql` (into an empty database) |

### Troubleshooting

- **"A container named 'postgres-document-service' already exists but belongs to Compose project ..."**
  An older deployment created the container from a different Compose project. Its data lives in the
  data directory, not in the container, so remove only the container with
  `docker rm -f postgres-document-service` and run `./deploy.sh` again.
- **The service does not become healthy.** The script prints its logs. The usual causes are wrong
  database credentials and an unreachable `DB_HOST`.

## Configuration

Every setting has a default, so nothing needs to be configured to start. To change a setting, either:
- copy `deploy/.env.example` to `deploy/.env` and edit the values there (`deploy/.env` is not committed), or
- set environment variables when running the script, for example `SERVICE_PORT=8080 ./deploy.sh`.

Environment variables take precedence over `deploy/.env`.

| Variable                  | Default                     | Meaning                                                        |
|---------------------------|-----------------------------|----------------------------------------------------------------|
| `DEPLOY_POSTGRES`         | `true`                      | Deploy Postgres with the service. Set `false` to use a Postgres running elsewhere. |
| `DB_HOST`                 | `postgres`                  | Host the service connects to. `postgres` is the bundled container. |
| `DB_PORT`                 | `5432`                      | Port the service connects to.                                  |
| `DB_NAME`                 | `document-db`               | Database name.                                                 |
| `DB_USERNAME`             | `admin`                     | Database user.                                                 |
| `DB_PASSWORD`             | `admin`                     | Database password.                                             |
| `POSTGRES_BIND_ADDRESS`   | `0.0.0.0`                   | Host address the bundled Postgres port is published on. `127.0.0.1` keeps it off the network. |
| `POSTGRES_PORT`           | `5445`                      | Host port of the bundled Postgres.                             |
| `POSTGRES_DATA_DIR`       | `./document/postgres`       | Where the bundled Postgres keeps its data. A relative path is relative to `deploy/`. |
| `POSTGRES_CONTAINER_NAME` | `postgres-document-service` | Name of the Postgres container.                                |
| `SERVICE_BIND_ADDRESS`    | `0.0.0.0`                   | Host address the service port is published on.                 |
| `SERVICE_PORT`            | `55535`                     | Host port of the service.                                      |
| `SERVICE_CONTAINER_NAME`  | `hl-document-service`       | Name of the service container.                                 |
| `SERVICE_IMAGE`           | `hl-document-service`       | Name of the built image. The tag is the project version.       |
| `JAVA_OPTS`               | empty                       | JVM options for the service, for example `-Xmx512m`.           |

Notes:
- **Database credentials** are applied by Postgres only when it creates its data directory, on the
  first deployment. Changing `DB_USERNAME` or `DB_PASSWORD` later does not change the existing
  database. Change them in Postgres first, then in the configuration.
- **External Postgres.** Set `DEPLOY_POSTGRES=false` and set `DB_HOST`, `DB_PORT`, `DB_NAME`,
  `DB_USERNAME` and `DB_PASSWORD`. The database must allow the `vector`, `hstore` and
  `uuid-ossp` extensions, and the user must be able to create them or they must already exist.
  `DB_HOST` is resolved inside the service container, so use a host name or IP address that the
  container can reach, not `localhost`.
- The service creates and updates its schema itself on startup (Liquibase).

When the service runs outside Docker, it reads `SERVER_PORT` (default `55535`) and `DB_HOST`,
`DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`. The defaults are `localhost`, `5445`,
`document-db`, `admin` and `admin`, which match the bundled Postgres.

## Build it yourself

Build the executable jar (needs JDK 26):

```bash
./gradlew bootJar
```

The jar is written to `build/libs/hl-document-service-<version>.jar`. Run it against a Postgres (see
Prerequisites), with the settings from the section above as environment variables:

```bash
DB_HOST=localhost DB_PORT=5445 java -jar build/libs/hl-document-service-1.0.0.jar
```

Build only the Docker image (needs only Docker; the build runs inside a container):

```bash
docker build -t hl-document-service:1.0.0 .
```

Start everything with Docker Compose directly, without the script:

```bash
docker compose -f deploy/docker-compose.yaml up -d --build
```

Unlike the script, this recreates Postgres when its settings changed. The data is kept, because it is in
the data directory.

Run the integration tests (needs JDK 26 and Docker; uses ports 43555–43556):

```bash
./gradlew :hl-document-service-IT:test
```

Dependency versions are kept in `gradle/libs.versions.toml`.
