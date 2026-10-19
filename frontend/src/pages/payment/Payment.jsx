import { useEffect, useState } from 'react'
import { useLocation, useParams } from 'react-router-dom'
import Loader from '../../components/loader/Loader.jsx'
import Notice from '../../components/notice/Notice.jsx'
import VirtualCard from '../../components/virtual-card/VirtualCard.jsx'
import CheckoutSteps from '../../components/checkout-steps/CheckoutSteps.jsx'
import OrderConfirmation from '../../components/order-confirmation/OrderConfirmation.jsx'
import PaymentProcessing from '../../components/payment-processing/PaymentProcessing.jsx'
import { paymentService } from '../../services/payments/paymentService.js'
import { orderService } from '../../services/orders/orderService.js'
import { useCart } from '../../hooks/cart/useCart.js'
import { cardBrands, walletProviders } from '../../constants/catalog/catalogOptions.js'
import { label, money } from '../../utils/format/formatters.js'

const emptyBilling = { billingName: '', streetAddress: '', suburb: '', city: '', province: '', postalCode: '', country: 'South Africa' }
const fromDelivery = (d) => d ? { billingName: d.recipientName || '', streetAddress: d.streetAddress || '', suburb: d.suburb || '', city: d.city || '', province: d.province || '', postalCode: d.postalCode || '', country: d.country || 'South Africa' } : { ...emptyBilling }
const paidStatuses = new Set(['PAID', 'PROCESSING', 'SHIPPED', 'DELIVERED'])

