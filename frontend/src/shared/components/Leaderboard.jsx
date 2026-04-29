import { useState, useEffect } from 'react'
import { useAuth } from '../../features/auth/AuthContext'
import styles from './Leaderboard.module.css'
import { apiFetch } from '../api'

function formatAmount(amt) {
  return '$ ' + amt.toFixed(0)
}

function Leaderboard({ url, label }) {
  const { user } = useAuth()
  const [loading, setLoading] = useState(true)
  const [entries, setEntries] = useState([])

  useEffect(() => {
    apiFetch(url)
      .then((data) => {
        setEntries(data)
        setLoading(false)
      })
      .catch(() => setLoading(false))
  }, [url])

  return (
    <div className={styles.leaderboardContainer}>
      <div className={styles.header}>
        <span>Rank</span>
        <span>Player</span>
        <span>{label}</span>
      </div>

      {entries.map((entry, index) => {
        return (
          <div className={styles.entry}>
            <span className={styles.rank}>{index + 1}</span>
            <span>
              {entry.username}{' '}
              {user.id === entry.id && (
                <span className={styles.youTag}>You</span>
              )}
            </span>
            <span>{formatAmount(entry.balance)}</span>
          </div>
        )
      })}
    </div>
  )
}

export default Leaderboard
