import { useState } from 'react'
import { Link, Navigate } from 'react-router-dom'
import Loader from '../../components/loader/Loader.jsx'
import Notice from '../../components/notice/Notice.jsx'
import ProductCard from '../../components/product-card/ProductCard.jsx'
import { useWishlist } from '../../hooks/wishlist/useWishlist.js'
import { useCart } from '../../hooks/cart/useCart.js'
import { useAuth } from '../../hooks/auth/useAuth.js'

export default function Wishlist() {
  const { isAdmin } = useAuth()
  const { products, loading } = useWishlist()
  const { addItem } = useCart()
  const [message, setMessage] = useState('')
  if (isAdmin) return <Navigate to="/admin" replace />

  const add = async (product) => {
    setMessage('')
    try { await addItem(product); setMessage('Added to cart.') }
    catch (err) { setMessage(err?.message || 'Item could not be added to cart') }
  }

  return <main className="page wishlist-page"><header className="page-hero page-hero--small"><span className="eyebrow">Saved for later</span><h1>My wishlist</h1><p>Keep your favourite technology close and add it to your cart when you are ready.</p></header><Notice message={message} type={message.startsWith('Added') ? 'success' : 'error'} />{loading ? <Loader /> : products.length ? <div className="product-grid">{products.map((product) => <ProductCard key={product.productId} product={product} onAdd={add} />)}</div> : <section className="empty"><h2>Your wishlist is empty</h2><p>Tap the heart on any product to save it here.</p><Link className="button button--primary" to="/catalog">Browse products</Link></section>}</main>
}
