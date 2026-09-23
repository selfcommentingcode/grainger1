# Architecture

A minimal three-tier web application: a **React** single-page app talks over
HTTP/JSON to a **Kotlin / Spring Boot** REST API, which persists to
**PostgreSQL** via Spring Data JPA. Scope is intentionally tiny — list products
and create a product — so it stays easy to read and extend live.

The diagram below renders directly on GitHub. A printable copy is at
[`architecture.pdf`](architecture.pdf), rendered from
[`architecture.mmd`](architecture.mmd).

```mermaid
flowchart LR
    user(["User / Browser"])

    subgraph FE["Frontend · React + Vite · :3000"]
        app["App.jsx<br/>product list + create form"]
    end

    subgraph BE["Backend · Kotlin + Spring Boot · :8080"]
        ctrl["ProductController<br/>REST: GET / POST /api/products"]
        repo["ProductRepository<br/>Spring Data JPA"]
        ent["Product entity<br/>@Entity → products"]
    end

    subgraph DB["Database · PostgreSQL · :5432"]
        tbl[("productsdb<br/>products table")]
    end

    schema["schema.sql<br/>CREATE TABLE IF NOT EXISTS"]

    user -->|"HTTP"| app
    app -->|"fetch JSON over HTTP<br/>(CORS: localhost:3000 allowed)"| ctrl
    ctrl --> repo
    repo --> ent
    repo -->|"JDBC"| tbl
    schema -. "runs on backend startup" .-> tbl
```

## Components

| Layer | Tech | Responsibility |
|-------|------|----------------|
| **Frontend** | React + Vite (plain JS), dev server on `:3000` | Renders the product list and a create form; calls the API with `fetch`. |
| **Backend** | Kotlin + Spring Boot on `:8080` | `ProductController` exposes `GET`/`POST /api/products`; `ProductRepository` (Spring Data JPA) maps the `Product` entity to the table. `WebConfig` allows the `:3000` dev origin (CORS). |
| **Database** | PostgreSQL on `:5432`, database `productsdb` | Stores products. The `products` table is created on startup by `schema.sql` (idempotent `CREATE TABLE IF NOT EXISTS`); Hibernate does not manage the schema (`ddl-auto=none`). |

## Request flow — creating a product

```mermaid
sequenceDiagram
    actor U as User (browser)
    participant FE as React app (:3000)
    participant BE as ProductController (:8080)
    participant JPA as ProductRepository (JPA)
    participant DB as PostgreSQL (:5432)

    U->>FE: type "P1", click Add
    FE->>BE: POST /api/products {"name":"P1"}
    Note over BE: reject with 400 if name is blank
    BE->>JPA: save(Product(name="P1"))
    JPA->>DB: INSERT INTO products (name) VALUES ('P1')
    DB-->>JPA: generated id = 1
    JPA-->>BE: Product(id=1, name="P1")
    BE-->>FE: 201 Created {"id":1,"name":"P1"}
    FE->>BE: GET /api/products
    BE->>JPA: findAll()
    JPA->>DB: SELECT id, name FROM products
    DB-->>JPA: rows
    JPA-->>BE: List<Product>
    BE-->>FE: 200 OK [{"id":1,"name":"P1"}]
    FE-->>U: render updated list
```

## Regenerating the PDF

The PDF is rendered from [`architecture.mmd`](architecture.mmd) with
[mermaid-cli](https://github.com/mermaid-js/mermaid-cli) (no global install needed):

```bash
npx -y @mermaid-js/mermaid-cli -i docs/architecture.mmd -o docs/architecture.pdf -b white -f
```

`-f` fits the page to the diagram. mermaid-cli uses Puppeteer/Chromium to render;
if you already have Chrome installed and want to avoid Puppeteer downloading its
own Chromium, point it at yours:

```bash
# puppeteer.json: { "executablePath": "<path-to-chrome>", "args": ["--no-sandbox"] }
npx -y @mermaid-js/mermaid-cli -i docs/architecture.mmd -o docs/architecture.pdf -p puppeteer.json -b white -f
```
