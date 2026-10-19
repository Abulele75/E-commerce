import { Link } from 'react-router-dom'
import AdminShell from '../../components/admin-shell/AdminShell.jsx'
import Loader from '../../components/loader/Loader.jsx'
import Notice from '../../components/notice/Notice.jsx'
import { dashboardService } from '../../services/dashboard/dashboardService.js'
import { useAsync } from '../../hooks/async/useAsync.js'
import { dateTime, label, money } from '../../utils/format/formatters.js'

const areas = [
  ['users', 'Customers & access', 'Activate or deactivate store accounts.'],
  ['products', 'Product catalogue', 'Create, edit and retire inventory.'],
  ['orders', 'Order operations', 'Move paid orders through fulfilment.'],
  ['payments', 'Payment ledger', 'Review transactions and issue refunds.'],
  ['deliveries', 'Delivery desk', 'Assign carriers and publish tracking.'],
  ['feedback', 'Feedback', 'Review and remove submitted feedback.'],
]

export default function AdminDashboard() {
  const { data, loading, error } = useAsync(dashboardService.summary, [])
  const metrics = data ? [
    ['Users', data.totalUsers, `${data.enabledUsers} enabled`],
    ['Recent logins', data.recentLoginsInPeriod, 'Last 30 days'],
    ['Registrations', data.registrationsInPeriod, 'Last 30 days'],
    ['Active carts', data.activeCarts, 'Current shopping activity'],
    ['Orders', data.totalOrders, `${data.pendingOrders} awaiting payment`],
    ['Successful revenue', money(data.successfulRevenue), `${data.successfulPayments} payments`],
    ['Feedback', data.totalFeedback, 'Customer submissions'],
  ] : []

  return <AdminShell title="Operations dashboard">
    <Notice message={error} />
    {loading ? <Loader /> : <>
      <section className="dashboard-metrics" aria-label="Store activity">
        {metrics.map(([title, value, detail]) => <article className="dashboard-metric" key={title}><small>{title}</small><strong>{value}</strong><span>{detail}</span></article>)}
      </section>

      <div className="admin-grid">{areas.map(([path, title, copy], index) => <Link to={`/admin/${path}`} className="admin-tile" key={path}><span>0{index + 1}</span><h2>{title}</h2><p>{copy}</p><b>Manage</b></Link>)}</div>

      <section className="admin-section">
        <div className="section__heading section__heading--flush"><h2>Recent payments</h2><Link to="/admin/payments">View all</Link></div>
        {data?.recentPayments?.length
          ? <div className="table-wrap"><table>
              <thead><tr><th>Transaction</th><th>Order</th><th>Amount</th><th>Status</th></tr></thead>
              <tbody>{data.recentPayments.map((payment) => <tr key={payment.transactionId}>
                <td><b>{payment.transactionId}</b><small>{dateTime(payment.createdAt)}</small></td>
                <td>{payment.orderId}</td>
                <td>{money(payment.capturedAmount)}</td>
                <td><span className={`status status--${payment.paymentStatus.toLowerCase()}`}>{label(payment.paymentStatus)}</span></td>
              </tr>)}</tbody>
            </table></div>
          : <p className="empty">No payments recorded yet.</p>}
      </section>
    </>}
  </AdminShell>
}
