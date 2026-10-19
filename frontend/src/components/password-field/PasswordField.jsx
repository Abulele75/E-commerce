import { useState } from 'react'

export default function PasswordField({ label, ...inputProps }) {
  const [visible, setVisible] = useState(false)

  return <label>{label}
    <span className="password-field">
      <input {...inputProps} type={visible ? 'text' : 'password'} />
      <button
        type="button"
        className="password-field__toggle"
        aria-label={visible ? `Hide ${label.toLowerCase()}` : `Show ${label.toLowerCase()}`}
        aria-pressed={visible}
        onClick={() => setVisible((current) => !current)}
      >{visible ? 'Hide' : 'Show'}</button>
    </span>
  </label>
}
