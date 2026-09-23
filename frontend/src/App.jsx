import { useEffect, useState } from 'react'

const API_URL = 'http://localhost:8080/api/products'

export default function App() {
  const [products, setProducts] = useState([])
  const [name, setName] = useState('')
  const [error, setError] = useState('')

  async function loadProducts() {
    try {
      const res = await fetch(API_URL)
      if (!res.ok) throw new Error(`GET /api/products -> ${res.status}`)
      setProducts(await res.json())
      setError('')
    } catch (e) {
      setError(e.message ?? String(e))
    }
  }

  useEffect(() => {
    // Load the list once on mount. setState happens after the awaited fetch
    // (asynchronously), so this is not the synchronous set-state the rule warns about.
    // eslint-disable-next-line react/set-state-in-effect
    loadProducts()
  }, [])

  async function createProduct(event) {
    event.preventDefault()
    const trimmed = name.trim()
    if (!trimmed) return
    try {
      const res = await fetch(API_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name: trimmed }),
      })
      if (!res.ok) throw new Error(`POST /api/products -> ${res.status}`)
      setName('')
      await loadProducts()
    } catch (e) {
      setError(e.message ?? String(e))
    }
  }

  return (
    <main style={{ maxWidth: 480, margin: '2rem auto', fontFamily: 'sans-serif' }}>
      <h1>Products</h1>

      <form onSubmit={createProduct}>
        <input
          value={name}
          onChange={(e) => setName(e.target.value)}
          placeholder="Product name"
          aria-label="Product name"
        />
        <button type="submit">Add</button>
      </form>

      {error && <p style={{ color: 'crimson' }}>Error: {error}</p>}

      {products.length === 0 && !error ? (
        <p>No products yet.</p>
      ) : (
        <ul>
          {products.map((p) => (
            <li key={p.id}>{p.name}</li>
          ))}
        </ul>
      )}
    </main>
  )
}
