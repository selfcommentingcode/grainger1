# Convenience launcher: load local secrets from the vault, then start the backend.
# The dot-source below sets env vars (DB_USER, DB_PASSWORD, ...) in this script's
# scope; the gradlew child process inherits them.
#
#     .\run-backend.ps1

. "$PSScriptRoot\vault\load-secrets.ps1"
Set-Location "$PSScriptRoot\backend"
.\gradlew.bat bootRun
