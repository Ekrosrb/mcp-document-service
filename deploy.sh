#!/usr/bin/env bash
#
# Deploys hl-document-service and its Postgres with Docker Compose (deploy/docker-compose.yaml).
#
# Postgres is created on the first run, started when it is stopped and left untouched when it runs.
# The service image is built from the local sources and its container is recreated when the image changed.
#
# Settings come from environment variables, then deploy/.env, then the defaults described in README.md.
#
# Usage: ./deploy.sh

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
COMPOSE_FILE="$ROOT_DIR/deploy/docker-compose.yaml"
ENV_FILE="$ROOT_DIR/deploy/.env"
POSTGRES_SERVICE="postgres"
APP_SERVICE="document-service"
HEALTH_TIMEOUT_SECONDS=180

compose() {
  docker compose --file "$COMPOSE_FILE" "$@"
}

# Prints a setting from the environment, else from deploy/.env, else the given default.
setting() {
  local name="$1" default="$2" line value
  if [[ -n "${!name:-}" ]]; then
    printf '%s' "${!name}"
    return
  fi
  if [[ -f "$ENV_FILE" ]]; then
    line="$(grep -E "^${name}=" "$ENV_FILE" | tail -n 1 || true)"
    if [[ -n "$line" ]]; then
      value="${line#*=}"
      value="${value%$'\r'}"
      value="${value#[\"\']}"
      value="${value%[\"\']}"
      printf '%s' "$value"
      return
    fi
  fi
  printf '%s' "$default"
}

fail() {
  echo "ERROR: $*" >&2
  exit 1
}

require_docker() {
  command -v docker >/dev/null 2>&1 || fail "docker is not installed or not on PATH."
  docker compose version >/dev/null 2>&1 || fail "Docker Compose v2 (the 'docker compose' command) is required."
  docker info >/dev/null 2>&1 || fail "The Docker daemon is not reachable. Start Docker and try again."
}

project_version() {
  sed -n "s/^version = '\(.*\)'.*/\1/p" "$ROOT_DIR/build.gradle"
}

# Waits until the container reports a healthy status, printing its logs when it does not.
wait_until_healthy() {
  local container_id="$1" label="$2" deadline=$((SECONDS + HEALTH_TIMEOUT_SECONDS))
  until [[ "$(docker inspect --format '{{if .State.Health}}{{.State.Health.Status}}{{end}}' "$container_id")" == "healthy" ]]; do
    if (( SECONDS >= deadline )); then
      docker logs --tail 50 "$container_id" >&2 || true
      fail "$label did not become healthy within ${HEALTH_TIMEOUT_SECONDS}s."
    fi
    sleep 2
  done
}

# Stops when a container with the Postgres name exists outside this Compose project.
reject_foreign_postgres() {
  local name project
  name="$(setting POSTGRES_CONTAINER_NAME postgres-document-service)"
  if ! docker container inspect "$name" >/dev/null 2>&1; then
    return
  fi
  project="$(docker container inspect --format '{{index .Config.Labels "com.docker.compose.project"}}' "$name")"
  fail "A container named '$name' already exists but belongs to Compose project '${project:-none}'.
       It is probably an older deployment. Its data stays in the data directory, so remove only the container:
         docker rm -f $name
       Then run this script again."
}

# Creates Postgres when it does not exist, starts it when it is stopped, and keeps it when it runs.
ensure_postgres() {
  local container_id state
  container_id="$(compose ps --all --quiet "$POSTGRES_SERVICE")"
  if [[ -z "$container_id" ]]; then
    reject_foreign_postgres
    echo "Postgres is not deployed yet, creating it."
    compose up --detach --wait --wait-timeout "$HEALTH_TIMEOUT_SECONDS" "$POSTGRES_SERVICE"
    return
  fi

  state="$(docker inspect --format '{{.State.Status}}' "$container_id")"
  case "$state" in
    running) echo "Postgres is already running." ;;
    paused)
      echo "Postgres is paused, resuming it."
      docker unpause "$container_id" >/dev/null
      ;;
    *)
      echo "Postgres is $state, starting it."
      docker start "$container_id" >/dev/null
      ;;
  esac
  wait_until_healthy "$container_id" "Postgres"
}

deploy_service() {
  echo "Building and deploying hl-document-service $SERVICE_VERSION."
  if ! compose up --detach --build --no-deps --wait --wait-timeout "$HEALTH_TIMEOUT_SECONDS" "$APP_SERVICE"; then
    compose logs --tail 100 "$APP_SERVICE" >&2 || true
    fail "hl-document-service did not start."
  fi
}

print_summary() {
  local port
  port="$(setting SERVICE_PORT 55535)"
  cat <<EOF

hl-document-service $SERVICE_VERSION is running.
  MCP endpoint: http://<host>:$port/mcp
  REST API:     http://<host>:$port/api/v1
  Swagger UI:   http://<host>:$port/swagger-ui/index.html
EOF
}

main() {
  require_docker

  SERVICE_VERSION="$(setting SERVICE_VERSION "$(project_version)")"
  export SERVICE_VERSION

  if [[ "$(setting DEPLOY_POSTGRES true)" == "true" ]]; then
    ensure_postgres
  else
    [[ "$(setting DB_HOST postgres)" != "postgres" ]] \
      || fail "DEPLOY_POSTGRES is false, so DB_HOST must point to your Postgres server."
    echo "DEPLOY_POSTGRES is false, using the Postgres at $(setting DB_HOST postgres):$(setting DB_PORT 5432)."
  fi

  deploy_service
  print_summary
}

main "$@"
