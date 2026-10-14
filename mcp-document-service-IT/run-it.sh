#!/usr/bin/env bash
#
# Runs the integration tests end to end:
#   1. starts the IT infrastructure with Docker Compose,
#   2. recreates the IT database,
#   3. runs the Cucumber suite, which starts the service with the "it" profile,
#   4. drops the IT database and stops the containers.
#
# The Postgres volume is kept between runs, so the next start skips the cluster initialisation.
#
# Usage: run-it.sh [--clean]
#   --clean   remove the containers and the Postgres volume when the run ends

set -euo pipefail

MODULE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(dirname "$MODULE_DIR")"
PROJECT_NAME="mcp-document-service-it"
SERVICE="postgres"
DATABASE="document-db"
DATABASE_USER="admin"

CLEAN=false
for argument in "$@"; do
  case "$argument" in
    --clean) CLEAN=true ;;
    *) echo "Unknown argument: $argument" >&2; exit 2 ;;
  esac
done

compose() {
  docker compose -p "$PROJECT_NAME" -f "$MODULE_DIR/docker/docker-compose.yaml" "$@"
}

drop_database() {
  compose exec -T "$SERVICE" dropdb -U "$DATABASE_USER" --if-exists --force "$DATABASE"
}

stop_infrastructure() {
  if [ "$CLEAN" = true ]; then
    compose down -v
  else
    drop_database || true
    compose stop
  fi
}

trap stop_infrastructure EXIT

compose up -d --wait
drop_database
compose exec -T "$SERVICE" createdb -U "$DATABASE_USER" "$DATABASE"

cd "$ROOT_DIR"
./gradlew :mcp-document-service-IT:test -PexternalInfrastructure
