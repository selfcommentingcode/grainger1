#!/usr/bin/env bash
# Loads KEY=VALUE pairs from vault/secrets into the CURRENT shell's env vars.
#
# Source it so the variables persist in your shell:
#     source vault/load-secrets.sh

_vault_dir="$(cd "$(dirname "${BASH_SOURCE[0]:-$0}")" && pwd)"
_secrets_file="$_vault_dir/secrets"

if [ ! -f "$_secrets_file" ]; then
  echo "vault: no secrets file at '$_secrets_file'. Copy secrets.example to secrets and fill it in." >&2
  return 1 2>/dev/null || exit 1
fi

while IFS= read -r line || [ -n "$line" ]; do
  line="${line%$'\r'}"                       # strip CR if the file is CRLF
  case "$line" in
    ''|\#*) continue ;;                        # skip blank + comment lines
  esac
  case "$line" in
    *=*) ;;                                     # must contain '='
    *) continue ;;
  esac
  key="${line%%=*}"
  val="${line#*=}"
  key="$(printf '%s' "$key" | tr -d '[:space:]')"
  [ -z "$key" ] && continue
  # strip one layer of surrounding matching quotes
  case "$val" in
    \"*\") val="${val#\"}"; val="${val%\"}" ;;
    \'*\') val="${val#\'}"; val="${val%\'}" ;;
  esac
  export "$key=$val"
  echo "vault: set env $key"
done < "$_secrets_file"
