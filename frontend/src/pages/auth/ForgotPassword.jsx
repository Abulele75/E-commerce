import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import Notice from '../../components/notice/Notice.jsx'
import { authService } from '../../services/auth/authService.js'

export default function ForgotPassword() {
  const [email, setEmail] = useState('')
  const [result, setResult] = useState(null)
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)
  const navigate = useNavigate()

  const submit = async (event) => {
    event.preventDefault(); setBusy(true); setError(''); setResult(null)
    try { setResult(await authService.forgotPassword({ email })) }
    catch (err) { setError(err.message) } finally { setBusy(false) }
  }

  return <main className="auth-page"><section className="auth-card">
    <header className="auth-card__header"><span className="eyebrow">Account recovery</span><h1>Forgot password</h1><p>Enter the email used for your account.</p><Notice message={error || result?.message} type={result && !error ? 'success' : 'error'} /></header>
    <form className="auth-card__form" onSubmit={submit}>
      <div className="auth-card__body"><div className="form">
        <label>Email<input type="email" required maxLength="254" autoComplete="email" value={email} onChange={(event) => setEmail(event.target.value)} /></label>
        {result?.developmentResetToken && <small className="form__hint">Local development mode generated a reset token. Continue now; it expires in 15 minutes and is not stored in your browser.</small>}
      </div></div>
      <footer className="auth-card__footer">
        {result?.developmentResetToken
          ? <button className="button button--primary button--full" type="button" onClick={() => navigate('/reset-password', { state: { token: result.developmentResetToken } })}>Continue to reset password</button>
          : <button className="button button--primary button--full" disabled={busy}>{busy ? 'Checking…' : 'Continue'}</button>}
        <p className="auth-card__switch"><Link to="/login">Back to sign in</Link></p>
      </footer>
    </form>
  </section></main>
}
