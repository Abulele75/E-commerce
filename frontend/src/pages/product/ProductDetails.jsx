import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import Loader from '../../components/loader/Loader.jsx'
import Notice from '../../components/notice/Notice.jsx'
import WishlistButton from '../../components/wishlist-button/WishlistButton.jsx'
import { productService } from '../../services/products/productService.js'
import { feedbackService } from '../../services/feedback/feedbackService.js'
import { useAsync } from '../../hooks/async/useAsync.js'
import { useAuth } from '../../hooks/auth/useAuth.js'
import { useCart } from '../../hooks/cart/useCart.js'
import { dateOnly, label, money, personName } from '../../utils/format/formatters.js'

const Stars = ({ value = 0 }) => <span className="stars" aria-label={`${value} out of 5`}>{'★★★★★'.slice(0, value)}{'☆☆☆☆☆'.slice(0, 5 - value)}</span>
const reviewerOf = (review) => personName(review.customer, review.reviewerCustomerId ? 'Verified customer' : 'Customer')

export default function ProductDetails()
{
  const { productId } = useParams();
  const { session, isAdmin } = useAuth();
  const { addItem } = useCart();
  const navigate = useNavigate();
  const [quantity, setQuantity] = useState(1);
  const [message, setMessage] = useState('')
  const { data: product, loading, error } = useAsync(() => productService.get(productId), [productId])
  const { data: reviews = [], loading: reviewsLoading, reload: reloadReviews } = useAsync(() => feedbackService.byProduct(productId), [productId])
  const [rating, setRating] = useState(5)
  const [reviewText, setReviewText] = useState('')
  const [reviewMessage, setReviewMessage] = useState('')
  const [submitting, setSubmitting] = useState(false)

  const add = async () =>
  {
      if (!session) return navigate('/login');
      try { await addItem(product, quantity); setMessage('Added to your cart.') }
      catch (err) { setMessage(err.message) }
  }

  const submitReview = async (event) => {
    event.preventDefault()
    if (!session) return navigate('/login')
    setSubmitting(true); setReviewMessage('')
    try {
      await feedbackService.createProductReview({ targetProductId: productId, hardwareStarRating: Number(rating), comprehensiveReviewText: reviewText.trim() })
      setReviewText(''); setRating(5); setReviewMessage('Thanks! Your review has been posted.')
      reloadReviews()
    } catch (err) { setReviewMessage(err.message) } finally { setSubmitting(false) }
  }

  if (loading)
      return <main className="page"><Loader /></main>
  if (error || !product)
      return <main className="page"><Notice message={error || 'Product not found'} /></main>

  const list = reviews || []
  const average = list.length ? Math.round((list.reduce((sum, r) => sum + (r.hardwareStarRating || 0), 0) / list.length) * 10) / 10 : 0

  const maxQuantity = Math.min(99, Math.max(0, Number(product.stockQuantity) || 0))
  const validQuantity = Number.isInteger(quantity) && quantity >= 1 && quantity <= maxQuantity

  const hasSpec = (value) => value != null && String(value).trim() !== '' && String(value).trim() !== '—'

  return <main className="page product-detail"><div className="product-detail__gallery"><WishlistButton product={product} className="wishlist-button--detail" /><img src={product.primaryImageUrl || '/placeholder.svg'} alt={product.productName} /></div><section><span className="eyebrow">{label(product.brand)} · {label(product.category)}</span><h1>{product.productName}</h1><div className="price"><strong>{money(product.effectivePrice ?? product.price)}</strong>{product.discountPrice && <del>{money(product.price)}</del>}</div><p className="lead">{product.description}</p><dl className="specs">{hasSpec(product.processor) && <div><dt>Processor</dt><dd>{product.processor}</dd></div>}{hasSpec(product.memory) && <div><dt>Memory</dt><dd>{product.memory}</dd></div>}{hasSpec(product.storage) && <div><dt>Storage</dt><dd>{product.storage}</dd></div>}<div><dt>Colour</dt><dd>{product.colour || '—'}</dd></div><div><dt>Availability</dt><dd>{product.stockQuantity} in stock</dd></div></dl><Notice message={message} type={message.startsWith('Added') ? 'success' : 'error'} />{!isAdmin && <div className="buy-row"><input type="number" min="1" max={maxQuantity} value={quantity} onChange={(e) => { const next = Number(e.target.value); setQuantity(Number.isFinite(next) ? next : 1) }} /><button className="button button--primary" disabled={!product.available || !validQuantity} onClick={add}>Add to cart</button></div>}</section>

  <section className="reviews span-2">
    <div className="reviews__head"><h2>Customer reviews</h2>{list.length > 0 && <span className="reviews__avg"><Stars value={Math.round(average)} /> <b>{average}</b> · {list.length} review{list.length === 1 ? '' : 's'}</span>}</div>

    {reviewsLoading ? <Loader /> : list.length ? <ul className="reviews__list">{list.map((review) => { const name = reviewerOf(review); return <li className="review" key={review.feedbackId}><div className="review__head"><span className="review__avatar" aria-hidden="true">{(name[0] || 'C').toUpperCase()}</span><div className="review__who"><b>{name}</b><small>Verified purchase · {dateOnly(review.dateSubmitted)}</small></div><Stars value={review.hardwareStarRating} /></div><p>{review.comprehensiveReviewText}</p></li> })}</ul> : <p className="empty">No reviews yet. Be the first to review this product.</p>}

    {!isAdmin && (session ? <form className="review-form panel" onSubmit={submitReview}><h3>Write a review</h3><label>Rating<select value={rating} onChange={(e) => setRating(Number(e.target.value))}>{[5, 4, 3, 2, 1].map((value) => <option key={value} value={value}>{value} star{value === 1 ? '' : 's'}</option>)}</select></label><label>Your review<textarea required maxLength={1000} rows={4} placeholder="Share your experience with this product..." value={reviewText} onChange={(e) => setReviewText(e.target.value)} /></label><small className="form__hint">Reviews are available to verified purchasers and limited to one per product.</small><Notice message={reviewMessage} type={reviewMessage.startsWith('Thanks') ? 'success' : 'error'} /><button className="button button--primary" disabled={submitting}>{submitting ? 'Posting...' : 'Post review'}</button></form> : <p className="empty"><button className="link-button" onClick={() => navigate('/login')}>Sign in</button> to write a review.</p>)}
  </section>
  </main>
}
