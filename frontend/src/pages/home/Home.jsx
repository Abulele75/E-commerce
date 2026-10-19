import { Navigate, useNavigate } from 'react-router-dom'
import StaggerGroup from '../../components/motion/stagger-group/StaggerGroup.jsx'
import StaggerItem from '../../components/motion/stagger-item/StaggerItem.jsx'
import HomeBentoHero from '../../components/home-bento-hero/HomeBentoHero.jsx'
import HomeCategories from '../../components/home-categories/HomeCategories.jsx'
import FeaturedRail from '../../components/featured-rail/FeaturedRail.jsx'
import AccountServiceCards from '../../components/account-service-cards/AccountServiceCards.jsx'
import { productService } from '../../services/products/productService.js'
import { useAsync } from '../../hooks/async/useAsync.js'
import { useAuth } from '../../hooks/auth/useAuth.js'
import { useCart } from '../../hooks/cart/useCart.js'

const showcaseCategories = ['LAPTOPS', 'SMARTPHONES', 'SMART_WATCHES']
const byNewest = (a, b) => new Date(b.createdAt || 0) - new Date(a.createdAt || 0) || String(a.productName).localeCompare(String(b.productName))
const seededPrice = (product) => Number(product.price ?? product.effectivePrice ?? 0)
const byLowestPrice = (a, b) => seededPrice(a) - seededPrice(b) || byNewest(a, b)

const mixCategories = (products, limit = 6, sorter = byNewest) => {
  const buckets = showcaseCategories.map((category) => products.filter((product) => product.category === category).sort(sorter))
  const mixed = []
  while (mixed.length < limit && buckets.some((bucket) => bucket.length)) {
    buckets.forEach((bucket) => { if (bucket.length && mixed.length < limit) mixed.push(bucket.shift()) })
  }
  return mixed
}

export default function Home() {
  const { session, profile, isAdmin } = useAuth()
  const navigate = useNavigate()
  const { addItem } = useCart()
  const { data: products = [], loading } = useAsync(productService.list, [])
  const list = products || []
  const eligible = list.filter((product) => showcaseCategories.includes(product.category))
  const newest = mixCategories(eligible)
  const newestIds = new Set(newest.map((product) => product.productId))
  const lowPrice = mixCategories(eligible.filter((product) => !newestIds.has(product.productId)), 6, byLowestPrice)
  const add = async (product) => { if (!session) return navigate('/login'); await addItem(product) }

  if (isAdmin) return <Navigate to="/admin" replace />
  return <>
    <HomeBentoHero session={session} firstName={profile?.firstName} />
    <HomeCategories products={list} />
    <FeaturedRail products={newest} loading={loading} onAdd={add} eyebrow="Just arrived" title="New products" linkLabel="View new products" />
    <FeaturedRail products={lowPrice} loading={loading} onAdd={add} eyebrow="Value picks" title="Low price products" emptyMessage="More low price products are coming soon." linkLabel="Browse all products" cardTag="Low price" />
    <AccountServiceCards />
    <StaggerGroup as="section" className="trust-strip"><StaggerItem><b>Secure Payments</b></StaggerItem><StaggerItem><b>Fast Delivery</b></StaggerItem><StaggerItem><b>Local Support</b></StaggerItem><StaggerItem><b>Quality Products</b></StaggerItem></StaggerGroup>
  </>
}
