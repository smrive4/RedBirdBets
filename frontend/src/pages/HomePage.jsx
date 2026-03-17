import styles from './HomePage.module.css'

function HomePage() {
  return (
    <main className={styles.background}>
      <nav className={styles.navContainer}>
        <div className={styles.brand}>REDBIRDBETS</div>
        <div className={styles.lhNav}>
          <a className={styles.buttonOutline}>Login In</a>
          <a className={styles.button}>Sign Up</a>
        </div>
      </nav>
      <section className={styles.heroSection}>
        <div className={styles.heroTitle}>
          Bet on what you <span className={styles.highlight}>know</span>.
        </div>
        <div className={styles.heroDesc}>
          Trade on real-world events; Academics, Sports, Campus Events, and
          more. Put your skills to the test against thousands of other users.
        </div>
        <a href="" className={styles.button}>
          Create free account
        </a>
      </section>
      <section className={styles.worksSection}>
        <div className={styles.sectionTitle}>How it works</div>
        <div className={styles.cardsContainer}>
          <div className={styles.card}>
            <div className={styles.highlight}>01</div>
            <div className={styles.cardTitle}>Browse Market</div>
            <div>
              Find open questions on sports, academeic events, academics, and
              more
            </div>
          </div>
          <div className={styles.card}>
            <div className={styles.highlight}>02</div>
            <div className={styles.cardTitle}>Place Bets</div>
            <div>
              Buy YES or NO shares based on your confidence in the outcome.
            </div>
          </div>
          <div className={styles.card}>
            <div className={styles.highlight}>03</div>
            <div className={styles.cardTitle}>Market Resolves</div>
            <div>
              When the event happens, correct bettors collect their winnings.
            </div>
          </div>
          <div className={styles.card}>
            <div className={styles.highlight}>04</div>
            <div className={styles.cardTitle}>Climb the ranks</div>
            <div>
              Build your track record and rise up the global leaderboard.
            </div>
          </div>
        </div>
      </section>
      <footer className={styles.footer}>
        <div className={styles.footerCopy}>
          © 2026 RedBirdBets. All rights reserved.
        </div>
      </footer>
    </main>
  )
}

export default HomePage
