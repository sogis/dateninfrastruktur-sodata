#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
BUCKET="${DATENINFRASTRUKTUR_BUCKET:-ch.so.datenportal}"
docker compose --profile tools run --rm aws s3 ls "s3://${BUCKET}/" --recursive
