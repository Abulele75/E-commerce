import { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import Notice from '../../components/notice/Notice.jsx'
import PasswordField from '../../components/password-field/PasswordField.jsx'
import { authService } from '../../services/auth/authService.js'

const strongPassword = '(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9\\s])\\S{12,64}'

export default function ResetPassword() {
  const location = useLocation()
  const navigate = useNavigate()
  const [form, setForm] = useState({ token: location.state?.token || '', newPassword: '', confirmPassword: '' })
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)
  const change = (event) => setForm({ ...form, [event.target.name]: event.target.value })

  const submit = async (event) => {
    event.preventDefault(); setError('')
    if (form.newPassword !== form.confirmPassword) { setError('New passwords do not match'); return }
    setBusy(true)
    try {
      await authService.resetPassword(form)
      setForm({ token: '', newPassword: '', confirmPassword: '' })
      navigate('/login', { replace: true, state: { passwordReset: true } })
    } catch (err) { setError(err.message) } finally { setBusy(false) }
  }

  return <main className="auth-page"><section className="auth-card">
    <header className="auth-card__header"><span className="eyebrow">Account recovery</span><h1>Create new password</h1><p>Use a password you have not used before.</p><Notice message={error} /></header>
    <form className="auth-card__form" onSubmit={submit}>
      <div className="auth-card__body"><div className="form">
        {!location.state?.token && <label>Reset token<input name="token" required maxLength="128" autoComplete="off" value={form.token} onChange={change} /></label>}
        <PasswordField label="New password" name="newPassword" required minLength="12" maxLength="64" pattern={strongPassword} autoComplete="new-password" value={form.newPassword} onChange={change} />
        <PasswordField label="Confirm new password" name="confirmPassword" required minLength="12" maxLength="64" autoComplete="new-password" value={form.confirmPassword} onChange={change} />
        <small className="form__hint">Use 12–64 characters with uppercase, lowercase, a number and a symbol.</small>
      </div></div>
      <footer className="auth-card__footer"><button className="button button--primary button--full" disabled={busy}>{busy ? 'Resetting…' : 'Reset password'}</button><p className="auth-card__switch"><Link to="/login">Back to sign in</Link></p></footer>
    </form>
  </section></main>
}
