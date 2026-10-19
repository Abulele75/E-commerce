import { NavLink } from 'react-router-dom'
import { useAuth } from '../../hooks/auth/useAuth.js'
import { useCart } from '../../hooks/cart/useCart.js'
import { useWishlist } from '../../hooks/wishlist/useWishlist.js'
import NavIcon from '../nav-icon/NavIcon.jsx'

const customerItems = [
  ['/', 'home', 'Home', true],
  ['/catalog', 'shop', 'Shop'],
  ['/tracking', 'truck', 'Delivery'],
  ['/wishlist', 'heart', 'Wishlist'],
  ['/cart', 'cart', 'Cart'],
]

const adminItems = [
  ['/admin', 'home', 'Dashboard', true],
  ['/admin/products', 'shop', 'Products'],
  ['/admin/orders', 'box', 'Orders'],
  ['/admin/deliveries', 'truck', 'Delivery'],
  ['/admin/payments', 'card', 'Payments'],
]

export default function MobileBottomNav() {
  const { session, isAdmin } = useAuth()
  const { count } = useCart()
  const { items: wishlist } = useWishlist()
  const items = isAdmin ? adminItems : customerItems

  return <nav className="bottom-nav" aria-label="Quick navigation">
    <div className="bottom-nav__inner">
      {items.map(([to, icon, text, end]) => <NavLink key={to} to={to} end={Boolean(end)} className={({ isActive }) => `bottom-nav__item${isActive ? ' is-active' : ''}`}>
        <span className="bottom-nav__icon"><NavIcon name={icon} />{to === '/cart' && count > 0 && <span className="bottom-nav__badge">{count}</span>}{to === '/wishlist' && wishlist.length > 0 && <span className="bottom-nav__badge">{wishlist.length}</span>}</span>
        <span>{text}</span>
      </NavLink>)}
      {!isAdmin && <NavLink to={session ? '/profile' : '/login'} className={({ isActive }) => `bottom-nav__item${isActive ? ' is-active' : ''}`}><span className="bottom-nav__icon"><NavIcon name="person" /></span><span>Account</span></NavLink>}
    </div>
  </nav>
}
