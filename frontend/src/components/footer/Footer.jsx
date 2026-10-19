import { Link } from 'react-router-dom'; import { storeAssets } from '../../constants/assets/storeAssets.js'

export default function Footer() {
  return <footer className="footer"><div className="footer__grid">
    <div><Link className="brand brand--footer" to="/"><img src={storeAssets.logo} alt="Incredible Tech" /></Link><p>Your trusted destination for premium technology. Based in Cape Town, serving all of South Africa.</p></div>
    <div><strong>Shop</strong><Link to="/catalog">All Products</Link><Link to="/catalog?category=LAPTOPS">Laptops</Link><Link to="/catalog?category=SMARTPHONES">Smartphones</Link></div>
    <div><strong>Customer Service</strong><Link to="/orders">My Orders</Link><Link to="/tracking">Track My Order</Link><Link to="/profile">My Account</Link></div>
    <div><strong>About</strong><Link to="/about">About Us</Link><span>Contact Us</span><span>Cape Town, South Africa</span></div>
  </div><div className="footer__bottom"><span>© {new Date().getFullYear()} Incredible Tech. All rights reserved.</span><span>Terms & Conditions　 Privacy Policy　 Cookie Policy</span></div></footer>
}