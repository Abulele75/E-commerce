import AdminNav from '../admin-nav/AdminNav.jsx'

export default function AdminShell({ title, eyebrow = 'Administration', children }) {
  return <main className="page"><header className="page-hero page-hero--small"><span className="eyebrow">{eyebrow}</span><h1>{title}</h1></header><AdminNav />{children}</main>
}