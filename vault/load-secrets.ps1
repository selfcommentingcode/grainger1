# Loads KEY=VALUE pairs from vault/secrets into the CURRENT session's env vars.
#
# Dot-source it so the variables persist in your shell:
#     . .\vault\load-secrets.ps1
#
# (Running it normally sets the vars only inside the script's own scope, which
#  is fine when another script dot-sources it before launching a child process.)

$secretsFile = Join-Path $PSScriptRoot 'secrets'
if (-not (Test-Path $secretsFile)) {
    Write-Warning "vault: no secrets file at '$secretsFile'. Copy secrets.example to secrets and fill it in."
    return
}

foreach ($raw in Get-Content -LiteralPath $secretsFile) {
    $line = $raw.Trim()
    if ($line -eq '' -or $line.StartsWith('#')) { continue }

    $idx = $line.IndexOf('=')
    if ($idx -lt 1) { continue }   # no key, or no '='

    $key = $line.Substring(0, $idx).Trim()
    $val = $line.Substring($idx + 1).Trim()

    # Strip one layer of surrounding matching quotes.
    if ($val.Length -ge 2 -and
        (($val.StartsWith('"') -and $val.EndsWith('"')) -or
         ($val.StartsWith("'") -and $val.EndsWith("'")))) {
        $val = $val.Substring(1, $val.Length - 2)
    }

    Set-Item -Path "Env:$key" -Value $val
    Write-Host "vault: set env $key"
}
