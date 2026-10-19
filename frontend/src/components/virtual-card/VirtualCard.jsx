import './virtual-card.css'

const BRAND_LABELS = {
  VISA: 'VISA',
  MASTERCARD: 'Mastercard',
  AMERICAN_EXPRESS: 'AMEX',
  OTHER: 'Card',
}

/**
 * Simulated bank card. Reflects the selected brand colour and the last four
 * digits live as the customer types. Reveals an alternate colour on hover.
 */
export default function VirtualCard({ brand = 'VISA', last4 = '', name = '' }) {
  const safeBrand = BRAND_LABELS[brand] ? brand : 'OTHER'
  const digits = (last4 || '').replace(/\D/g, '').slice(0, 4)
  const filled = digits.padStart(4, '•')

  return (
    <div className={`virtual-card virtual-card--${safeBrand.toLowerCase()}`} aria-hidden="true">
      <div className="virtual-card__sheen" />
      <div className="virtual-card__top">
        <span className="virtual-card__chip" />
        <span className="virtual-card__brand">{BRAND_LABELS[safeBrand]}</span>
      </div>
      <div className="virtual-card__number">
        <span>••••</span><span>••••</span><span>••••</span><span className="virtual-card__last4">{filled}</span>
      </div>
      <div className="virtual-card__bottom">
        <div className="virtual-card__field">
          <small>Card holder</small>
          <strong>{name?.trim() || 'YOUR NAME'}</strong>
        </div>
        <div className="virtual-card__field virtual-card__field--exp">
          <small>Valid thru</small>
          <strong>••/••</strong>
        </div>
      </div>
    </div>
  )
}
