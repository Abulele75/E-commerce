import { useState } from 'react'
import AdminShell from '../../components/admin-shell/AdminShell.jsx'
import Loader from '../../components/loader/Loader.jsx'
import Notice from '../../components/notice/Notice.jsx'
import { paymentService } from '../../services/payments/paymentService.js'
import { useAsync } from '../../hooks/async/useAsync.js'
import { byRecent, dateTime, label, money } from '../../utils/format/formatters.js'

export default function AdminPayments() {
  const { data: payments = [], setData, loading, error } = useAsync(paymentService.all, [])
  const [refundingId, setRefundingId] = useState('')
  const [operation, setOperation] = useState({ message: '', type: 'error' })

  const refund = async (id) => {
    if (refundingId) return
    if (!confirm('Simulate this refund? This updates the order and restores stock, but no real funds are returned.')) return
    setRefundingId(id); setOperation({ message: '', type: 'error' })
    try {
      const refunded = await paymentService.refund(id)
      setData((current) => (current || []).map((payment) => payment.transactionId === id ? refunded : payment))
      setOperation({ message: 'Simulated refund completed and product stock was restored.', type: 'success' })
    } catch (err) {
      setOperation({ message: err?.message || 'The simulated refund could not be completed.', type: 'error' })
    } finally { setRefundingId('') }
  }

  const rows = [...(payments || [])].sort(byRecent('createdAt', 'transactionId'))
  return <AdminShell title="Simulated payments">
    <p className="admin-disclosure">Demo ledger only — no real provider charges or refunds occur.</p>
    <Notice message={operation.message || error} type={operation.message ? operation.type : 'error'} />
    {loading ? <Loader /> : <div className="table-wrap"><table><thead><tr><th>Transaction</th><th>Order</th><th>Method</th><th>Amount</th><th>Status</th><th /></tr></thead><tbody>{rows.map((payment) => <tr key={payment.transactionId}><td><b>{payment.transactionId}</b><small>{dateTime(payment.createdAt)}</small></td><td>{payment.orderId}</td><td>{payment.paymentDescription}</td><td>{money(payment.capturedAmount)}</td><td><span className={`status status--${payment.paymentStatus.toLowerCase()}`}>{label(payment.paymentStatus)}</span></td><td>{payment.paymentStatus === 'SUCCESSFUL' && <button type="button" className="button button--ghost" disabled={Boolean(refundingId)} onClick={() => refund(payment.transactionId)}>{refundingId === payment.transactionId ? 'Refunding…' : 'Simulate refund'}</button>}</td></tr>)}</tbody></table></div>}
  </AdminShell>
}
