import { api } from '../api/apiClient.js'

export const dashboardService = {
  summary: () => api('/admin/dashboard'),
}
