import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../features/auth/AuthContext'
import styles from './Dashboard.module.css'

function BetCard({ bet, onBet }) {
  const totalYes = bet.totalYes || 0
  const totalNo = bet.totalNo || 0
  const total = totalYes + totalNo
  const yesPercent = total === 0 ? 50 : Math.round((totalYes / total) * 100)
  const noPercent = 100 - yesPercent

  return (
    <div className={styles.card}>
      <p className={styles.question}>{bet.title}</p>
      {bet.description && (
        <p
          style={{
            fontSize: '13px',
            color: 'var(--color-text-secondary)',
            margin: 0,
          }}
        >
          {bet.description}
        </p>
      )}
      <div className={styles.options}>
        {[
          { label: 'YES', percent: yesPercent },
          { label: 'NO', percent: noPercent },
        ].map((opt) => (
          <div key={opt.label} className={styles.optionRow}>
            <div className={styles.optionLeft}>
              <span className={styles.optionLabel}>{opt.label}</span>
              <div className={styles.barTrack}>
                <div
                  className={styles.barFill}
                  style={{ width: `${opt.percent}%` }}
                />
              </div>
            </div>
            <span
              className={`${styles.percent} ${opt.percent >= 50 ? styles.percentHigh : ''}`}
            >
              {opt.percent}%
            </span>
          </div>
        ))}
      </div>
      <div className={styles.cardFooter}>
        <button className={styles.betBtn} onClick={() => onBet(bet)}>
          Place Bet
        </button>
      </div>
    </div>
  )
}

export default function Dashboard() {
  const navigate = useNavigate()
  const { user, login, logout } = useAuth()

  const [markets, setMarkets] = useState([])
  const [loadingMarkets, setLoadingMarkets] = useState(true)
  const [modal, setModal] = useState(null)
  const [selectedOption, setSelectedOption] = useState(null)
  const [betAmount, setBetAmount] = useState('')
  const [toast, setToast] = useState(null)
  const [betLoading, setBetLoading] = useState(false)
  const [betError, setBetError] = useState(null)

  useEffect(() => {
    fetch('http://localhost:8080/api/markets')
      .then((res) => res.json())
      .then((data) => {
        setMarkets(data.filter((m) => m.status === 'OPEN'))
        setLoadingMarkets(false)
      })
      .catch(() => setLoadingMarkets(false))
  }, [])

  const openModal = (market) => {
    setModal(market)
    setSelectedOption(null)
    setBetAmount('')
    setBetError(null)
  }

  const handleConfirm = async () => {
    if (!selectedOption || !betAmount || +betAmount <= 0) return

    setBetLoading(true)
    setBetError(null)

    try {
      const response = await fetch(
        `http://localhost:8080/api/bets/user/${user.id}/market/${modal.id}`,
        {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            amount: parseFloat(betAmount),
            side: selectedOption,
          }),
        }
      )

      if (!response.ok) {
        const errText = await response.text()
        throw new Error(errText || 'Failed to place bet')
      }

      const updatedUser = await fetch(
        `http://localhost:8080/api/users/${user.id}`
      ).then((r) => r.json())
      login(updatedUser)

      setModal(null)
      setToast(`Bet placed! ${betAmount} coins on "${selectedOption}"`)
      setTimeout(() => setToast(null), 3000)
    } catch (err) {
      setBetError(err.message)
    } finally {
      setBetLoading(false)
    }
  }

  return (
    <div className={styles.page}>
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

      <div className={styles.hero}>
        <h1>What's your prediction?</h1>
        <p>Browse open markets, place bets, and climb the leaderboard.</p>
      </div>

      <div className={styles.content}>
        {loadingMarkets ? (
          <p
            style={{
              textAlign: 'center',
              color: 'var(--color-text-muted)',
              padding: '40px',
            }}
          >
            Loading markets...
          </p>
        ) : markets.length === 0 ? (
          <p
            style={{
              textAlign: 'center',
              color: 'var(--color-text-muted)',
              padding: '40px',
            }}
          >
            No open markets right now.
          </p>
        ) : (
          <div className={styles.grid}>
            {markets.map((market) => (
              <BetCard key={market.id} bet={market} onBet={openModal} />
            ))}
          </div>
        )}
      </div>

      {modal && (
        <div className={styles.overlay} onClick={() => setModal(null)}>
          <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
            <button className={styles.closeBtn} onClick={() => setModal(null)}>
              ✕
            </button>
            <p className={styles.modalQuestion}>{modal.title}</p>

            <p className={styles.modalLabel}>Choose outcome</p>
            <div className={styles.modalOptions}>
              {['YES', 'NO'].map((opt) => (
                <button
                  key={opt}
                  onClick={() => setSelectedOption(opt)}
                  className={`${styles.optionBtn} ${selectedOption === opt ? styles.optionBtnActive : ''}`}
                >
                  {opt}
                </button>
              ))}
            </div>

            <p className={styles.modalLabel}>Bet amount (coins)</p>
            <input
              type="number"
              placeholder="e.g. 100"
              value={betAmount}
              onChange={(e) => setBetAmount(e.target.value)}
              className={styles.amountInput}
            />

            {betError && (
              <p style={{ color: 'red', fontSize: '13px', margin: 0 }}>
                {betError}
              </p>
            )}

            <button
              onClick={handleConfirm}
              disabled={betLoading || !selectedOption || !betAmount}
              className={`${styles.confirmBtn} ${!selectedOption || !betAmount ? styles.confirmBtnDisabled : ''}`}
            >
              {betLoading ? 'Placing Bet...' : 'Confirm Bet'}
            </button>
          </div>
        </div>
      )}

      {toast && <div className={styles.toast}>{toast}</div>}
    </div>
  )
}
