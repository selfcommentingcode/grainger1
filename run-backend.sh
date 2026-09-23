#!/usr/bin/env bash
# Convenience launcher (macOS / Linux): load local secrets from the vault, then
# start the backend. Mirrors run-backend.ps1 for Windows.
#
#     ./run-backend.sh
set -euo pipefail

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]:-$0}")" && pwd)"

# Load DB_USER / DB_PASSWORD (etc.) into this shell; gradlew inherits them.
# shellcheck source=vault/load-secrets.sh
source "$script_dir/vault/load-secrets.sh"

cd "$script_dir/backend"
./gradlew bootRun
