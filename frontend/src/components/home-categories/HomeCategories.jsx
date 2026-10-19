import { Link } from 'react-router-dom'
import Reveal from '../motion/reveal/Reveal.jsx'
import StaggerGroup from '../motion/stagger-group/StaggerGroup.jsx'
import StaggerItem from '../motion/stagger-item/StaggerItem.jsx'
import { categories } from '../../constants/catalog/catalogOptions.js'
import { categoryImages } from '../../constants/assets/storeAssets.js'
import { label } from '../../utils/format/formatters.js'

const categoryThemes = {
  LAPTOPS: '#d8d2c8',
  DESKTOP_COMPUTERS: '#d96724',
  COMPUTER_COMPONENTS: '#113a5c',
  SMARTPHONES: '#087da5',
  SMART_WATCHES: '#a23d84',
  ACCESSORIES: '#61791d',
}

export default function HomeCategories({ products = [] }) {
  const countFor = (category) => products.filter((product) => product.category === category).length
  return <section className="section category-section">
    <Reveal className="section__heading"><h2>Shop by category</h2><Link to="/catalog">View all categories</Link></Reveal>
    <StaggerGroup className="category-strip">
      {categories.map((category) => {
        const count = countFor(category)
        return <StaggerItem key={category} className="category-strip__item">
          <Link className="category-card category-card--compact" style={{ '--category-bg': categoryThemes[category] }} to={`/catalog?category=${category}`} aria-label={`Shop ${label(category)}`}>
            <span className="category-card__visual"><img src={categoryImages[category]} alt="" aria-hidden="true" loading="lazy" decoding="async" /></span>
            <span className="category-card__pill"><strong>{label(category)}</strong><small>{count > 0 ? `${count} available` : 'Coming soon'}</small></span>
          </Link>
        </StaggerItem>
      })}
    </StaggerGroup>
  </section>
}
