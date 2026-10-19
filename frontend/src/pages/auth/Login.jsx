import { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import Notice from '../../components/notice/Notice.jsx'
import PasswordField from '../../components/password-field/PasswordField.jsx'
import { useAuth } from '../../hooks/auth/useAuth.js'

export default function Login() {
  const [form, setForm] = useState({ email: '', password: '' })
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)
  const { login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const successMessage = location.state?.passwordChanged
    ? 'Password changed successfully. Sign in with your new password.'
    : location.state?.passwordReset
      ? 'Password reset successfully. Sign in with your new password.'
      : location.state?.registered
        ? 'Account created successfully. You can now sign in.'
        : ''

  const submit = async (event) => {
    event.preventDefault(); setBusy(true); setError('')
    try { await login(form); navigate(location.state?.from?.pathname || '/', { replace: true }) }
    catch (err) { setError(err.message) } finally { setBusy(false) }
  }

  return <main className="auth-page"><section className="auth-card">
    <header className="auth-card__header"><span className="eyebrow">Welcome back</span><h1>Sign in</h1><p>Access your cart, orders and account.</p><Notice message={error || successMessage} type={successMessage && !error ? 'success' : 'error'} /></header>
    <form onSubmit={submit} className="auth-card__form">
      <div className="auth-card__body"><div className="form">
        <label>Email<input type="email" required autoComplete="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} /></label>
        <PasswordField label="Password" required maxLength="64" autoComplete="current-password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} />
        <div className="auth-card__links"><Link to="/forgot-password">Forgot password?</Link></div>
      </div></div>
      <footer className="auth-card__footer"><button className="button button--primary button--full" disabled={busy}>{busy ? 'Signing in…' : 'Sign in'}</button><p className="auth-card__switch">New here? <Link to="/register">Create an account</Link></p></footer>
    </form>
  </section></main>
}
