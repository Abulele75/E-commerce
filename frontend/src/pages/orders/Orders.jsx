import { Link } from 'react-router-dom'
import Loader from '../../components/loader/Loader.jsx'
import Notice from '../../components/notice/Notice.jsx'
import { orderService } from '../../services/orders/orderService.js'
import { useAsync } from '../../hooks/async/useAsync.js'
import { dateTime, label, money } from '../../utils/format/formatters.js'

export default function Orders() {
  const { data: orders = [], loading, error } = useAsync(orderService.mine, [])
  return <main className="page"><header className="page-hero page-hero--small"><span className="eyebrow">Purchase history</span><h1>Your orders</h1></header><Notice message={error} />{loading ? <Loader /> : !orders?.length ? <section className="empty"><h2>No orders yet.</h2><Link className="button button--primary" to="/catalog">Shop now</Link></section> : <div className="data-list">{orders.map((order) => <Link className="order-card" to={`/orders/${order.orderId}`} key={order.orderId}><div><small>{order.orderId}</small><h3>{dateTime(order.createdAt)}</h3></div><span className={`status status--${order.orderStatus.toLowerCase()}`}>{label(order.orderStatus)}</span><strong>{money(order.financialBreakdown.finalInvoiceTotal)}</strong><span>View</span></Link>)}</div>}</main>
}