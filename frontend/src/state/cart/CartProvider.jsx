import { createContext, useCallback, useEffect, useMemo, useRef, useState } from 'react'
import { cartService } from '../../services/cart/cartService.js'
import { useAuth } from '../../hooks/auth/useAuth.js'

export const CartContext = createContext(null)

/**
 * Tracks the cart item count for the header badge and shows a transient
 * confirmation toast when an item is added. Admins never carry a cart.
 */
export function CartProvider({ children }) {
  const { session, isAdmin } = useAuth()
  const [count, setCount] = useState(0)
  const [toast, setToast] = useState('')
  const timerRef = useRef(0)

  const refresh = useCallback(async () => {
    if (!session || isAdmin) { setCount(0); return }
    try {
      const cart = await cartService.get()
      setCount(cart?.totalItemQuantity ?? (cart?.items || []).reduce((sum, item) => sum + (item.quantity || 0), 0))
    } catch { /* ignore - header badge is non-critical */ }
  }, [session, isAdmin])

  useEffect(() => { refresh() }, [refresh])

  const showToast = useCallback((message) => {
    setToast(message)
    if (timerRef.current) window.clearTimeout(timerRef.current)
    timerRef.current = window.setTimeout(() => setToast(''), 2600)
  }, [])

  const addItem = useCallback(async (product, quantity = 1) => {
    const productId = typeof product === 'string' ? product : product?.productId
    const name = typeof product === 'string' ? 'Item' : (product?.productName || 'Item')
    await cartService.add(productId, quantity)
    setCount((current) => current + quantity)
    showToast(`${name} added to cart`)
  }, [showToast])

  useEffect(() => () => { if (timerRef.current) window.clearTimeout(timerRef.current) }, [])

  const value = useMemo(() => ({ count, addItem, refresh, setCount }), [count, addItem, refresh])

  return (
    <CartContext.Provider value={value}>
      {children}
      <div className={`cart-toast${toast ? ' cart-toast--visible' : ''}`} role="status" aria-live="polite">
        {toast && <span className="cart-toast__dot" aria-hidden="true" />}
        {toast}
      </div>
    </CartContext.Provider>
  )
}
