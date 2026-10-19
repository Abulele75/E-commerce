import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Notice from '../../components/notice/Notice.jsx'
import PasswordField from '../../components/password-field/PasswordField.jsx'
import { authService } from '../../services/auth/authService.js'
import { useAuth } from '../../hooks/auth/useAuth.js'

const emptyPassword = { currentPassword: '', newPassword: '', confirmPassword: '' }
const strongPassword = '(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9\\s])\\S{12,64}'

export default function Profile() {
  const { profile, refreshProfile, logout } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({ firstName: '', lastName: '', email: '', phoneNumber: '' })
  const [password, setPassword] = useState(emptyPassword)
  const [message, setMessage] = useState('')
  const [passwordMessage, setPasswordMessage] = useState('')
  const [busy, setBusy] = useState(false)
  const [passwordBusy, setPasswordBusy] = useState(false)

  useEffect(() => { if (profile) setForm({ ...profile }) }, [profile])
  const change = (e) => setForm({ ...form, [e.target.name]: e.target.value })
  const changePassword = (e) => setPassword({ ...password, [e.target.name]: e.target.value })

  const submit = async (e) => {
    e.preventDefault(); setBusy(true); setMessage('')
    try {
      const emailChanged = profile.email !== form.email
      await authService.updateProfile(form)
      if (emailChanged) { logout(); navigate('/login', { replace: true }); return }
      await refreshProfile(); setMessage('Profile updated.')
    } catch (err) { setMessage(err.message) } finally { setBusy(false) }
  }

  const submitPassword = async (e) => {
    e.preventDefault(); setPasswordMessage('')
    if (password.newPassword !== password.confirmPassword) { setPasswordMessage('New passwords do not match'); return }
    setPasswordBusy(true)
    try {
      await authService.changePassword(password)
      setPassword(emptyPassword)
      logout()
      navigate('/login', { replace: true, state: { passwordChanged: true } })
    } catch (err) { setPasswordMessage(err.message) } finally { setPasswordBusy(false) }
  }

  const deactivate = async () => { if (confirm('Deactivate your account?')) { await authService.deactivate(); logout() } }

  return <main className="page narrow">
    <header className="page-hero page-hero--small"><span className="eyebrow">Your account</span><h1>Profile</h1></header>
    <Notice message={message} type={message.includes('updated') ? 'success' : 'error'} />
    <form className="form form--grid panel" onSubmit={submit}>
      <label>First name<input name="firstName" required value={form.firstName} onChange={change} /></label>
      <label>Last name<input name="lastName" required value={form.lastName} onChange={change} /></label>
      <label>Email<input name="email" type="email" required value={form.email} onChange={change} /></label>
      <label>Phone<input name="phoneNumber" required value={form.phoneNumber} onChange={change} /></label>
      <small className="span-2">Changing your email signs you out so a new token can be issued.</small>
      <button className="button button--primary" disabled={busy}>Save changes</button>
      <button className="button button--danger" type="button" onClick={deactivate}>Deactivate account</button>
    </form>

    <form className="form form--grid panel profile-password" onSubmit={submitPassword}>
      <h2 className="span-2">Change password</h2>
      <Notice message={passwordMessage} />
      <PasswordField label="Current password" name="currentPassword" required maxLength="64" autoComplete="current-password" value={password.currentPassword} onChange={changePassword} />
      <PasswordField label="New password" name="newPassword" required minLength="12" maxLength="64" pattern={strongPassword} autoComplete="new-password" value={password.newPassword} onChange={changePassword} />
      <PasswordField label="Confirm new password" name="confirmPassword" required minLength="12" maxLength="64" autoComplete="new-password" value={password.confirmPassword} onChange={changePassword} />
      <small className="span-2">Use 12–64 characters with upper/lowercase, a number and a symbol. You will sign in again after changing it.</small>
      <button className="button button--primary" disabled={passwordBusy}>{passwordBusy ? 'Changing…' : 'Change password'}</button>
    </form>
  </main>
}
