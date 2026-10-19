import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import App from './app/App.jsx'
import { AuthProvider } from './state/auth/AuthProvider.jsx'
import { CartProvider } from './state/cart/CartProvider.jsx'
import { WishlistProvider } from './state/wishlist/WishlistProvider.jsx'
import './styles/base/global.css'
import './styles/base/motion.css'
import './styles/layout/app.css'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <BrowserRouter>
      <AuthProvider><WishlistProvider><CartProvider><App /></CartProvider></WishlistProvider></AuthProvider>
    </BrowserRouter>
  </StrictMode>,
)