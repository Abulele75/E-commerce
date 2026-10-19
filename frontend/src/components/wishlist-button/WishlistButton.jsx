import { useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../../hooks/auth/useAuth.js'
import { useWishlist } from '../../hooks/wishlist/useWishlist.js'

export default function WishlistButton({ product, className = '' }) {
  const { session, isAdmin } = useAuth()
  const { has, toggle, pendingIds } = useWishlist()
  const navigate = useNavigate()
  const location = useLocation()
  if (isAdmin) return null

  const selected = has(product.productId)
  const pending = pendingIds.has(product.productId)
  const click = async (event) => {
    event.preventDefault(); event.stopPropagation()
    if (!session) { navigate('/login', { state: { from: location } }); return }
    await toggle(product)
  }

  return <button type="button" className={`wishlist-button${selected ? ' is-selected' : ''}${className ? ` ${className}` : ''}`} aria-pressed={selected} aria-label={`${selected ? 'Remove' : 'Add'} ${product.productName} ${selected ? 'from' : 'to'} wishlist`} disabled={pending} onClick={click}>
    <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M20.8 4.7a5.5 5.5 0 0 0-7.8 0L12 5.8l-1.1-1.1a5.5 5.5 0 0 0-7.8 7.8L12 21l8.8-8.5a5.5 5.5 0 0 0 0-7.8Z" /></svg>
  </button>
}
