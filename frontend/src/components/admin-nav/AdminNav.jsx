import { NavLink } from 'react-router-dom'

const links = [['/admin', 'Overview'], ['/admin/users', 'Users'], ['/admin/products', 'Products'], ['/admin/orders', 'Orders'], ['/admin/payments', 'Payments'], ['/admin/deliveries', 'Deliveries'], ['/admin/feedback', 'Feedback']]
export default function AdminNav() {
  return <nav className="admin-nav" aria-label="Administration">{links.map(([to, text]) => <NavLink end={to === '/admin'} key={to} to={to}>{text}</NavLink>)}</nav>
}