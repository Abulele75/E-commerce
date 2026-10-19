import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Notice from '../../components/notice/Notice.jsx'
import CheckoutSteps from '../../components/checkout-steps/CheckoutSteps.jsx'
import { orderService } from '../../services/orders/orderService.js'
import { provinces } from '../../constants/catalog/catalogOptions.js'

const initial = { recipientName: '', recipientPhone: '', streetAddress: '', suburb: '', city: '', province: '', postalCode: '', country: 'South Africa', deliveryInstructions: '' }

export default function Checkout() {
  const [form, setForm] = useState(initial)
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)
  const navigate = useNavigate()
  const change = (e) => setForm({ ...form, [e.target.name]: e.target.value })

  const submit = async (e) => {
    e.preventDefault(); setBusy(true); setError('')
    try {
      const order = await orderService.checkout(form)
      // The backend keeps the cart active until payment succeeds. Keep the
      // badge accurate too, so an abandoned/failed payment remains recoverable.
      navigate(`/payment/${order.orderId}`, { state: { order } })
    } catch (err) { setError(err.message) } finally { setBusy(false) }
  }

  return <main className="page narrow">
    <header className="page-hero page-hero--small page-hero--flow"><span className="eyebrow">Secure checkout</span><h1>Delivery details</h1></header>
    <CheckoutSteps current={2} />
    <Notice message={error} />
    <form className="flow-card" onSubmit={submit}>
      <div className="flow-card__body">
        <div className="form form--grid">
          <label>Recipient name<input name="recipientName" required minLength="2" maxLength="100" pattern="[A-Za-zÀ-ÖØ-öø-ÿ]+(?:[ '’-][A-Za-zÀ-ÖØ-öø-ÿ]+)*" value={form.recipientName} onChange={change} /></label>
          <label>Mobile number<input name="recipientPhone" required inputMode="tel" pattern="0(?:6[0-9]|7[0-9]|8[1-9])[0-9]{7}" maxLength="10" placeholder="0821234567" value={form.recipientPhone} onChange={change} /></label>
          <label className="span-2">Street address<input name="streetAddress" required maxLength="200" value={form.streetAddress} onChange={change} /></label>
          <label>Suburb<input name="suburb" maxLength="100" value={form.suburb} onChange={change} /></label>
          <label>City<input name="city" required maxLength="100" value={form.city} onChange={change} /></label>
          <label>Province
            <select name="province" required value={form.province} onChange={change}>
              <option value="" disabled>Select a province</option>
              {provinces.map((p) => <option key={p} value={p}>{p}</option>)}
            </select>
          </label>
          <label>Postal code<input name="postalCode" required inputMode="numeric" pattern="[0-9]{4}" maxLength="4" value={form.postalCode} onChange={change} /></label>
          <label className="span-2">Delivery instructions<textarea name="deliveryInstructions" maxLength="500" value={form.deliveryInstructions} onChange={change} /></label>
        </div>
      </div>
      <div className="flow-card__footer">
        <button className="button button--primary button--full" disabled={busy}>{busy ? 'Creating order…' : 'Continue to payment'}</button>
      </div>
    </form>
  </main>
}
