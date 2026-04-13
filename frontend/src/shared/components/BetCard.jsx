import styles from './BetCard.module.css'

function BetCard({ bet, onBet }) {
  const totalYes = bet.totalYesAmt || 0
  const totalNo = bet.totalNoAmt || 0
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

export default BetCard
