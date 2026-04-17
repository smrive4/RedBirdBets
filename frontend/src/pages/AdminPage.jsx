import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../features/auth/AuthContext'
import styles from './AdminPage.module.css'
import { API_BASE } from '../config'
const BASE = API_BASE

// const Styles.inputStyle = {
//   width: '100%',
//   padding: '10px 14px',
//   borderRadius: '8px',
//   border: '1px solid var(--color-border)',
//   fontSize: '14px',
//   fontFamily: 'inherit',
//   background: 'var(--color-surface-alt)',
//   color: 'var(--color-text-primary)',
//   boxSizing: 'border-box',
//   marginBottom: '12px',
// }

const btnStyle = {
  padding: '10px 20px',
  borderRadius: '8px',
  border: 'none',
  background: 'var(--color-primary)',
  color: '#fff',
  fontWeight: 'bold',
  fontSize: '14px',
  cursor: 'pointer',
  fontFamily: 'inherit',
}

const dangerBtn = {
  ...btnStyle,
  background: '#c0392b',
}

const secondaryBtn = {
  ...btnStyle,
  background: 'transparent',
  border: '1.5px solid var(--color-primary)',
  color: 'var(--color-primary)',
}

export default function AdminPage() {
  const { user } = useAuth()
  const navigate = useNavigate()

  const [markets, setMarkets] = useState([])
  const [form, setForm] = useState({
    title: '',
    description: '',
    closeTime: '',
    marketCategory: '',
  })
  const [error, setError] = useState(null)
  const [options, setOptions] = useState(['', ''])
  const [success, setSuccess] = useState(null)

  const showSuccess = (msg) => {
    setSuccess(msg)
    setTimeout(() => setSuccess(null), 3000)
  }

  const loadMarkets = () => {
    fetch(`${BASE}/api/markets`)
      .then((r) => r.json())
      .then(setMarkets)
  }

  useEffect(() => {
    loadMarkets()
  }, [])

  const handleCreate = async (e) => {
  e.preventDefault()
  setError(null)
  const filledOptions = options.map((o) => o.trim()).filter((o) => o !== '')
  if (filledOptions.length < 2) {
    setError('Please provide at least 2 options.')
    return
  }
  try {
    const res = await fetch(`${BASE}/api/markets?requesterId=${user.id}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        title: form.title,
        description: form.description,
        closeTime: form.closeTime,
        marketCategory: form.marketCategory,
        options: filledOptions.map((name) => ({ optionName: name })),
      }),
    })
    if (!res.ok) throw new Error(await res.text())
    setForm({ title: '', description: '', closeTime: '', marketCategory: '' })
    setOptions(['', ''])
    loadMarkets()
    showSuccess('Market created!')
  } catch (err) {
    setError(err.message)
  }
}

  const handleClose = async (id) => {
    try {
      const res = await fetch(
        `${BASE}/api/markets/${id}/close?requesterId=${user.id}`,
        {
          method: 'POST',
        }
      )
      if (!res.ok) throw new Error(await res.text())
      loadMarkets()
      showSuccess('Market closed!')
    } catch (err) {
      setError(err.message)
    }
  }

  const handleResolve = async (id, winningSide) => {
    try {
      const res = await fetch(
        `${BASE}/api/markets/${id}/resolve?winningOptionId=${winningSide}&requesterId=${user.id}`,
        { method: 'POST' }
      )
      if (!res.ok) throw new Error(await res.text())
      loadMarkets()
      showSuccess(`Market resolved! Winner: ${winningSide}`)
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <div
      style={{
        minHeight: '100vh',
        background: 'var(--color-bg)',
        fontFamily: "'Segoe UI', Arial, sans-serif",
      }}
    >
      <nav
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          padding: '16px 32px',
          background: 'var(--color-surface-alt)',
          borderBottom: '2px solid var(--color-primary)',
        }}
      >
        <div
          style={{
            fontSize: '22px',
            fontWeight: 'bold',
            color: 'var(--color-primary)',
            letterSpacing: '0.08em',
          }}
        >
          REDBIRDBETS — ADMIN
        </div>
        <button style={secondaryBtn} onClick={() => navigate('/dashboard')}>
          Back to Dashboard
        </button>
      </nav>

      <div style={{ maxWidth: '900px', margin: '0 auto', padding: '32px' }}>
        {error && (
          <div
            style={{
              background: '#fdecea',
              border: '1px solid #f5c6cb',
              borderRadius: '8px',
              padding: '12px 16px',
              marginBottom: '20px',
              color: '#c0392b',
              fontSize: '14px',
            }}
          >
            {error}
          </div>
        )}
        {success && (
          <div
            style={{
              background: '#eafaf1',
              border: '1px solid #a9dfbf',
              borderRadius: '8px',
              padding: '12px 16px',
              marginBottom: '20px',
              color: '#1e8449',
              fontSize: '14px',
            }}
          >
            {success}
          </div>
        )}

        <div
          style={{
            background: 'var(--color-surface)',
            borderRadius: '8px',
            padding: '24px',
            border: '1px solid var(--color-border)',
            marginBottom: '32px',
          }}
        >
          <h2
            style={{ margin: '0 0 20px', fontSize: '18px', fontWeight: '800' }}
          >
            Create New Market
          </h2>
          <form onSubmit={handleCreate}>
            <label className={styles.label}>Title</label>
            <input
              className={styles.inputStyle}
              value={form.title}
              onChange={(e) => setForm({ ...form, title: e.target.value })}
              required
            />
            <label className={styles.label}>Description</label>
            <input
              className={styles.inputStyle}
              value={form.description}
              onChange={(e) =>
                setForm({ ...form, description: e.target.value })
              }
            />
            <label className={styles.label}>Close Time</label>
            <input
              className={styles.inputStyle}
              type="datetime-local"
              value={form.closeTime}
              onChange={(e) => setForm({ ...form, closeTime: e.target.value })}
              required
            />
            <label className={styles.label}>Category</label>
            <select
              className={styles.inputStyle}
              value={form.marketCategory}
              onChange={(e) =>
                setForm({ ...form, marketCategory: e.target.value })
              }
              required
            >
              <option value="" disabled>
                Select a category
              </option>
              <option value="ACADEMICS">Academics</option>
              <option value="CAMPUS_LIFE">Campus Life</option>
              <option value="SPORTS">Sports</option>
              <option value="OTHER">Other</option>
            </select>
            <label className={styles.label}>Options (min. 2)</label>
            {options.map((opt, i) => (
              <div key={i} style={{ display: 'flex', gap: '8px', marginBottom: '8px' }}>
                <input
                  className={styles.inputStyle}
                  style={{ marginBottom: 0, flex: 1 }}
                  placeholder={`Option ${i + 1}`}
                  value={opt}
                  onChange={(e) => {
                    const updated = [...options]
                    updated[i] = e.target.value
                    setOptions(updated)
                  }}
                />
                {options.length > 2 && (
                  <button
                    type="button"
                    style={{ ...dangerBtn, padding: '8px 12px' }}
                    onClick={() => setOptions(options.filter((_, idx) => idx !== i))}
                  >
                    ✕
                  </button>
                )}
              </div>
            ))}
            <button
              type="button"
              style={{ ...secondaryBtn, marginBottom: '16px', fontSize: '13px', padding: '7px 14px' }}
              onClick={() => setOptions([...options, ''])}
            >
              + Add Option
            </button>
            <br />
            <button style={btnStyle} type="submit">
              Create Market
            </button>
          </form>
        </div>

        <h2
          style={{ fontSize: '18px', fontWeight: '800', marginBottom: '16px' }}
        >
          All Markets
        </h2>
        {markets.length === 0 ? (
          <p style={{ color: 'var(--color-text-muted)' }}>No markets yet.</p>
        ) : (
          markets.map((m) => (
            <div
              key={m.id}
              style={{
                background: 'var(--color-surface)',
                borderRadius: '8px',
                padding: '20px',
                border: '1px solid var(--color-border)',
                marginBottom: '12px',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                gap: '16px',
              }}
            >
              <div style={{ flex: 1 }}>
                <p
                  style={{
                    margin: '0 0 4px',
                    fontWeight: '700',
                    fontSize: '15px',
                  }}
                >
                  {m.title}
                </p>
                {m.description && (
                  <p
                    style={{
                      margin: '0 0 4px',
                      fontSize: '13px',
                      color: 'var(--color-text-secondary)',
                    }}
                  >
                    {m.description}
                  </p>
                )}
                <span
                  style={{
                    fontSize: '11px',
                    fontWeight: '700',
                    padding: '2px 8px',
                    borderRadius: '4px',
                    background: m.status === 'OPEN' ? '#eafaf1' : '#fdecea',
                    color: m.status === 'OPEN' ? '#1e8449' : '#c0392b',
                    border: `1px solid ${m.status === 'OPEN' ? '#a9dfbf' : '#f5c6cb'}`,
                  }}
                >
                  {m.status}
                </span>
                {m.winningSide && (
                  <span
                    style={{
                      marginLeft: '8px',
                      fontSize: '11px',
                      fontWeight: '700',
                      color: 'var(--color-text-muted)',
                    }}
                  >
                    Winner: {m.winningSide}
                  </span>
                )}
              </div>
              <div style={{ display: 'flex', gap: '8px', flexShrink: 0 }}>
                {m.status === 'OPEN' && (
                  <>
                    <button style={dangerBtn} onClick={() => handleClose(m.id)}>
                      Close
                    </button>
                    {m.options.map((opt) => (
                      <button
                        key={opt.id}
                        style={btnStyle}
                        onClick={() => handleResolve(m.id, opt.id)}
                      >
                        Resolve: {opt.optionName}
                      </button>
                    ))}
                  </>
                )}
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  )
}
