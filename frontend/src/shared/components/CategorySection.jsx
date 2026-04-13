import styles from './CategorySection.module.css'
import BetCard from './BetCard'

function CategorySection({ title, markets, onBet }) {
  return (
    <>
      <div className={styles.categoryTitle}>{title}</div>
      <div className={styles.grid}>
        {markets.map((market) => (
          <BetCard key={market.id} bet={market} onBet={onBet} />
        ))}
      </div>
    </>
  )
}

export default CategorySection
