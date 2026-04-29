const BASE_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080'

export async function apiFetch(url, options = {}) {
  const token = localStorage.getItem('token')

  const response = await fetch(`${BASE_URL}${url}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...options.headers,
    },
  })

  if (response.status === 401) {
    localStorage.removeItem('user')
    localStorage.removeItem('token')
    window.location.href = '/login'
    return
  }

  if (!response.ok) {
    const error = await response.text()
    const err = new Error(error || response.statusText)
    err.status = response.status
    throw err
  }

  const text = await response.text()
  return text ? JSON.parse(text) : null
}
