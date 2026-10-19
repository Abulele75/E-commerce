import { useEffect } from 'react'
import { Route, Routes, useLocation } from 'react-router-dom'
import { AnimatePresence } from 'motion/react'
import Header from '../components/header/Header.jsx'
import MobileBottomNav from '../components/mobile-bottom-nav/MobileBottomNav.jsx'
import Footer from '../components/footer/Footer.jsx'
import ProtectedRoute from '../components/protected-route/ProtectedRoute.jsx'
import PageTransition from '../components/motion/page-transition/PageTransition.jsx'
import Home from '../pages/home/Home.jsx'; import About from '../pages/about/About.jsx'; import Catalog from '../pages/catalog/Catalog.jsx'; import ProductDetails from '../pages/product/ProductDetails.jsx'
import Login from '../pages/auth/Login.jsx'; import Register from '../pages/auth/Register.jsx'; import ForgotPassword from '../pages/auth/ForgotPassword.jsx'; import ResetPassword from '../pages/auth/ResetPassword.jsx'; import Cart from '../pages/cart/Cart.jsx'; import Checkout from '../pages/checkout/Checkout.jsx'; import Payment from '../pages/payment/Payment.jsx'
import Orders from '../pages/orders/Orders.jsx'; import OrderDetails from '../pages/orders/OrderDetails.jsx'; import Wishlist from '../pages/wishlist/Wishlist.jsx'; import Profile from '../pages/profile/Profile.jsx'; import Tracking from '../pages/tracking/Tracking.jsx'
import AdminDashboard from '../pages/admin-dashboard/AdminDashboard.jsx'; import AdminUsers from '../pages/admin-users/AdminUsers.jsx'; import AdminProducts from '../pages/admin-products/AdminProducts.jsx'; import AdminOrders from '../pages/admin-orders/AdminOrders.jsx'; import AdminPayments from '../pages/admin-payments/AdminPayments.jsx'; import AdminDeliveries from '../pages/admin-deliveries/AdminDeliveries.jsx'; import AdminFeedback from '../pages/admin-feedback/AdminFeedback.jsx'

const Guard = ({ children, admin = false }) => <ProtectedRoute admin={admin}>{children}</ProtectedRoute>

export default function App() {
  const location = useLocation()

  useEffect(() => {
    const previous = window.history.scrollRestoration
    window.history.scrollRestoration = 'manual'
    return () => { window.history.scrollRestoration = previous }
  }, [])

  return <div className="app"><Header /><MobileBottomNav /><div className="app__content">
    <AnimatePresence mode="wait" initial={false}>
      <PageTransition key={location.pathname}>
        <Routes location={location}>
          <Route path="/" element={<Home />} /><Route path="/about" element={<About />} /><Route path="/catalog" element={<Catalog />} /><Route path="/products/:productId" element={<ProductDetails />} /><Route path="/login" element={<Login />} /><Route path="/register" element={<Register />} /><Route path="/forgot-password" element={<ForgotPassword />} /><Route path="/reset-password" element={<ResetPassword />} />
          <Route path="/cart" element={<Guard><Cart /></Guard>} /><Route path="/wishlist" element={<Guard><Wishlist /></Guard>} /><Route path="/checkout" element={<Guard><Checkout /></Guard>} /><Route path="/payment/:orderId" element={<Guard><Payment /></Guard>} /><Route path="/orders" element={<Guard><Orders /></Guard>} /><Route path="/orders/:orderId" element={<Guard><OrderDetails /></Guard>} /><Route path="/profile" element={<Guard><Profile /></Guard>} /><Route path="/tracking" element={<Guard><Tracking /></Guard>} />
          <Route path="/admin" element={<Guard admin><AdminDashboard /></Guard>} /><Route path="/admin/users" element={<Guard admin><AdminUsers /></Guard>} /><Route path="/admin/products" element={<Guard admin><AdminProducts /></Guard>} /><Route path="/admin/orders" element={<Guard admin><AdminOrders /></Guard>} /><Route path="/admin/payments" element={<Guard admin><AdminPayments /></Guard>} /><Route path="/admin/deliveries" element={<Guard admin><AdminDeliveries /></Guard>} /><Route path="/admin/feedback" element={<Guard admin><AdminFeedback /></Guard>} />
          <Route path="*" element={<main className="page empty"><h1>Page not found</h1><a href="/">Return home</a></main>} />
        </Routes>
      </PageTransition>
    </AnimatePresence>
  </div><Footer /></div>
}
