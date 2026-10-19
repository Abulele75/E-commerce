import { createContext, useCallback, useEffect, useMemo, useState } from 'react'
import { authService } from '../../services/auth/authService.js'
import { TOKEN_KEY } from '../../services/api/apiClient.js'

export const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [session, setSession] = useState(() => JSON.parse(localStorage.getItem(TOKEN_KEY) || 'null'))
  const [profile, setProfile] = useState(null)
  const logout = useCallback(() => { localStorage.removeItem(TOKEN_KEY); setSession(null); setProfile(null) }, [])
  const login = async (credentials) => {
    const next = await authService.login(credentials)
    localStorage.setItem(TOKEN_KEY, JSON.stringify(next)); setSession(next)
    return next
  }
  const refreshProfile = useCallback(async () => {
    if (!session) return null
    const next = await authService.profile(); setProfile(next); return next
  }, [session])
  useEffect(() => { if (session) refreshProfile().catch(logout) }, [session, refreshProfile, logout])
  useEffect(() => {
    window.addEventListener('auth:expired', logout)
    return () => window.removeEventListener('auth:expired', logout)
  }, [logout])
  const value = useMemo(() => ({ session, profile, login, logout, refreshProfile, isAdmin: session?.role === 'ADMINISTRATOR' }), [session, profile, logout, refreshProfile])
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}