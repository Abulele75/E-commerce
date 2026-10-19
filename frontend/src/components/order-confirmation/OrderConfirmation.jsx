import { Link } from 'react-router-dom'
import CheckoutSteps from '../checkout-steps/CheckoutSteps.jsx'
import { money } from '../../utils/format/formatters.js'
import './order-confirmation.css'

/**
 * Full-screen liquid-glass confirmation shown after a successful payment.
 * Presents an animated tick plus the essentials of the order.
 */
export default function OrderConfirmation({ order }) {
  const total = order?.financialBreakdown?.finalInvoiceTotal
  const delivery = order?.deliveryAddress
  const itemCount = (order?.orderItems || []).reduce((sum, item) => sum + item.quantity, 0)

  return (
    <div className="order-confirm" role="dialog" aria-modal="true" aria-labelledby="order-confirm-title">
      <div className="order-confirm__card">
        <div className="order-confirm__tick" aria-hidden="true">
          <svg viewBox="0 0 52 52">
            <circle className="oc-circle" cx="26" cy="26" r="23" />
            <path className="oc-check" d="M15 27l7 7 15-15" />
          </svg>
        </div>
        <span className="eyebrow">Payment successful</span>
        <h1 id="order-confirm-title">Thank you for your order</h1>
        <p>Your payment went through and your order is confirmed. A receipt is on its way to your inbox.</p>

        <CheckoutSteps current={4} />

        <dl className="order-confirm__facts">
          <div><dt>Order number</dt><dd>{order.orderId}</dd></div>
          <div><dt>Items</dt><dd>{itemCount}</dd></div>
          {total != null && <div><dt>Total paid</dt><dd>{money(total)}</dd></div>}
          {delivery && <div className="order-confirm__facts-wide"><dt>Delivering to</dt><dd>{delivery.recipientName} — {delivery.streetAddress}, {delivery.city} {delivery.postalCode}</dd></div>}
        </dl>

        <div className="order-confirm__actions">
          <Link className="button button--primary button--full" to={`/orders/${order.orderId}`}>View order &amp; tracking</Link>
          <Link className="button button--ghost button--full" to="/catalog">Continue shopping</Link>
        </div>
      </div>
    </div>
  )
}
