import { api } from '../api/apiClient.js'

export const userService = {
  all: () => api('/users'),
  activate: (id) => api(`/users/${id}/activate`, { method: 'PATCH' }),
  deactivate: (id) => api(`/users/${id}/deactivate`, { method: 'PATCH' }),
}