import './payment-processing.css'

export default function PaymentProcessing({ amount }) {
  return (
    <div className="pay-processing" role="status" aria-live="polite" aria-label="Simulating payment">
      <div className="pay-processing__card">
        <div className="pay-processing__orb" aria-hidden="true">
          <span className="pay-processing__ring" />
          <span className="pay-processing__ring pay-processing__ring--slow" />
          <span className="pay-processing__chip">
            <svg viewBox="0 0 24 24" width="26" height="26" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
              <rect x="2.5" y="5" width="19" height="14" rx="2.5" />
              <path d="M2.5 9.5h19" />
              <path d="M6 14.5h4" />
            </svg>
          </span>
        </div>
        <h2>Simulating payment</h2>
        <p>Running the local test payment{amount ? ` for ${amount}` : ''}.</p>
        <div className="pay-processing__bar" aria-hidden="true"><span /></div>
        <span className="pay-processing__note">Demo only · No real charge</span>
      </div>
    </div>
  )
}
