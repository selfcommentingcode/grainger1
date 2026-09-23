# Frontend — Products (React + Vite)

The React single-page app for the Products demo: it lists products and lets you
create one. Plain JavaScript, minimal styling (per the assignment). It calls the
backend REST API at `http://localhost:8080/api/products`.

See the repository [`README.md`](../README.md) for full setup, the architecture
diagram, and how the pieces fit together. Quick commands:

```bash
npm install     # first time only
npm run dev     # dev server on http://localhost:3000
npm run build   # production build to dist/
npm run lint    # oxlint
```

## Layout

- `src/App.jsx` — the whole UI: fetches and renders the list, plus the create form.
- `src/main.jsx` — React entry point.
- `src/index.css` — minimal global styles.
- `index.html` — Vite HTML entry (title, favicon, root element).

The dev server is pinned to port **3000** (`vite.config.js`, `strictPort: true`)
to match the backend's CORS allow-list and the assignment's verification URL.
