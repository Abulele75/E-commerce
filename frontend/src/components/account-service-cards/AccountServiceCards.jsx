import { Link } from 'react-router-dom'
import StaggerGroup from '../motion/stagger-group/StaggerGroup.jsx'
import StaggerItem from '../motion/stagger-item/StaggerItem.jsx'
import { storeAssets } from '../../constants/assets/storeAssets.js'
import './account-service-cards.css'

const services = [
  {
    title: 'My Orders',
    copy: 'Track purchases, review order details and stay updated.',
    action: 'View my orders',
    to: '/orders',
    image: storeAssets.myOrders,
    icon: 'box',
  },
  {
    title: 'Track Delivery',
    copy: 'Follow delivery progress and view the latest updates.',
    action: 'Track delivery',
    to: '/tracking',
    image: storeAssets.myDelivery,
    icon: 'truck',
  },
  {
    title: 'My Account',
    copy: 'Manage your profile, password and account information.',
    action: 'Manage account',
    to: '/profile',
    image: storeAssets.myAccount,
    icon: 'person',
  },
]

function ServiceIcon({ name }) {
  if (name === 'truck') return <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M3 6h11v10H3zM14 10h4l3 3v3h-7zM7 19a2 2 0 1 0 0-4 2 2 0 0 0 0 4Zm10 0a2 2 0 1 0 0-4 2 2 0 0 0 0 4Z" /></svg>
  if (name === 'person') return <svg viewBox="0 0 24 24" aria-hidden="true"><circle cx="12" cy="8" r="4" /><path d="M4.5 21c.7-4.2 3.2-6.3 7.5-6.3s6.8 2.1 7.5 6.3" /></svg>
  return <svg viewBox="0 0 24 24" aria-hidden="true"><path d="m4 7 8-4 8 4-8 4-8-4Zm0 0v10l8 4 8-4V7M12 11v10" /></svg>
}

export default function AccountServiceCards() {
  return <section className="section account-services" aria-labelledby="account-services-title">
    <div className="account-services__heading">
      <span>Customer services</span>
      <h2 id="account-services-title">Everything you need, one tap away.</h2>
    </div>
    <StaggerGroup className="account-services__grid">
      {services.map((service) => <StaggerItem key={service.title} className="account-service-card">
        <Link to={service.to} className="account-service-card__image" aria-label={service.action}>
          <img src={service.image} alt={`${service.title} service`} loading="lazy" decoding="async" />
        </Link>
        <div className="account-service-card__body">
          <span className="account-service-card__icon"><ServiceIcon name={service.icon} /></span>
          <div className="account-service-card__copy"><h3>{service.title}</h3><p>{service.copy}</p></div>
          <Link className="account-service-card__action" to={service.to}>{service.action}<span aria-hidden="true">→</span></Link>
        </div>
      </StaggerItem>)}
    </StaggerGroup>
  </section>
}
