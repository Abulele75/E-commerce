export default function Notice({ message, type = 'error' }) {
  if (!message) return null
  const isError = type === 'error'
  return <div className={`notice notice--${type}`} role={isError ? 'alert' : 'status'} aria-live={isError ? 'assertive' : 'polite'}>{message}</div>
}
