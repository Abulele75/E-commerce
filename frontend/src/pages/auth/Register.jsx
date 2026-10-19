import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import Notice from '../../components/notice/Notice.jsx'
import PasswordField from '../../components/password-field/PasswordField.jsx'
import { authService } from '../../services/auth/authService.js'

const initial = { firstName: '', lastName: '', email: '', phoneNumber: '', password: '', confirmPassword: '' }
const strongPassword = '(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9\\s])\\S{12,64}'

export default function Register() {
  const [form, setForm] = useState(initial)
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)
  const navigate = useNavigate()
  const change = (e) => setForm({ ...form, [e.target.name]: e.target.value })

  const submit = async (e) => {
    e.preventDefault(); setError('')
    if (form.password !== form.confirmPassword) { setError('Passwords do not match'); return }
    setBusy(true)
    try { await authService.register(form); navigate('/login', { state: { registered: true } }) }
    catch (err) { setError(err.message) } finally { setBusy(false) }
  }

  return <main className="auth-page"><section className="auth-card">
    <header className="auth-card__header"><span className="eyebrow">Join IncredibleTech</span><h1>Create your account</h1><p>It only takes a minute.</p><Notice message={error} /></header>
    <form className="auth-card__form" onSubmit={submit}>
      <div className="auth-card__body"><div className="form">
        <label>First name<input name="firstName" required minLength="2" maxLength="50" autoComplete="given-name" value={form.firstName} onChange={change} /></label>
        <label>Last name<input name="lastName" required minLength="2" maxLength="50" autoComplete="family-name" value={form.lastName} onChange={change} /></label>
        <label>Email<input name="email" type="email" required maxLength="254" autoComplete="email" value={form.email} onChange={change} /></label>
        <label>SA mobile number<input name="phoneNumber" required pattern="0(6[0-9]|7[0-9]|8[1-9])[0-9]{7}" autoComplete="tel" value={form.phoneNumber} onChange={change} /></label>
        <PasswordField label="Password" name="password" required minLength="12" maxLength="64" pattern={strongPassword} autoComplete="new-password" value={form.password} onChange={change} />
        <PasswordField label="Confirm password" name="confirmPassword" required minLength="12" maxLength="64" autoComplete="new-password" value={form.confirmPassword} onChange={change} />
        <small className="form__hint">Use 12–64 characters with upper/lowercase, a number and a symbol. Supported emails include Gmail, Outlook, Hotmail, Live and .co.za.</small>
      </div></div>
      <footer className="auth-card__footer"><button className="button button--primary button--full" disabled={busy}>{busy ? 'Creating…' : 'Create account'}</button><p className="auth-card__switch">Already registered? <Link to="/login">Sign in</Link></p></footer>
    </form>
  </section></main>
}
