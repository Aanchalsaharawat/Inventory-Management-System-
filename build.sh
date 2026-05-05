#!/bin/bash
# =============================================================================
# build.sh – Builds and runs IMS with Maven
# Usage: bash build.sh
# =============================================================================

set -e

# ── Check Maven ───────────────────────────────────────────────────────────────
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven not found. Install Maven and add it to PATH."
    echo "Visit: https://maven.apache.org/download.cgi"
    exit 1
fi

cd "$(dirname "$0")"

# ── Build and run ─────────────────────────────────────────────────────────────
echo "Building with Maven…"
mvn clean compile exec:java
