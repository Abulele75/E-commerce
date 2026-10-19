export const money = (value = 0) => new Intl.NumberFormat('en-ZA', {
  style: 'currency', currency: 'ZAR',
}).format(Number(value))

export const dateTime = (value) => value
  ? new Intl.DateTimeFormat('en-ZA', { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(value))
  : '—'

export const dateOnly = (value) => value
  ? new Intl.DateTimeFormat('en-ZA', { dateStyle: 'medium' }).format(new Date(value))
  : '—'

export const label = (value = '') => value.toLowerCase().replaceAll('_', ' ').replace(/\b\w/g, (c) => c.toUpperCase())

// Comparator that orders records by most recent activity first. Falls back to a
// secondary string field (e.g. an id) when timestamps are missing or equal.
export const byRecent = (field = 'createdAt', fallback = null) => (a, b) => {
  const diff = new Date(b?.[field] || 0) - new Date(a?.[field] || 0)
  if (diff !== 0 || !fallback) return diff
  return String(b?.[fallback] || '').localeCompare(String(a?.[fallback] || ''))
}

// Best-effort display name for a person, tolerant of the various shapes the API
// may return (top-level first/last name, a nested profile, or a full name).
export const personName = (p, fallback = 'Verified customer') => {
  if (!p) return fallback
  const source = p.firstName || p.lastName ? p : (p.accountProfile || p.profile || p.customer || p)
  const full = [source?.firstName, source?.lastName].filter(Boolean).join(' ').trim()
  return full || source?.fullName || source?.name || fallback
}
