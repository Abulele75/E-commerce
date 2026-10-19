import { createContext, useCallback, useEffect, useMemo, useRef, useState } from 'react'
import { wishlistService } from '../../services/wishlist/wishlistService.js'
import { useAuth } from '../../hooks/auth/useAuth.js'

export const WishlistContext = createContext(null)

export function WishlistProvider({ children }) {
  const { session, isAdmin } = useAuth()
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(false)
  const [pendingIds, setPendingIds] = useState(new Set())
  const [notice, setNotice] = useState('')
  const timerRef = useRef(0)

  const showNotice = useCallback((message) => {
    setNotice(message)
    if (timerRef.current) window.clearTimeout(timerRef.current)
    timerRef.current = window.setTimeout(() => setNotice(''), 2400)
  }, [])

  const refresh = useCallback(async () => {
    if (!session || isAdmin) { setItems([]); return [] }
    setLoading(true)
    try { const next = await wishlistService.list(); setItems(next || []); return next || [] }
    catch (err) { showNotice(err?.message || 'Wishlist could not be loaded'); return [] }
    finally { setLoading(false) }
  }, [session, isAdmin, showNotice])

  useEffect(() => { refresh() }, [refresh])
  useEffect(() => () => { if (timerRef.current) window.clearTimeout(timerRef.current) }, [])

  const ids = useMemo(() => new Set(items.map((item) => item.product.productId)), [items])
  const has = useCallback((productId) => ids.has(productId), [ids])

  const toggle = useCallback(async (product) => {
    const productId = typeof product === 'string' ? product : product?.productId
    if (!productId || pendingIds.has(productId)) return
    const removing = ids.has(productId)
    setPendingIds((current) => new Set(current).add(productId))
    try {
      if (removing) {
        await wishlistService.remove(productId)
        setItems((current) => current.filter((item) => item.product.productId !== productId))
        showNotice('Removed from wishlist')
      } else {
        const created = await wishlistService.add(productId)
        setItems((current) => [created, ...current.filter((item) => item.product.productId !== productId)])
        showNotice('Saved to wishlist')
      }
    } catch (err) { showNotice(err?.message || 'Wishlist could not be updated') }
    finally { setPendingIds((current) => { const next = new Set(current); next.delete(productId); return next }) }
  }, [ids, pendingIds, showNotice])

  const value = useMemo(() => ({ items, products: items.map((item) => item.product), loading, has, toggle, pendingIds, refresh }), [items, loading, has, toggle, pendingIds, refresh])
  return <WishlistContext.Provider value={value}>{children}<div className={`wishlist-toast${notice ? ' is-visible' : ''}`} role="status" aria-live="polite">{notice}</div></WishlistContext.Provider>
}
