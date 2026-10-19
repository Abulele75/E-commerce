import { api, json } from '../api/apiClient.js'

export const authService = {
  login: (credentials) => api('/auth/login', json('POST', credentials)),
  register: (details) => api('/users/register', json('POST', details)),
  forgotPassword: (details) => api('/auth/forgot-password', json('POST', details)),
  resetPassword: (details) => api('/auth/reset-password', json('POST', details)),
  profile: () => api('/users/me'),
  updateProfile: (details) => api('/users/me', json('PUT', details)),
  changePassword: (details) => api('/users/me/password', json('PATCH', details)),
  deactivate: () => api('/users/me/deactivate', { method: 'PATCH' }),
}