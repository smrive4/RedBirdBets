import { useState, useEffect } from 'react'

export function useMarkets() {
  const [markets, setMarkets] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetch('http://localhost:8080/api/markets')
      .then((res) => res.json())
      .then((data) => {
        setMarkets(data.filter((m) => m.status === 'OPEN'))
        setLoading(false)
      })
      .catch(() => setLoading(false))
  }, [])

  const academicMarkets = markets.filter(
    (m) => m.marketCategory === 'ACADEMICS'
  )
  const sportMarkets = markets.filter((m) => m.marketCategory === 'SPORTS')
  const campusMarkets = markets.filter(
    (m) => m.marketCategory === 'CAMPUS_LIFE'
  )
  const otherMarkets = markets.filter((m) => m.marketCategory === 'OTHER')

  return {
    markets,
    academicMarkets,
    sportMarkets,
    campusMarkets,
    otherMarkets,
    loading,
  }
}
