const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081/api'
const TOKEN_KEY = 'incredibletech.auth'

export class ApiError extends Error {
  constructor(message, status, fields = {}) {
    super(message)
    this.status = status
    this.fields = fields
  }
}

export async function api(path, options = {}) {
  const session = JSON.parse(localStorage.getItem(TOKEN_KEY) || 'null')
  const headers = { Accept: 'application/json', ...options.headers }
  if (options.body && !(options.body instanceof FormData)) headers['Content-Type'] = 'application/json'
  if (session?.token) headers.Authorization = `Bearer ${session.token}`

  let response
  try {
    response = await fetch(`${BASE_URL}${path}`, { ...options, headers })
  } catch {
    throw new ApiError('Unable to reach the server. Check your connection and try again.', 0)
  }

  if (response.status === 204) return null
  const type = response.headers.get('content-type') || ''
  let data
  try { data = type.includes('json') ? await response.json() : await response.text() }
  catch { data = '' }

  if (!response.ok) {
    if (response.status === 401 && session?.token) window.dispatchEvent(new Event('auth:expired'))
    const legacyFields = data && typeof data === 'object' && !('message' in data) ? data : {}
    const fields = data?.fieldErrors || legacyFields
    const firstFieldError = Object.values(fields)[0]
    const message = firstFieldError || data?.message || (typeof data === 'string' && data) || `Request failed (${response.status})`
    throw new ApiError(message, response.status, fields)
  }

  return data
}

export const json = (method, body) => ({ method, body: JSON.stringify(body) })
export { TOKEN_KEY }
