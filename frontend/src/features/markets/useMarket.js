import { useState, useEffect } from 'react'
import { apiFetch } from '../../shared/api'

function getRecentlyCreated(markets) {
  let recMarkets = markets.filter((m) => m.createdAt != null)

  recMarkets = recMarkets
    .sort(
      (a, b) =>
        new Date(b.createdAt).valueOf() - new Date(a.createdAt).valueOf()
    )
    .slice(0, 3)

  return recMarkets
}

export function useMarkets() {
  const [markets, setMarkets] = useState([])
  const [loading, setLoading] = useState(true)
  const [refetch, setRefetch] = useState(0)

  const refresh = () => setRefetch((prev) => prev + 1)

  useEffect(() => {
    apiFetch('/api/markets')
      .then((data) => {
        setMarkets(data.filter((m) => m.status === 'OPEN'))
        setLoading(false)
      })
      .catch(() => setLoading(false))
  }, [refetch])

  const academicMarkets = markets.filter(
    (m) => m.marketCategory === 'ACADEMICS'
  )
  const sportMarkets = markets.filter((m) => m.marketCategory === 'SPORTS')
  const campusMarkets = markets.filter(
    (m) => m.marketCategory === 'CAMPUS_LIFE'
  )
  const otherMarkets = markets.filter((m) => m.marketCategory === 'OTHER')

  const recentlyCreatedMarkets = getRecentlyCreated(markets)

  return {
    markets,
    academicMarkets,
    sportMarkets,
    campusMarkets,
    otherMarkets,
    recentlyCreatedMarkets,
    loading,
    refresh,
  }
}
