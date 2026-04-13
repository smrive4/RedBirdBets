import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../features/auth/AuthContext'
import styles from './Dashboard.module.css'

import CategorySection from '../shared/components/CategorySection'
import Leaderboard from '../shared/components/Leaderboard'
import { useMarkets } from '../features/markets/useMarket'
import BetCard from '../shared/components/BetCard'

export default function Dashboard() {
  const navigate = useNavigate()
  const { user, login, logout } = useAuth()

  const [modal, setModal] = useState(null)
  const [selectedOption, setSelectedOption] = useState(null)
  const [betAmount, setBetAmount] = useState('')
  const [toast, setToast] = useState(null)
  const [betLoading, setBetLoading] = useState(false)
  const [betError, setBetError] = useState(null)

  const {
    markets,
    academicMarkets,
    sportMarkets,
    campusMarkets,
    otherMarkets,
    recentlyCreatedMarkets,
    loading,
  } = useMarkets()

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

      <div className={styles.container}>
        <div className={styles.content}>
          <div>
            {loading ? (
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
              <>
                <CategorySection
                  title="Campus Life"
                  markets={campusMarkets}
                  onBet={openModal}
                />
                <CategorySection
                  title="Academics"
                  markets={academicMarkets}
                  onBet={openModal}
                />
                <CategorySection
                  title="Sports"
                  markets={sportMarkets}
                  onBet={openModal}
                />
                <CategorySection
                  title="Other"
                  markets={otherMarkets}
                  onBet={openModal}
                />
              </>
            )}
          </div>
        </div>
        <div className={styles.sidebar}>
          <div className={styles.title}>This Months Biggest Lossers</div>
          <Leaderboard url="http://localhost:8080/api/users/leaderboard/monthly-losses" />

          <div className={styles.title}>Recently Created Bets</div>
          {recentlyCreatedMarkets.map((m) => {
            return (
              <div className={styles.margin}>
                <BetCard key={m.id} bet={m} onBet={openModal} />
              </div>
            )
          })}
        </div>
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
