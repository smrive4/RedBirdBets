import { useState, useEffect } from 'react'
import { useAuth } from '../features/auth/AuthContext'

import styles from './Leaderboard.module.css'
import { useNavigate } from 'react-router-dom'

function Leaderboard() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const [entries, setEntries] = useState([])
  const [loading, setLoading] = useState(true)
  const [mode, setMode] = useState('balance')

  function handleModeChange(modeSelected) {
    setMode(modeSelected)
  }

  useEffect(() => {
    fetch(`http://localhost:8080/api/users/leaderboard/${mode}`)
      .then((res) => res.json())
      .then((data) => {
        setEntries(data)
        setLoading(false)
      })
      .catch(() => setLoading(false))
  }, [mode])

  return (
    <div>
      <nav className={styles.navbar}>
        <div className={styles.brand} onClick={() => navigate('/')}>
          REDBIRDBETS
        </div>
        <button
          onClick={() => navigate('/leaderboard')}
          className={styles.navLink}
        >
          Leaderboard
        </button>
        <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
          <div className={styles.balanceChip}>
            🪙 <strong>{Number(user?.balance || 0).toLocaleString()}</strong>{' '}
            coins
          </div>
          <button
            onClick={() => {
              logout()
              navigate('/')
            }}
            style={{
              background: 'none',
              border: '1.5px solid var(--color-border)',
              borderRadius: '8px',
              padding: '6px 14px',
              cursor: 'pointer',
              fontSize: '13px',
              fontWeight: 'bold',
              color: 'var(--color-text-secondary)',
            }}
          >
            Log Out
          </button>
        </div>
      </nav>

      <div className={styles.heroBanner}>
        <h1>Leaderboard</h1>
        <p>See how you stack up against other bettors.</p>
      </div>

      {/* Toggle Buttons */}
      <div className={styles.container}>
        <div className={styles.buttonContainer}>
          <button
            onClick={() => handleModeChange('balance')}
            className={styles.btn}
          >
            By Balance
          </button>
          <button
            onClick={() => handleModeChange('winnings')}
            className={styles.btn}
          >
            By Winnings
          </button>
          <button
            onClick={() => handleModeChange('losses')}
            className={styles.btn}
          >
            By Losses
          </button>
        </div>
      </div>

      {/* LeaderBoard */}
      <div className={styles.content}>
        {loading ? (
          <p>Loading...</p>
        ) : entries.length === 0 ? (
          <p>No data yet</p>
        ) : (
          <>
            <div className={styles.tableCard}>
              <div className={styles.tableHeader}>
                <span>Rank</span>
                <span>Player</span>
                <span>Balance</span>
              </div>
            </div>
            {entries.map((entry, index) => {
              return (
                <div key={entry.id} className={styles.tableRow}>
                  <span>{index + 1}</span>
                  <span>{entry.username}</span>
                  <span>{entry.balance}</span>
                </div>
              )
            })}
          </>
        )}
      </div>
    </div>
  )
}

export default Leaderboard
