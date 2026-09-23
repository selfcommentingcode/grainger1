# vault

Local secrets for development. Not a real secrets manager — just a `secrets`
file plus a loader that copies its `KEY=VALUE` pairs into your environment
before you run the app.

> **Note for this demo:** `secrets` is intentionally **tracked in git** (plain
> text) by choice — it's a throwaway demo credential. Do **not** use this pattern
> for real secrets; put `vault/secrets` in `.gitignore` and never commit it.

## Files

| File                | Purpose                     |
|---------------------|-----------------------------|
| `secrets`           | Values. Edit this.          |
| `load-secrets.ps1`  | PowerShell loader.          |
| `load-secrets.sh`   | Bash loader.                |

## Format

```
# comments and blank lines are ignored
DB_USER=postgres
DB_PASSWORD=your-password
```

Each `KEY` becomes an environment variable. Everything after the first `=` is
the value.

## Use it

**PowerShell** — dot-source so the vars stay in your shell:

```powershell
. .\vault\load-secrets.ps1
cd backend; .\gradlew.bat bootRun
```

Or just run the launcher, which does both:

```powershell
.\run-backend.ps1
```

**Bash / Git Bash:**

```bash
source vault/load-secrets.sh
cd backend && ./gradlew bootRun
```

Or just run the launcher, which does both:

```bash
./run-backend.sh
```
