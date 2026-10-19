import { useRef, useState } from 'react'
import { Link, NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../../hooks/auth/useAuth.js'
import { useCart } from '../../hooks/cart/useCart.js'
import { useWishlist } from '../../hooks/wishlist/useWishlist.js'
import { storeAssets } from '../../constants/assets/storeAssets.js'
import HamburgerButton from '../hamburger-button/HamburgerButton.jsx'
import MobileNav from '../mobile-nav/MobileNav.jsx'
import NavIcon from '../nav-icon/NavIcon.jsx'

const MENU_ID = 'primary-navigation'

export default function Header() {
  const { session, profile, isAdmin, logout } = useAuth()
  const { count } = useCart()
  const { items: wishlistItems } = useWishlist()
  const [search, setSearch] = useState('')
  const [menuOpen, setMenuOpen] = useState(false)
  const menuButtonRef = useRef(null)
  const navigate = useNavigate()
  const actionClass = (name) => ({ isActive }) => `nav__link nav__link--icon nav__link--${name}${isActive ? ' nav__link--active' : ''}`
  const mainClass = ({ isActive }) => `nav__link${isActive ? ' nav__link--active' : ''}`
  const submit = (event) => { event.preventDefault(); navigate(`/catalog?search=${encodeURIComponent(search)}`) }

  return <>
    <div className="header-shell"><header className="header">
      <span className="header__menu"><HamburgerButton buttonRef={menuButtonRef} open={menuOpen} controls={MENU_ID} onToggle={() => setMenuOpen((value) => !value)} /></span>
      <Link className="brand header__brand" to="/"><img src={storeAssets.logo} alt="Incredible Tech" /></Link>
      <nav className="nav" aria-label="Main navigation">{isAdmin ? <NavLink className={mainClass} to="/admin">Admin Dashboard</NavLink> : <><NavLink className={mainClass} to="/catalog">Shop</NavLink><NavLink className={mainClass} to="/about">About</NavLink></>}</nav>
      {!isAdmin && <form className="header-search" role="search" onSubmit={submit}><input aria-label="Search store" placeholder="Search products, categories or brands..." value={search} onChange={(event) => setSearch(event.target.value)} /></form>}
      <div className="header__actions">{isAdmin ? (session && <div className="account-menu"><Link to="/admin"><span className="avatar">{profile?.firstName?.[0] || 'A'}</span><span>{profile?.firstName || 'Admin'}<small>Administrator</small></span></Link><button type="button" onClick={logout}>Sign out</button></div>) : <>
        {session && <NavLink className={actionClass('orders')} to="/orders" aria-label="My orders" title="My orders"><NavIcon name="box" /><span>Orders</span></NavLink>}
        <NavLink className={actionClass('tracking')} to="/tracking" aria-label="Track delivery" title="Track delivery"><NavIcon name="truck" /><span>Delivery</span></NavLink>
        <NavLink className={actionClass('wishlist')} to="/wishlist" aria-label={`Wishlist with ${wishlistItems.length} items`} title="Wishlist"><NavIcon name="heart" /><span>Wishlist</span>{wishlistItems.length > 0 && <span className="cart-badge">{wishlistItems.length}</span>}</NavLink>
        <NavLink className={actionClass('cart')} to="/cart" aria-label={`Cart with ${count} items`} title="Cart"><NavIcon name="cart" /><span>Cart</span>{count > 0 && <span className="cart-badge">{count}</span>}</NavLink>
        {session ? <div className="account-menu"><Link to="/profile" aria-label="My account" title="My account"><span className="avatar avatar--icon"><NavIcon name="person" /></span><span>Hi, {profile?.firstName || 'there'}<small>My Account</small></span></Link><button type="button" onClick={logout}>Sign out</button></div> : <Link className="button button--primary header-signin" to="/login"><NavIcon name="person" /><span>Sign in</span></Link>}
      </>}</div>
    </header></div>
    <MobileNav id={MENU_ID} open={menuOpen} onClose={() => setMenuOpen(false)} triggerRef={menuButtonRef} />
  </>
}
