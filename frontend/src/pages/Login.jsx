import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import styles from './Auth.module.css'
import logo from '../assets/Logo.png'
import { useAuth } from '../features/auth/AuthContext'
import { apiFetch } from '../shared/api'

function Login() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()
  const { login } = useAuth()

  const handleLogin = async (e) => {
    e.preventDefault()
    setError(null)
    setLoading(true)

    try {
      const userData = await apiFetch(
        `/api/users/login-user?username=${encodeURIComponent(username)}&password=${encodeURIComponent(password)}`,
        { method: 'POST' }
      ).catch(() => {
        throw new Error('Invalid username or password')
      })

      login(userData)
      navigate('/dashboard')
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className={styles['auth-container']}>
      <div className={styles.left}>
        <Link to="/" className={styles['brand-link']}>
          <img src={logo} alt="logo" />
          <h1>Redbird Bets</h1>
        </Link>
      </div>

      <div className={styles.right}>
        <h2>Login</h2>

        <form className={styles.form} onSubmit={handleLogin}>
          <label className={styles.label}>Username</label>
          <input
            className={styles.input}
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />

          <label className={styles.label}>Password</label>
          <input
            className={styles.input}
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />

          {error && (
            <p style={{ color: 'red', fontSize: '14px', marginBottom: '12px' }}>
              {error}
            </p>
          )}

          <button className={styles['login-btn']} disabled={loading}>
            {loading ? 'Logging in...' : 'Log In'}
          </button>

          <button
            type="button"
            className={styles['signup-btn']}
            onClick={() => navigate('/signup')}
          >
            SIGN UP!
          </button>
        </form>
      </div>
    </div>
  )
}

export default Login
