#!/usr/bin/env bash
set -euo pipefail

image="${1:?usage: test-container.sh IMAGE PLATFORM}"
platform="${2:?usage: test-container.sh IMAGE PLATFORM}"
fixture="$(dirname "${BASH_SOURCE[0]}")/container-smoke-catalog.sql"
postgres_image=postgres:16.10-alpine
database_password="$(openssl rand -hex 24)"
catalog_password="$(openssl rand -hex 24)"
name="sodata-smoke-$(openssl rand -hex 8)"
database="${name}-db"
application="${name}-app"

cleanup() {
    local status=$?
    if (( status != 0 )); then
        docker logs "$database" >&2 || true
        docker logs "$application" >&2 || true
    fi
    docker rm -f -v "$application" "$database" >/dev/null 2>&1 || true
    docker network rm "$name" >/dev/null 2>&1 || true
}

wait_for() {
    local container="$1"
    local attempt
    shift
    for ((attempt = 0; attempt < 60; attempt++)); do
        if "$@" >/dev/null 2>&1; then
            return 0
        fi
        if [[ "$(docker inspect --format '{{.State.Running}}' "$container")" != true ]]; then
            break
        fi
        sleep 2
    done
    echo "$container did not become ready." >&2
    return 1
}

fetch_page() {
    local url="$1"
    if ! docker exec "$application" curl -fsS --max-time 30 "$url"; then
        echo "Sodata request failed: $url" >&2
        return 1
    fi
}

actual_platform="$(docker image inspect --format '{{.Os}}/{{.Architecture}}' "$image")"
if [[ "$actual_platform" != "$platform" ]]; then
    echo "Expected $image for $platform, got $actual_platform." >&2
    exit 1
fi

docker network create "$name" >/dev/null
trap cleanup EXIT
docker run --detach --platform "$platform" --name "$database" --network "$name" \
    --mount type=tmpfs,destination=/var/lib/postgresql/data \
    --env "POSTGRES_PASSWORD=$database_password" --env POSTGRES_DB=catalog "$postgres_image" >/dev/null
wait_for "$database" docker exec "$database" pg_isready -h 127.0.0.1 -U postgres -d catalog
docker exec --interactive "$database" psql -X --set ON_ERROR_STOP=1 \
    --set "catalog_password=$catalog_password" -U postgres -d catalog < "$fixture"

docker run --detach --platform "$platform" --name "$application" --network "$name" \
    --user 1001230000:0 \
    --env "DATENINFRASTRUKTUR_CATALOG_DATABASE_JDBCURL=jdbc:postgresql://$database:5432/catalog" \
    --env DATENINFRASTRUKTUR_CATALOG_DATABASE_USERNAME=di_public \
    --env "DATENINFRASTRUKTUR_CATALOG_DATABASE_PASSWORD=$catalog_password" "$image" >/dev/null

base=http://127.0.0.1:8080
wait_for "$application" docker exec "$application" curl -fsS --max-time 3 "$base/actuator/health/readiness"
catalog="$(fetch_page "$base/datasets")"
detail="$(fetch_page "$base/datasets/container-smoke")"
if [[ "$catalog" != *'Sodata Container Smoke Dataset'* || "$detail" != *'Read from the PostgreSQL smoke fixture.'* ]]; then
    echo "Sodata did not render the PostgreSQL fixture on the catalog and detail pages." >&2
    exit 1
fi
echo "PASS: $image ($platform) reached readiness and rendered its PostgreSQL dataset."
