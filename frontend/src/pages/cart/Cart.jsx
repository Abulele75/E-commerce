import { useCallback, useState } from 'react'
import { Link } from 'react-router-dom'
import { AnimatePresence, motion, useReducedMotion } from 'motion/react'
import Loader from '../../components/loader/Loader.jsx'
import Notice from '../../components/notice/Notice.jsx'
import CheckoutSteps from '../../components/checkout-steps/CheckoutSteps.jsx'
import { cartService } from '../../services/cart/cartService.js'
import { useAsync } from '../../hooks/async/useAsync.js'
import { useCart } from '../../hooks/cart/useCart.js'
import { label, money } from '../../utils/format/formatters.js'
import { duration, easeOut } from '../../constants/motion/motionTokens.js'

const payablePrice = (item) => Number(item.unitPrice ?? item.price ?? 0)
const hasValue = (value) => value != null && String(value).trim() !== '' && String(value).trim() !== '—'

const recompute = (cart) => {
  const items = cart.items || []
  return {
    ...cart,
    items,
    subtotal: items.reduce((sum, item) => sum + payablePrice(item) * item.quantity, 0),
    totalItemQuantity: items.reduce((sum, item) => sum + item.quantity, 0),
  }
}

const availableStock = (item) => {
  const stock = Number(item.stockQuantity ?? item.availableStock)
  return Number.isFinite(stock) ? Math.max(0, stock) : 0
}

const productSpecs = (item) => [
  ['Brand', hasValue(item.brand) ? label(item.brand) : null],
  ['Category', hasValue(item.category) ? label(item.category) : null],
  ['Colour', item.colour],
  ['Storage', item.storage],
  ['Memory', item.memory],
  ['Processor', item.processor],
  ['Graphics', item.graphicsCard],
  ['Operating system', item.operatingSystem],
  ['Warranty', Number(item.warrantyPeriodMonths) > 0 ? `${item.warrantyPeriodMonths} months` : null],
].filter(([, value]) => hasValue(value))

export default function Cart() {
  const { data: cart, setData, loading, error } = useAsync(cartService.get, [])
  const [operationError, setOperationError] = useState('')
  const { setCount } = useCart()
  const reduceMotion = useReducedMotion()

  const sync = useCallback((next) => {
    if (!next) return
    setData(next)
    setCount(next.totalItemQuantity ?? 0)
  }, [setData, setCount])

  const remove = async (id) => {
    if (!cart) return
    const previous = cart
    setOperationError('')
    sync(recompute({ ...cart, items: cart.items.filter((item) => item.cartItemId !== id) }))
    try { sync(await cartService.remove(id)) } catch (err) { sync(previous); setOperationError(err.message) }
  }

  const updateQty = async (item, quantity) => {
    const max = Math.min(99, availableStock(item))
    if (quantity < 1 || quantity > max || quantity === item.quantity) return
    const previous = cart
    setOperationError('')
    sync(recompute({ ...cart, items: cart.items.map((entry) => entry.cartItemId === item.cartItemId ? { ...entry, quantity, lineTotal: payablePrice(entry) * quantity } : entry) }))
    try { sync(await cartService.update(item.cartItemId, quantity)) } catch (err) { sync(previous); setOperationError(err.message) }
  }

  const clear = async () => {
    if (!cart) return
    const previous = cart
    setOperationError('')
    sync(recompute({ ...cart, items: [] }))
    try { sync(await cartService.clear()) } catch (err) { sync(previous); setOperationError(err.message) }
  }

  if (loading) return <main className="page"><Loader /></main>

  const itemMotion = reduceMotion ? {} : {
    layout: true,
    initial: { opacity: 0, y: 8 },
    animate: { opacity: 1, y: 0 },
    exit: { opacity: 0, x: -24, transition: { duration: duration.fast, ease: easeOut } },
    transition: { duration: duration.base, ease: easeOut },
  }
  const hasUnavailableItem = cart?.items?.some((item) => availableStock(item) < item.quantity)

  return <main className="page">
    <header className="page-hero page-hero--small page-hero--flow"><span className="eyebrow">Your selection</span><h1>Shopping cart</h1></header>
    <CheckoutSteps current={1} />
    <Notice message={operationError || error} />
    {!cart?.items?.length
      ? <section className="empty"><h2>Your cart is empty.</h2><p>Find something built to keep up with you.</p><Link className="button button--primary" to="/catalog">Start shopping</Link></section>
      : <div className="cart-layout">
          <section className="cart-list">
            <AnimatePresence initial={false}>
              {cart.items.map((item) => {
                const stock = availableStock(item)
                const specs = productSpecs(item)
                const regularPrice = Number(item.price)
                const effectivePrice = payablePrice(item)
                return <motion.article className="cart-item" key={item.cartItemId} {...itemMotion}>
                  <img src={item.imageUrl || '/placeholder.svg'} alt={item.productName} />
                  <div className="cart-item__details">
                    <small>{item.sku}{item.featured ? ' · Featured' : ''}</small>
                    <h3>{item.productName}</h3>
                    {hasValue(item.description) && <p className="cart-item__description">{item.description}</p>}
                    <p className="cart-item__price">{money(effectivePrice)} each {Number.isFinite(regularPrice) && regularPrice !== effectivePrice && <del>{money(regularPrice)}</del>}</p>
                    <dl className="cart-item__specs">
                      {specs.map(([name, value]) => <div key={name}><dt>{name}</dt><dd>{value}</dd></div>)}
                      <div><dt>Stock</dt><dd>{stock} available</dd></div>
                      <div><dt>Featured</dt><dd>{item.featured ? 'Yes' : 'No'}</dd></div>
                    </dl>
                    {stock < item.quantity && <small className="cart-item__warning">Only {stock} currently available</small>}
                  </div>
                  <div className="qty-stepper">
                    <button type="button" onClick={() => updateQty(item, item.quantity - 1)} disabled={item.quantity <= 1} aria-label={`Decrease ${item.productName} quantity`}>−</button>
                    <span>{item.quantity}</span>
                    <button type="button" onClick={() => updateQty(item, item.quantity + 1)} disabled={item.quantity >= Math.min(99, stock)} aria-label={`Increase ${item.productName} quantity`}>+</button>
                  </div>
                  <strong>{money(item.lineTotal)}</strong>
                  <button className="link-button" onClick={() => remove(item.cartItemId)}>Remove</button>
                </motion.article>
              })}
            </AnimatePresence>
          </section>
          <aside className="summary">
            <h2>Order summary</h2>
            <div><span>Items</span><span>{cart.totalItemQuantity}</span></div>
            <div className="summary__total"><span>Subtotal</span><strong>{money(cart.subtotal)}</strong></div>
            {hasUnavailableItem ? <Notice message="Update or remove unavailable items before checkout." /> : <Link className="button button--primary button--full" to="/checkout">Continue to checkout</Link>}
            <button className="link-button" onClick={clear}>Clear cart</button>
          </aside>
        </div>}
  </main>
}
