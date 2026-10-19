/**
 * Placeholder grid shown while catalogue data loads.
 * Mirrors the product card footprint so the layout does not shift on arrival.
 */
export default function ProductSkeleton({ count = 4 }) {
  return (
    <div className="product-grid" aria-hidden="true">
      {Array.from({ length: count }).map((_, index) => (
        <article className="product-card product-card--skeleton" key={index}>
          <div className="product-card__image-wrap"><div className="skeleton skeleton--image" /></div>
          <div className="product-card__body">
            <div className="skeleton skeleton--line skeleton--short" />
            <div className="skeleton skeleton--line" />
            <div className="skeleton skeleton--line skeleton--medium" />
            <div className="skeleton skeleton--button" />
          </div>
        </article>
      ))}
    </div>
  )
}
