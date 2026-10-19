import { Link } from 'react-router-dom'
import Reveal from '../../components/motion/reveal/Reveal.jsx'
import StaggerGroup from '../../components/motion/stagger-group/StaggerGroup.jsx'
import StaggerItem from '../../components/motion/stagger-item/StaggerItem.jsx'
import { storeAssets } from '../../constants/assets/storeAssets.js'
import './about.css'

const pillars = [
  ['01', 'Quality products', 'A focused technology range for work, connection and everyday life.'],
  ['02', 'Clear choices', 'Useful product information, filters and saved favourites make decisions simpler.'],
  ['03', 'Connected delivery', 'Orders and tracking stay together from checkout through to delivery.'],
  ['04', 'Local support', 'A Cape Town base serving customers across South Africa.'],
]

const journey = [
  ['Discover', 'Search categories, products and trusted technology brands.'],
  ['Choose', 'Compare details, save favourites and build your cart.'],
  ['Order', 'Move through checkout and simulated payment in one clear flow.'],
  ['Follow through', 'Return to your orders and track delivery progress.'],
]

export default function About() {
  return <main className="about-page">
    <section className="about-hero" aria-labelledby="about-title">
      <img className="about-hero__image" src={storeAssets.aboutHero} width="1086" height="1448" alt="" fetchPriority="high" />
      <div className="about-hero__shade" />
      <Reveal className="about-hero__copy">
        <span className="about-eyebrow">About Incredible Tech</span>
        <h1 id="about-title">Technology for the way you live.</h1>
        <p>Based in Cape Town and serving South Africa, Incredible Tech brings premium devices, components and everyday technology into one clear shopping experience.</p>
        <div className="about-actions"><Link className="button button--light" to="/catalog">Explore the range</Link><a className="about-text-link" href="#why-we-exist">Why we exist ↓</a></div>
      </Reveal>
    </section>

    <section className="about-section" id="why-we-exist" aria-labelledby="why-title">
      <Reveal className="about-heading"><span className="about-eyebrow about-eyebrow--dark">Why we exist</span><h2 id="why-title">Technology should keep up with real life.</h2><p>We focus the experience on what matters from discovery to delivery: useful products, clear choices and support close to home.</p></Reveal>
      <StaggerGroup className="about-pillars">{pillars.map(([number, title, copy]) => <StaggerItem className="about-pillar" key={title}><span>{number}</span><h3>{title}</h3><p>{copy}</p></StaggerItem>)}</StaggerGroup>
    </section>

    <section className="about-section about-platform" aria-labelledby="platform-title">
      <Reveal className="about-heading about-heading--wide"><span className="about-eyebrow about-eyebrow--dark">The platform</span><h2 id="platform-title">One journey, built to keep growing.</h2><p>Incredible Tech connects product discovery, saved favourites, checkout, orders and delivery tracking in one storefront.</p></Reveal>
      <StaggerGroup className="about-journey">{journey.map(([title, copy], index) => <StaggerItem className="about-journey__step" key={title}><span>{String(index + 1).padStart(2, '0')}</span><div><h3>{title}</h3><p>{copy}</p></div></StaggerItem>)}</StaggerGroup>
    </section>

    <section className="about-section about-footprint" aria-labelledby="footprint-title">
      <Reveal className="about-heading"><span className="about-eyebrow about-eyebrow--dark">Our footprint</span><h2 id="footprint-title">Local roots. A connected experience.</h2></Reveal>
      <StaggerGroup className="about-facts"><StaggerItem><strong>Cape Town</strong><span>Our home base</span></StaggerItem><StaggerItem><strong>South Africa</strong><span>The market we serve</span></StaggerItem><StaggerItem><strong>Browse to delivery</strong><span>One connected customer journey</span></StaggerItem></StaggerGroup>
    </section>

    <section className="about-team" aria-labelledby="team-title">
      <div className="about-team__media"><img src={storeAssets.aboutTeam} width="1868" height="842" alt="Team preparing technology orders in a warehouse" loading="lazy" decoding="async" /></div>
      <Reveal className="about-team__copy"><span className="about-eyebrow about-eyebrow--dark">The people behind the experience</span><h2 id="team-title">Technology retail takes a team.</h2><p>A dependable shopping journey brings product discovery, fulfilment and customer support together. Incredible Tech is designed to make those hand-offs feel clear from the first search to delivery tracking.</p></Reveal>
    </section>

    <section className="about-cta"><Reveal><span className="about-eyebrow">Find what keeps up</span><h2>Ready for your next piece of technology?</h2><p>Explore devices and components for work, connection and everyday life.</p><div className="about-actions"><Link className="button button--light" to="/catalog">Shop all products</Link><Link className="about-text-link" to="/tracking">Track an order →</Link></div></Reveal></section>
  </main>
}
