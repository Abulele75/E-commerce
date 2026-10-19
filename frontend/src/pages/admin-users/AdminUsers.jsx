import AdminShell from '../../components/admin-shell/AdminShell.jsx'
import Loader from '../../components/loader/Loader.jsx'; import Notice from '../../components/notice/Notice.jsx'
import { userService } from '../../services/users/userService.js'; import { useAsync } from '../../hooks/async/useAsync.js'; import { byRecent, dateTime, label } from '../../utils/format/formatters.js'

export default function AdminUsers() {
  const { data: users = [], loading, error, reload } = useAsync(userService.all, [])
  const toggle = async (user) => { if (user.active) await userService.deactivate(user.userId); else await userService.activate(user.userId); reload() }
  const rows = [...(users || [])].sort(byRecent('createdAt', 'userId'))
  return <AdminShell title="Users"><Notice message={error} />{loading ? <Loader /> : <div className="table-wrap"><table><thead><tr><th>User</th><th>Role</th><th>Joined</th><th>Status</th><th /></tr></thead><tbody>{rows.map((user) => <tr key={user.userId}><td><b>{user.firstName} {user.lastName}</b><small>{user.email}<br />{user.phoneNumber}</small></td><td>{label(user.role)}</td><td>{dateTime(user.createdAt)}</td><td><span className={`status ${user.active ? 'status--paid' : 'status--cancelled'}`}>{user.active ? 'Active' : 'Inactive'}</span></td><td><button className="button button--ghost" onClick={() => toggle(user)}>{user.active ? 'Deactivate' : 'Activate'}</button></td></tr>)}</tbody></table></div>}</AdminShell>
}