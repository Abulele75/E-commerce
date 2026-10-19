import { Link } from 'react-router-dom'
import WishlistButton from '../wishlist-button/WishlistButton.jsx'
import { money, label } from '../../utils/format/formatters.js'
import { useAuth } from '../../hooks/auth/useAuth.js'

export default function ProductCard({ product, onAdd, tag = '' }) {
  const { isAdmin } = useAuth()
  const displayedPrice = tag ? product.price : (product.effectivePrice ?? product.price)
  return <article className="product-card">
    <WishlistButton product={product} />
    <Link to={`/products/${product.productId}`} className="product-card__image-wrap">
      <img className="product-card__image" src={product.primaryImageUrl || '/placeholder.svg'} alt={product.productName} loading="lazy" decoding="async" />
      {(tag || product.featured) && <span className={`badge${tag ? ' badge--low-price' : ''}`}>{tag || 'Featured'}</span>}
    </Link>
    <div className="product-card__body">
      <span className="eyebrow">{label(product.brand)} · {label(product.category)}</span>
      <Link to={`/products/${product.productId}`}><h3>{product.productName}</h3></Link>
      <div className="product-card__price"><strong>{money(displayedPrice)}</strong>{!tag && product.discountPrice && <del>{money(product.price)}</del>}</div>
      {isAdmin
        ? <Link className="button button--ghost button--full" to={`/products/${product.productId}`}>View product</Link>
        : <button className="button button--primary button--full" disabled={!product.available} onClick={() => onAdd?.(product)}>{product.available ? 'Add to cart' : 'Out of stock'}</button>}
    </div>
  </article>
}
