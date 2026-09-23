# Postman collection — Products API (E2E)

An end-to-end demonstration of the Products API with **embedded tests** that
assert each endpoint behaves correctly.

| File | Purpose |
|------|---------|
| `products-api.postman_collection.json`  | The requests + embedded tests |
| `products-api.postman_environment.json` | `baseUrl` (defaults to `http://localhost:8080`) |

## What it covers

The five requests run in order and share state via collection variables, so the
suite is self-contained and **baseline-relative** (it does not assume an empty
database — safe to re-run against a live one):

1. **List products (baseline)** — GET → 200, JSON array; records the starting count.
2. **Create 'P1'** — POST → 201, numeric `id`, `name == "P1"`; remembers the id.
3. **Create, trims whitespace** — POST `"  Widget  "` → 201, persisted as `"Widget"`.
4. **Reject blank name** — POST `"   "` → 400, nothing persisted.
5. **List reflects new products** — GET → 200; count is baseline **+2** (the blank
   one was rejected) and the `P1` product created in step 2 is present.

## Run it

**Prerequisite:** the backend must be running on `http://localhost:8080`
(see the repo root `README.md` — "Start backend").

### Headless (Newman)

From the repo root:

```bash
# no install needed — runs Newman via npx
npx newman run postman/products-api.postman_collection.json \
  -e postman/products-api.postman_environment.json
```

`baseUrl` is also baked into the collection, so `-e` is optional:

```bash
npx newman run postman/products-api.postman_collection.json
```

### In the Postman app

1. **Import** both JSON files (Import → Files).
2. Select the **"Products API — Local"** environment (top-right).
3. Open the collection → **Run** (Runner), or Send each request in order.
4. Check the **Test Results** tab on each response.

> Each run creates two products (`P1` and `Widget`). Because the assertions are
> baseline-relative, re-running always passes; the table simply grows.
