import styles from './BetCard.module.css'

function BetCard({ bet, onBet }) {
  const options = bet.options || []
  const total = options.reduce((sum, o) => sum + (o.totalAmount || 0), 0)

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
        {options.map((opt) => {
          const percent = total === 0
            ? Math.round(100 / options.length)
            : Math.round((opt.totalAmount / total) * 100)
          return (
            <div key={opt.id} className={styles.optionRow}>
              <div className={styles.optionLeft}>
                <span className={styles.optionLabel}>{opt.optionName}</span>
                <div className={styles.barTrack}>
                  <div
                    className={styles.barFill}
                    style={{ width: `${percent}%` }}
                  />
                </div>
              </div>
              <span
                className={`${styles.percent} ${percent >= 50 ? styles.percentHigh : ''}`}
              >
                {percent}%
              </span>
            </div>
          )
        })}
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
