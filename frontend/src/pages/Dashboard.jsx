import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../features/auth/AuthContext'
import styles from './Dashboard.module.css'

import CategorySection from '../shared/components/CategorySection'
import Leaderboard from '../shared/components/Leaderboard'
import { useMarkets } from '../features/markets/useMarket'
import BetCard from '../shared/components/BetCard'
import { apiFetch } from '../shared/api'

export default function Dashboard() {
  const navigate = useNavigate()
  const { user, login, logout } = useAuth()

  const [modal, setModal] = useState(null)
  const [selectedOption, setSelectedOption] = useState(null)
  const [betAmount, setBetAmount] = useState('')
  const [toast, setToast] = useState(null)
  const [betLoading, setBetLoading] = useState(false)
  const [betError, setBetError] = useState(null)
  const [claimLoading, setClaimLoading] = useState(false)
  const [claimedToday, setClaimedToday] = useState(false)

  const today = new Date().toISOString().split('T')[0]
  const alreadyClaimed = claimedToday || user?.lastDailyClaimDate === today

  const handleClaimDaily = async () => {
    setClaimLoading(true)
    try {
      const updatedUser = await apiFetch(
        `/api/users/${user.id}/claim-daily-reward`,
        { method: 'POST' }
      )
      login({ ...updatedUser, token: localStorage.getItem('token') })
      setClaimedToday(true)
      setToast('250 coins claimed!')
      setTimeout(() => setToast(null), 3000)
    } catch (err) {
      if (err.status === 409) {
        setClaimedToday(true)
      }
    } finally {
      setClaimLoading(false)
    }
  }

  const {
    markets,
    academicMarkets,
    sportMarkets,
    campusMarkets,
    otherMarkets,
    recentlyCreatedMarkets,
    loading,
    refresh,
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
      await apiFetch(`/api/bets/user/${user.id}/market/${modal.id}`, {
        method: 'POST',
        body: JSON.stringify({
          amount: parseFloat(betAmount),
          selectedOption: { id: selectedOption.id },
        }),
      })

      const updatedUser = await apiFetch(`/api/users/${user.id}`)
      login({ ...updatedUser, token: localStorage.getItem('token') })

      setModal(null)
      refresh()
      setToast(
        `Bet placed! ${betAmount} coins on "${selectedOption.optionName}"`
      )
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
        <div className={styles.lh}>
          <div className={styles.brand} onClick={() => navigate('/')}>
            REDBIRDBETS
          </div>
          <button
            onClick={() => navigate('/leaderboard')}
            className={styles.navLink}
          >
            Leaderboard
          </button>
          {user?.role === 'ADMIN' && (
            <button
              onClick={() => navigate('/admin')}
              className={styles.navLink}
            >
              Admin
            </button>
          )}
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
          <div className={styles.balanceChip}>
            🪙 <strong>{Number(user?.balance || 0).toLocaleString()}</strong>{' '}
            coins
          </div>
          <button
            onClick={handleClaimDaily}
            disabled={alreadyClaimed || claimLoading}
            className={styles.claimBtn}
            title={
              alreadyClaimed
                ? 'Come back tomorrow for more coins'
                : 'Claim your daily 250 coins'
            }
          >
            {alreadyClaimed
              ? 'Claimed'
              : claimLoading
                ? 'Claiming...'
                : '+ Daily Coins'}
          </button>
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
          <Leaderboard
            url={`/api/users/leaderboard/monthly-losses`}
            label="Amt. Lost"
          />

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
              {modal.options.map((opt) => (
                <button
                  key={opt.id}
                  onClick={() => setSelectedOption(opt)}
                  className={`${styles.optionBtn} ${selectedOption?.id === opt.id ? styles.optionBtnActive : ''}`}
                >
                  {opt.optionName}
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