export default function Payment() {
  const { orderId } = useParams()
  const location = useLocation()
  const { refresh: refreshCart } = useCart()
  const [order, setOrder] = useState(location.state?.order || null)
  const [orderLoading, setOrderLoading] = useState(!location.state?.order)
  const [method, setMethod] = useState('CARD')
  const [card, setCard] = useState({ paymentToken: '', cardBrand: 'VISA', cardholderName: '', cardLastFourDigits: '' })
  const [wallet, setWallet] = useState({ walletProvider: 'PAYPAL', walletToken: '' })
  const [billing, setBilling] = useState(() => fromDelivery(location.state?.order?.deliveryAddress))
  const [sameAsDelivery, setSameAsDelivery] = useState(true)
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)
  const [confirmed, setConfirmed] = useState(() => paidStatuses.has(location.state?.order?.orderStatus))

  // Navigation state is only a fast initial render. Always fetch the order so
  // refreshes, another-tab checkout revisions, and completed payments recover.
  useEffect(() => {
    let active = true
    setOrderLoading(true)
    orderService.get(orderId)
      .then((data) => {
        if (!active) return
        setOrder(data)
        setBilling(fromDelivery(data.deliveryAddress))
        setConfirmed(paidStatuses.has(data.orderStatus))
        setError('')
      })
      .catch((err) => { if (active) setError(err.message || 'Unable to load this order') })
      .finally(() => { if (active) setOrderLoading(false) })
    return () => { active = false }
  }, [orderId])

  const breakdown = order?.financialBreakdown
  const total = breakdown?.finalInvoiceTotal
  const delivery = order?.deliveryAddress

  const changeCard = (e) => setCard({ ...card, [e.target.name]: e.target.value })
  const changeBilling = (e) => setBilling({ ...billing, [e.target.name]: e.target.value })

  const confirmPaidOrder = async (latestOrder) => {
    if (latestOrder) setOrder(latestOrder)
    else setOrder((current) => current ? { ...current, orderStatus: 'PAID' } : current)
    setConfirmed(true)
    setError('')
    try { await refreshCart() } catch { /* confirmation is authoritative */ }
  }

  const reconcilePayment = async () => {
    const [orderResult, paymentsResult] = await Promise.allSettled([
      orderService.get(orderId),
      paymentService.forOrder(orderId),
    ])
    const latestOrder = orderResult.status === 'fulfilled' ? orderResult.value : null
    const attempts = paymentsResult.status === 'fulfilled' ? paymentsResult.value : []
    const successful = attempts.some((attempt) => attempt.paymentStatus === 'SUCCESSFUL')
    if (latestOrder && paidStatuses.has(latestOrder.orderStatus)) { await confirmPaidOrder(latestOrder); return true }
    if (successful) { await confirmPaidOrder(latestOrder); return true }
    if (latestOrder) setOrder(latestOrder)
    return false
  }

  const submit = async (e) => {
    e.preventDefault(); setBusy(true); setError('')
    let paymentStarted = false
    try {
      const displayedOrder = order
      const latestOrder = await orderService.get(orderId)

      if (paidStatuses.has(latestOrder.orderStatus)) { await confirmPaidOrder(latestOrder); return }
      if (latestOrder.orderStatus !== 'PENDING_PAYMENT') {
        setOrder(latestOrder)
        setError(`This order is ${label(latestOrder.orderStatus).toLowerCase()} and cannot be paid.`)
        return
      }

      const displayedCents = Math.round(Number(displayedOrder?.financialBreakdown?.finalInvoiceTotal) * 100)
      const latestCents = Math.round(Number(latestOrder.financialBreakdown?.finalInvoiceTotal) * 100)
      const changed = displayedOrder?.updatedAt !== latestOrder.updatedAt || displayedCents !== latestCents
      setOrder(latestOrder)
      if (changed) {
        if (sameAsDelivery) setBilling(fromDelivery(latestOrder.deliveryAddress))
        setError('Your order changed. Review the updated items and total before simulating payment.')
        return
      }

      const billingForRequest = sameAsDelivery ? fromDelivery(latestOrder.deliveryAddress) : billing
      paymentStarted = true
      const result = method === 'CARD'
        ? await paymentService.payCard({ orderId, paymentToken: card.paymentToken, cardBrand: card.cardBrand, cardholderName: card.cardholderName, cardLastFourDigits: card.cardLastFourDigits, billingLocation: billingForRequest })
        : await paymentService.payWallet({ orderId, walletProvider: wallet.walletProvider, walletToken: wallet.walletToken, billingLocation: billingForRequest })

      if (result.paymentStatus === 'SUCCESSFUL') {
        let paidOrder = latestOrder
        try { paidOrder = await orderService.get(orderId) } catch { paidOrder = { ...latestOrder, orderStatus: 'PAID' } }
        await confirmPaidOrder(paidOrder)
      } else if (result.paymentStatus === 'FAILED') {
        setError(result.failureReason || 'Simulated payment was declined')
      } else if (!(await reconcilePayment())) {
        setError('Payment status could not be confirmed. Check your orders before trying again.')
      }
    } catch (err) {
      if (paymentStarted && await reconcilePayment()) return
      setError(paymentStarted && err?.status === 0
        ? 'Connection was lost and payment could not be confirmed. Check your orders before trying again.'
        : err?.message || 'Payment could not be processed')
    } finally { setBusy(false) }
  }

  if (orderLoading && !order) return <main className="page"><Loader /></main>

  if (confirmed && order) {
    return <main className="page payment-page">
      <header className="page-hero page-hero--small page-hero--flow"><span className="eyebrow">Order confirmed</span><h1>All done</h1></header>
      <OrderConfirmation order={order} />
    </main>
  }

  if (!order) return <main className="page narrow"><Notice message={error || 'Order not found'} /></main>
  if (order.orderStatus !== 'PENDING_PAYMENT') return <main className="page narrow"><Notice message={`This order is ${label(order.orderStatus).toLowerCase()} and cannot be paid.`} /></main>

  return <main className="page payment-page">
    {busy && <PaymentProcessing amount={total != null ? money(total) : ''} />}
    <header className="page-hero page-hero--small page-hero--flow"><span className="eyebrow">Final step</span><h1>Simulated payment {total != null && <small>{money(total)}</small>}</h1></header>
    <CheckoutSteps current={3} />
    <Notice message={error} />
    <div className="payment-layout">
      <form className="form panel payment-form" onSubmit={submit}>
        <div className="tabs">
          <button type="button" className={method === 'CARD' ? 'active' : ''} onClick={() => setMethod('CARD')}>Card</button>
          <button type="button" className={method === 'WALLET' ? 'active' : ''} onClick={() => setMethod('WALLET')}>Digital wallet</button>
        </div>
        <p className="payment-simulation-note">Simulation only — no real card, wallet, bank, or money is charged.</p>

        {method === 'CARD'
          ? <div className="pay-method">
              <VirtualCard brand={card.cardBrand} last4={card.cardLastFourDigits} name={card.cardholderName} />
              <div className="form form--grid pay-fields">
                <label className="span-2">Card brand<select name="cardBrand" value={card.cardBrand} onChange={changeCard}>{cardBrands.map((v) => <option key={v} value={v}>{label(v)}</option>)}</select></label>
                <label className="span-2">Cardholder name<input name="cardholderName" required value={card.cardholderName} onChange={changeCard} placeholder="Name on card" /></label>
                <label>Last 4 digits<input name="cardLastFourDigits" required inputMode="numeric" pattern="[0-9]{4}" maxLength="4" value={card.cardLastFourDigits} onChange={changeCard} placeholder="4242" /></label>
                <label>Payment token<input name="paymentToken" minLength="12" required value={card.paymentToken} onChange={changeCard} placeholder="tok_test_card_success_0001" /></label>
                <small className="span-2 form__hint">Use any 12+ character test token to succeed. Use <code>TEST-PAYMENT-DECLINED</code> with either method to simulate a decline.</small>
              </div>
            </div>
          : <div className="form form--grid pay-fields">
              <label>Wallet<select name="walletProvider" value={wallet.walletProvider} onChange={(e) => setWallet({ ...wallet, walletProvider: e.target.value })}>{walletProviders.map((v) => <option key={v} value={v}>{label(v)}</option>)}</select></label>
              <label>Wallet token<input name="walletToken" minLength="12" required value={wallet.walletToken} onChange={(e) => setWallet({ ...wallet, walletToken: e.target.value })} placeholder="tok_test_wallet_success_0001" /></label>
            </div>}

        <label className="pay-billing-toggle"><input type="checkbox" checked={sameAsDelivery} onChange={(e) => setSameAsDelivery(e.target.checked)} /><span>Billing address same as delivery{delivery ? ` (${delivery.streetAddress}, ${delivery.city})` : ''}</span></label>

        {!sameAsDelivery && <>
          <h2>Billing address</h2>
          <div className="form form--grid">{Object.keys(emptyBilling).map((key) => <label key={key} className={key === 'streetAddress' ? 'span-2' : ''}>{label(key)}<input name={key} required={key !== 'suburb'} value={billing[key]} onChange={changeBilling} /></label>)}</div>
        </>}

        <button className="button button--primary button--full" disabled={busy || orderLoading || total == null}>{busy ? 'Simulating…' : total != null ? `Simulate payment ${money(total)}` : 'Simulate payment'}</button>
      </form>

      <aside className="summary payment-summary">
        <h2>Order summary</h2>
        {order?.orderItems?.map((item) => <div key={item.orderItemId} className="pay-line"><span>{item.quantity}× {item.productName}</span><span>{money(item.lineTotal)}</span></div>)}
        {breakdown && <>
          <div className="pay-line pay-line--muted"><span>Subtotal</span><span>{money(breakdown.basketSubtotal)}</span></div>
          {Number(breakdown.discountAmount) > 0 && <div className="pay-line pay-line--muted"><span>Discount</span><span>−{money(breakdown.discountAmount)}</span></div>}
          {Number(breakdown.vatAmount) > 0 && <div className="pay-line pay-line--muted"><span>VAT</span><span>{money(breakdown.vatAmount)}</span></div>}
          <div className="pay-line pay-line--muted"><span>Delivery</span><span>{Number(breakdown.deliveryFee) > 0 ? money(breakdown.deliveryFee) : 'Free'}</span></div>
          <div className="pay-line pay-line--total"><span>Total</span><strong>{money(breakdown.finalInvoiceTotal)}</strong></div>
        </>}
      </aside>
    </div>
  </main>
}
