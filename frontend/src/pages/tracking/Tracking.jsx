import { useState } from 'react'
import Notice from '../../components/notice/Notice.jsx'
import { deliveryService } from '../../services/deliveries/deliveryService.js'
import { dateTime, label } from '../../utils/format/formatters.js'

export default function Tracking() {
  const [number, setNumber] = useState(''); const [events, setEvents] = useState([]); const [error, setError] = useState(''); const [busy, setBusy] = useState(false); const [searched, setSearched] = useState(false)
  const submit = async (e) => {
    e.preventDefault(); setBusy(true); setError(''); setSearched(true)
    try {
      const value = number.trim()
      const result = value.toUpperCase().startsWith('ORD-')
        ? await deliveryService.trackOrder(value)
        : await deliveryService.trackNumber(value)
      setEvents(result || [])
    } catch (err) { setError(err.message); setEvents([]) } finally { setBusy(false) }
  }
  return <main className="page narrow"><header className="page-hero page-hero--small"><span className="eyebrow">Delivery updates</span><h1>Track your order</h1></header><form className="search-box" onSubmit={submit}><input required placeholder="Enter your order number or tracking number" value={number} onChange={(e) => setNumber(e.target.value)} /><button className="button button--primary" disabled={busy}>Track</button></form><Notice message={error} />{events.length ? <ol className="timeline">{events.map((event) => <li key={event.trackingId}><span /><div><b>{label(event.deliveryStatus)}</b><p>{event.statusMessage}</p><small>{event.currentLocation || 'Location unavailable'} · {dateTime(event.updateTimestamp)}</small></div></li>)}</ol> : searched && !busy && !error && <p className="empty">No tracking updates found for that number.</p>}</main>
}