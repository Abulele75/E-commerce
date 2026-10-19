import { useMemo, useState } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { AnimatePresence, motion, useReducedMotion } from 'motion/react'
import ProductCard from '../../components/product-card/ProductCard.jsx'
import ProductSkeleton from '../../components/product-skeleton/ProductSkeleton.jsx'
import Notice from '../../components/notice/Notice.jsx'
import Reveal from '../../components/motion/reveal/Reveal.jsx'
import { productService } from '../../services/products/productService.js'
import { categories } from '../../constants/catalog/catalogOptions.js'
import { duration, easeOut } from '../../constants/motion/motionTokens.js'
import { useAsync } from '../../hooks/async/useAsync.js'
import { useAuth } from '../../hooks/auth/useAuth.js'
import { useCart } from '../../hooks/cart/useCart.js'
import { label, money } from '../../utils/format/formatters.js'

const comingSoonCategories = new Set(['COMPUTER_COMPONENTS', 'DESKTOP_COMPUTERS'])
const effectivePrice = (product) => Number(product.effectivePrice ?? product.price ?? 0)
const clamp = (value, minimum, maximum) => Math.min(maximum, Math.max(minimum, value))

export default function Catalog() {
  const [params, setParams] = useSearchParams()
  const [addError, setAddError] = useState('')
  const reduceMotion = useReducedMotion()
  const query = params.get('search') || ''
  const category = params.get('category') || ''
  const { session } = useAuth()
  const { addItem } = useCart()
  const navigate = useNavigate()
  const { data = [], loading, error } = useAsync(productService.list, [])

  const priceBounds = useMemo(() => {
    const prices = (data || []).map(effectivePrice).filter((price) => Number.isFinite(price) && price >= 0)
    if (!prices.length) return { minimum: 0, maximum: 100000 }
    return {
      minimum: Math.floor(Math.min(...prices) / 100) * 100,
      maximum: Math.ceil(Math.max(...prices) / 100) * 100,
    }
  }, [data])
  const requestedMin = Number(params.get('minPrice'))
  const requestedMax = Number(params.get('maxPrice'))
  const minPrice = clamp(Number.isFinite(requestedMin) && params.has('minPrice') ? requestedMin : priceBounds.minimum, priceBounds.minimum, priceBounds.maximum)
  const maxPrice = clamp(Number.isFinite(requestedMax) && params.has('maxPrice') ? requestedMax : priceBounds.maximum, minPrice, priceBounds.maximum)
  const rangeSpan = Math.max(1, priceBounds.maximum - priceBounds.minimum)
  const rangeStart = ((minPrice - priceBounds.minimum) / rangeSpan) * 100
  const rangeEnd = ((maxPrice - priceBounds.minimum) / rangeSpan) * 100

  const products = useMemo(() => (data || []).filter((product) => {
    const price = effectivePrice(product)
    return (!category || product.category === category)
      && `${product.productName} ${product.brand} ${product.description || ''}`.toLowerCase().includes(query.toLowerCase())
      && price >= minPrice
      && price <= maxPrice
  }), [data, query, category, minPrice, maxPrice])

  const categoryHasProducts = !category || (data || []).some((product) => product.category === category)
  const isComingSoon = comingSoonCategories.has(category) && !categoryHasProducts
  const update = (key, value) => {
    const next = new URLSearchParams(params)
    value ? next.set(key, value) : next.delete(key)
    setParams(next, { replace: key === 'search' })
  }
  const updatePrice = (key, value, boundary) => {
    const next = new URLSearchParams(params)
    if (Number(value) === boundary) next.delete(key)
    else next.set(key, String(value))
    setParams(next, { replace: true })
  }
  const clearFilters = () => setParams({})
  const add = async (product) => {
    if (!session) return navigate('/login')
    setAddError('')
    try { await addItem(product) } catch (err) { setAddError(err.message) }
  }
  const itemMotion = reduceMotion ? {} : {
    layout: true,
    initial: { opacity: 0, y: 10, scale: 0.985 },
    animate: { opacity: 1, y: 0, scale: 1 },
    exit: { opacity: 0, y: -6, scale: 0.98 },
    transition: { duration: duration.base, ease: easeOut },
  }

  return <main className="page catalog-page">
    <Reveal as="header" className="page-hero catalog-hero"><span className="eyebrow">The collection</span><h1>Premium technology. Precisely filtered.</h1><p>Trusted devices and components, delivered across South Africa.</p></Reveal>
    <div className="filters catalog-filters">
      <input aria-label="Search products" placeholder="Search products or brands…" value={query} onChange={(event) => update('search', event.target.value)} />
      <select aria-label="Category" value={category} onChange={(event) => update('category', event.target.value)}><option value="">All categories</option>{categories.map((option) => <option key={option} value={option}>{label(option)}</option>)}</select>
      <div className="price-range" style={{ '--range-start': `${rangeStart}%`, '--range-end': `${rangeEnd}%` }}>
        <div className="price-range__values"><span>Price</span><strong>{money(minPrice)} – {money(maxPrice)}</strong></div>
        <div className="price-range__control">
          <div className="price-range__track" aria-hidden="true" />
          <input aria-label="Minimum price" type="range" min={priceBounds.minimum} max={priceBounds.maximum} step="100" value={minPrice} onChange={(event) => updatePrice('minPrice', Math.min(Number(event.target.value), maxPrice), priceBounds.minimum)} />
          <input aria-label="Maximum price" type="range" min={priceBounds.minimum} max={priceBounds.maximum} step="100" value={maxPrice} onChange={(event) => updatePrice('maxPrice', Math.max(Number(event.target.value), minPrice), priceBounds.maximum)} />
        </div>
      </div>
      <span className="catalog-count">{products.length} products</span>
    </div>
    <Notice message={addError || error} />
    {loading
      ? <ProductSkeleton count={8} />
      : isComingSoon
        ? <motion.section className="empty catalog-empty" initial={reduceMotion ? false : { opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }}><span className="eyebrow">{label(category)}</span><h2>Coming soon</h2><p>We’re preparing a new range of {label(category).toLowerCase()}. Check back soon.</p></motion.section>
        : products.length === 0
          ? <motion.section className="empty catalog-empty" initial={reduceMotion ? false : { opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }}><h2>No products match your filters</h2><p>Adjust the price range, try another search, or browse the complete collection.</p><button className="button button--ghost" type="button" onClick={clearFilters}>Clear filters</button></motion.section>
          : <div className="product-grid"><AnimatePresence initial={false} mode="popLayout">{products.map((product) => <motion.div className="product-grid__item" key={product.productId} {...itemMotion}><ProductCard product={product} onAdd={add} /></motion.div>)}</AnimatePresence></div>}
  </main>
}
