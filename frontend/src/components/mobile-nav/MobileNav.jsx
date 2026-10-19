import { useEffect, useRef } from 'react'
import { Link, NavLink, useLocation } from 'react-router-dom'
import { AnimatePresence, motion, useReducedMotion } from 'motion/react'
import { useAuth } from '../../hooks/auth/useAuth.js'
import { categories } from '../../constants/catalog/catalogOptions.js'
import { categoryImages, storeAssets } from '../../constants/assets/storeAssets.js'
import { label } from '../../utils/format/formatters.js'
import NavIcon from '../nav-icon/NavIcon.jsx'

const availableCategories = categories.filter((category) => category !== 'DESKTOP_COMPUTERS')
const linkClass = ({ isActive }) => (isActive ? 'drawer__link drawer__link--active' : 'drawer__link')

export default function MobileNav({ id, open, onClose, triggerRef }) {
  const { session, profile, isAdmin, logout } = useAuth()
  const location = useLocation()
  const selectedCategory = new URLSearchParams(location.search).get('category') || ''
  const panelRef = useRef(null)
  const reduceMotion = useReducedMotion()

  useEffect(() => { if (open) onClose() }, [location.pathname, location.search])

  useEffect(() => {
    if (!open) return
    const onKeyDown = (event) => { if (event.key === 'Escape') onClose() }
    document.addEventListener('keydown', onKeyDown)
    const previousOverflow = document.body.style.overflow
    document.body.style.overflow = 'hidden'
    panelRef.current?.querySelector('a, button')?.focus()

    return () => {
      document.removeEventListener('keydown', onKeyDown)
      document.body.style.overflow = previousOverflow
      triggerRef?.current?.focus()
    }
  }, [open])

  const slide = reduceMotion
    ? { initial: { opacity: 0 }, animate: { opacity: 1 }, exit: { opacity: 0 } }
    : { initial: { x: '-100%' }, animate: { x: 0 }, exit: { x: '-100%' } }

  return <AnimatePresence>
    {open && <>
      <motion.div className="drawer-backdrop" onClick={onClose} initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} transition={{ duration: 0.2 }} />
      <motion.nav id={id} ref={panelRef} className="drawer" aria-label="Site navigation" {...slide} transition={{ type: 'tween', duration: 0.26, ease: [0.22, 0.61, 0.36, 1] }}>
        <div className="drawer__header">
          <Link to="/" onClick={onClose} className="drawer__brand"><img src={storeAssets.logo} alt="Incredible Tech" /></Link>
          <button type="button" className="drawer__close" aria-label="Close menu" onClick={onClose}>×</button>
        </div>

        {session && <div className="drawer__user"><span className="avatar">{profile?.firstName?.[0] || 'U'}</span><span>{profile?.firstName || 'Account'}<small>{profile?.email}</small></span></div>}

        {!isAdmin && <div className="drawer__section drawer__shop">
          <div className="drawer__section-head"><span className="drawer__label">Products available</span><Link to="/catalog" onClick={onClose}>View all</Link></div>
          <Link to="/catalog" onClick={onClose} className={`drawer__all${location.pathname === '/catalog' && !selectedCategory ? ' is-active' : ''}`}>All products</Link>
          <div className="drawer__category-grid">
            {availableCategories.map((category) => <Link key={category} to={`/catalog?category=${category}`} onClick={onClose} className={`drawer__category${selectedCategory === category ? ' is-active' : ''}`}>
              <span className="drawer__category-image"><img src={categoryImages[category]} alt="" aria-hidden="true" /></span>
              <strong>{label(category)}</strong>
            </Link>)}
          </div>
        </div>}

        <div className="drawer__section"><span className="drawer__label">Company</span><NavLink to="/about" className={linkClass}>About Incredible Tech</NavLink></div>

        {session && !isAdmin && <div className="drawer__section"><span className="drawer__label">My account</span><NavLink to="/cart" className={linkClass}><NavIcon name="cart" /><span>Cart</span></NavLink><NavLink to="/wishlist" className={linkClass}><NavIcon name="heart" /><span>Wishlist</span></NavLink><NavLink to="/orders" className={linkClass}><NavIcon name="box" /><span>Orders</span></NavLink><NavLink to="/tracking" className={linkClass}><NavIcon name="truck" /><span>Track delivery</span></NavLink><NavLink to="/profile" className={linkClass}><NavIcon name="person" /><span>My account</span></NavLink></div>}

        {isAdmin && <div className="drawer__section"><span className="drawer__label">Administration</span><NavLink to="/admin" className={linkClass} end>Dashboard</NavLink><NavLink to="/admin/products" className={linkClass}>Products</NavLink><NavLink to="/admin/orders" className={linkClass}>Orders</NavLink><NavLink to="/admin/payments" className={linkClass}>Payments</NavLink><NavLink to="/admin/deliveries" className={linkClass}>Deliveries</NavLink><NavLink to="/admin/feedback" className={linkClass}>Feedback</NavLink><NavLink to="/admin/users" className={linkClass}>Users</NavLink></div>}

        <div className="drawer__footer">{session
          ? <button className="button button--outline-light button--full" onClick={() => { onClose(); logout() }}>Sign out</button>
          : <><Link className="button button--drawer button--full" to="/login" onClick={onClose}>Sign in</Link><Link className="drawer__link drawer__link--center" to="/register" onClick={onClose}>Create an account</Link></>}
        </div>
      </motion.nav>
    </>}
  </AnimatePresence>
}
