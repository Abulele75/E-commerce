import { useMemo, useState } from 'react'
import AdminShell from '../../components/admin-shell/AdminShell.jsx'
import Loader from '../../components/loader/Loader.jsx'
import Notice from '../../components/notice/Notice.jsx'
import { deliveryService } from '../../services/deliveries/deliveryService.js'
import { orderService } from '../../services/orders/orderService.js'
import { useAsync } from '../../hooks/async/useAsync.js'
import { carrierServices } from '../../constants/catalog/catalogOptions.js'
import { byRecent, label } from '../../utils/format/formatters.js'

const blankEvent = { deliveryStatus: 'DISPATCHED', currentLocation: '', statusMessage: '' }
const blankAssign = { orderId: '', trackingNumber: '', carrier: carrierServices[0], carrierContact: '' }
const nextStatuses = {
  PREPARING: ['DISPATCHED', 'CANCELLED'],
  DISPATCHED: ['IN_TRANSIT', 'CANCELLED'],
  IN_TRANSIT: ['OUT_FOR_DELIVERY', 'DELIVERY_FAILED', 'CANCELLED'],
  OUT_FOR_DELIVERY: ['DELIVERED', 'DELIVERY_FAILED'],
  DELIVERY_FAILED: ['OUT_FOR_DELIVERY', 'CANCELLED'],
}

export default function AdminDeliveries() {
  const { data: deliveries = [], loading, error, reload } = useAsync(deliveryService.all, [])
  const { data: orders = [] } = useAsync(() => orderService.all(), [])
  const [assign, setAssign] = useState(blankAssign)
  const [event, setEvent] = useState(blankEvent)
  const [selected, setSelected] = useState('')
  const [message, setMessage] = useState('')
  const [busy, setBusy] = useState(false)

  // Orders that are paid but not yet handed to a carrier.
  const assignedOrderIds = useMemo(() => new Set((deliveries || []).map((d) => d.orderId)), [deliveries])
  const pending = useMemo(
    () => (orders || [])
      .filter((o) => ['PAID', 'PROCESSING', 'SHIPPED'].includes(o.orderStatus) && !assignedOrderIds.has(o.orderId))
      .sort(byRecent('createdAt', 'orderId')),
    [orders, assignedOrderIds],
  )
  const selectedDelivery = (deliveries || []).find((d) => d.deliveryId === selected)
  const chosen = (orders || []).find((o) => o.orderId === assign.orderId)
  const addr = chosen?.deliveryAddress
  const recentDeliveries = [...(deliveries || [])].sort(byRecent('createdAt', 'deliveryId'))

  const allowedStatuses = nextStatuses[selectedDelivery?.deliveryStatus] || []

  const create = async (e) => {
    e.preventDefault(); setBusy(true); setMessage('')
    try {
      if (!addr) throw new Error('Select an order to assign.')
      await deliveryService.create({
        orderId: assign.orderId,
        trackingNumber: assign.trackingNumber,
        carrier: assign.carrier,
        carrierContact: assign.carrierContact,
      })
      setAssign(blankAssign)
      setMessage('Delivery created and published to the customer.')
      await reload()
    } catch (err) { setMessage(err.message) } finally { setBusy(false) }
  }

  const track = async (e) => {
    e.preventDefault()
    try {
      const updated = await deliveryService.addTracking(selected, event)
      setEvent({ ...blankEvent, deliveryStatus: nextStatuses[updated.deliveryStatus]?.[0] || '' })
      setMessage('Tracking update published.')
      await reload()
    }
    catch (err) { setMessage(err.message) }
  }

  const selectDelivery = (delivery) => {
    setSelected(delivery.deliveryId)
    setEvent({ ...blankEvent, deliveryStatus: nextStatuses[delivery.deliveryStatus]?.[0] || '' })
  }

  return <AdminShell title="Delivery desk">
    <Notice message={error || message} type={/publish|created/.test(message) ? 'success' : 'error'} />
    <div className="admin-columns">
      <form className="form panel" onSubmit={create}>
        <h2>Assign a carrier</h2>
        <label>Order awaiting delivery
          <select required value={assign.orderId} onChange={(e) => setAssign({ ...assign, orderId: e.target.value })}>
            <option value="" disabled>Select a paid order…</option>
            {pending.map((o) => <option key={o.orderId} value={o.orderId}>{o.orderId} — {o.deliveryAddress?.recipientName || o.deliveryAddress?.city || 'Customer'}</option>)}
          </select>
        </label>
        {addr && <div className="delivery-recap">
          <span className="drawer__label">Deliver to</span>
          <p>{addr.recipientName}{addr.recipientPhone ? ` · ${addr.recipientPhone}` : ''}<br />{addr.streetAddress}<br />{addr.suburb && `${addr.suburb}, `}{addr.city}<br />{addr.province}, {addr.postalCode}</p>
        </div>}
        <label>Carrier service
          <select value={assign.carrier} onChange={(e) => setAssign({ ...assign, carrier: e.target.value })}>{carrierServices.map((c) => <option key={c}>{c}</option>)}</select>
        </label>
        <label>Carrier contact<input value={assign.carrierContact} onChange={(e) => setAssign({ ...assign, carrierContact: e.target.value })} placeholder="Phone or email" /></label>
        <label>Tracking number<input required value={assign.trackingNumber} onChange={(e) => setAssign({ ...assign, trackingNumber: e.target.value })} placeholder="Carrier tracking number" /></label>
        <button className="button button--primary" disabled={busy || !assign.orderId}>{busy ? 'Publishing…' : 'Create & publish'}</button>
        {!pending.length && <small className="form__hint">No paid orders are waiting for a carrier right now.</small>}
      </form>

      <section>
        {selected && <form className="form panel publish-form" onSubmit={track}>
          <div className="publish-form__head"><h2>Publish tracking update</h2><button type="button" className="link-button" onClick={() => setSelected('')}>Close</button></div>
          <small className="publish-form__meta">{selectedDelivery?.trackingNumber} · order {selectedDelivery?.orderId}</small>
          <label>Status<select required value={event.deliveryStatus} onChange={(e) => setEvent({ ...event, deliveryStatus: e.target.value })}><option value="" disabled>Select next status</option>{allowedStatuses.map((s) => <option key={s}>{s}</option>)}</select></label>
          <label>Current location<input placeholder="e.g. Cape Town depot" value={event.currentLocation} onChange={(e) => setEvent({ ...event, currentLocation: e.target.value })} /></label>
          <label className="publish-form__wide">Message<textarea required rows={2} value={event.statusMessage} onChange={(e) => setEvent({ ...event, statusMessage: e.target.value })} /></label>
          <button className="button button--primary publish-form__wide">Publish update</button>
        </form>}
        {loading ? <Loader /> : <div className="data-list">{recentDeliveries.map((d) => <button type="button" className={`delivery-card ${selected === d.deliveryId ? 'active' : ''}`} key={d.deliveryId} onClick={() => selectDelivery(d)}><b>{d.trackingNumber}</b><span>{d.orderId}</span><small>{label(d.deliveryStatus)}</small></button>)}</div>}
      </section>
    </div>
  </AdminShell>
}
