import { Link } from 'react-router-dom'
import ProductCard from '../product-card/ProductCard.jsx'
import ProductSkeleton from '../product-skeleton/ProductSkeleton.jsx'
import Reveal from '../motion/reveal/Reveal.jsx'
import './featured-rail.css'

export default function FeaturedRail({
  products = [],
  loading,
  onAdd,
  eyebrow = 'Featured',
  title = 'New in store',
  emptyMessage = '',
  linkLabel = 'Shop all',
  cardTag = '',
}) {
  return <section className="section product-showcase">
    <Reveal className="product-showcase__head">
      <div><span className="eyebrow">{eyebrow}</span><h2>{title}</h2></div>
      <Link className="product-showcase__all" to="/catalog">{linkLabel} →</Link>
    </Reveal>

    {loading
      ? <div className="product-showcase__loading"><ProductSkeleton count={6} /></div>
      : products.length
        ? <div className="product-showcase__grid">{products.slice(0, 6).map((product) => <ProductCard key={product.productId} product={product} onAdd={onAdd} tag={cardTag} />)}</div>
        : <div className="product-showcase__empty"><p>{emptyMessage || 'No products are available in this collection right now.'}</p><Link to="/catalog">Browse all products</Link></div>}
  </section>
}
