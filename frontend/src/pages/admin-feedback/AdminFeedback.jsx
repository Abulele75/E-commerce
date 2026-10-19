import { useState } from 'react'
import AdminShell from '../../components/admin-shell/AdminShell.jsx'
import Loader from '../../components/loader/Loader.jsx'
import Notice from '../../components/notice/Notice.jsx'
import { feedbackService } from '../../services/feedback/feedbackService.js'
import { useAsync } from '../../hooks/async/useAsync.js'
import { byRecent, dateOnly, personName } from '../../utils/format/formatters.js'

export default function AdminFeedback() {
  const { data: feedback = [], setData, loading, error } = useAsync(feedbackService.all, [])
  const [deletingId, setDeletingId] = useState('')
  const [message, setMessage] = useState('')
  const remove = async (id) => {
    if (deletingId || !confirm('Delete this feedback?')) return
    setDeletingId(id); setMessage('')
    try { await feedbackService.remove(id); setData((current) => (current || []).filter((item) => item.feedbackId !== id)); setMessage('Feedback deleted.') }
    catch (err) { setMessage(err?.message || 'Feedback could not be deleted') }
    finally { setDeletingId('') }
  }
  const rows = [...(feedback || [])].sort(byRecent('dateSubmitted', 'feedbackId'))
  return <AdminShell title="Feedback"><Notice message={error || message} type={message === 'Feedback deleted.' ? 'success' : 'error'} />
    {loading ? <Loader /> : rows.length ? <div className="data-list">{rows.map((item) => {
      const name = personName(item.customer, item.reviewerCustomerId || 'Verified customer')
      const rating = item.hardwareStarRating ?? item.courierFulfillmentRating
      const text = item.comprehensiveReviewText || item.packageConditionFeedback || 'No comment provided.'
      return <article className="feedback-card" key={item.feedbackId}><span className="feedback-card__avatar" aria-hidden="true">{(name[0] || 'C').toUpperCase()}</span><div className="feedback-card__body"><div className="feedback-card__who"><b>{name}</b>{rating != null && <span className="stars">{'★★★★★'.slice(0, rating)}{'☆☆☆☆☆'.slice(0, 5 - rating)}</span>}</div><p>{text}</p><small>{item.feedbackId} · {dateOnly(item.dateSubmitted)}</small></div><button type="button" className="button button--danger" disabled={Boolean(deletingId)} onClick={() => remove(item.feedbackId)}>{deletingId === item.feedbackId ? 'Deleting…' : 'Delete'}</button></article>
    })}</div> : <p className="empty">No feedback submitted yet.</p>}
  </AdminShell>
}
