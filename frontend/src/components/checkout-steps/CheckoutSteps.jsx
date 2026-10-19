import './checkout-steps.css'

const STEPS = [
  { id: 1, label: 'Cart' },
  { id: 2, label: 'Details' },
  { id: 3, label: 'Payment' },
  { id: 4, label: 'Done' },
]

/**
 * Compact progress indicator for the purchase flow.
 * `current` is the 1-based index of the active step.
 */
export default function CheckoutSteps({ current = 1 }) {
  return (
    <ol className="checkout-steps" aria-label="Checkout progress">
      {STEPS.map((step) => {
        const state = step.id < current ? 'done' : step.id === current ? 'active' : 'upcoming'
        return (
          <li
            key={step.id}
            className={`checkout-steps__step is-${state}`}
            aria-current={state === 'active' ? 'step' : undefined}
          >
            <span className="checkout-steps__node" aria-hidden="true">
              {state === 'done'
                ? <svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round"><path d="M20 6 9 17l-5-5" /></svg>
                : step.id}
            </span>
            <span className="checkout-steps__label">{step.label}</span>
          </li>
        )
      })}
    </ol>
  )
}
