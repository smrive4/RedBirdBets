import { useState, useEffect } from 'react'
import { useAuth } from '../../features/auth/AuthContext'
import styles from './Leaderboard.module.css'

function formatAmount(amt) {
  return '$ ' + amt.toFixed(0)
}

function Leaderboard() {
  const { user } = useAuth()
  const [loading, setLoading] = useState(true)
  const [entries, setEntries] = useState([])

  useEffect(() => {
    fetch('http://localhost:8080/api/users/leaderboard/balance')
      .then((res) => res.json())
      .then((data) => {
        setEntries(data)
        setLoading(false)
      })
      .catch(() => setLoading(false))
  }, [])

  return (
    <div className={styles.leaderboardContainer}>
      <div className={styles.header}>
        <span>Rank</span>
        <span>Player</span>
        <span>Amount</span>
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
