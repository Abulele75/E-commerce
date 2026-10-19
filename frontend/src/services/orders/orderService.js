import { api, json } from '../api/apiClient.js'

export const orderService = {
  checkout: (details) => api('/orders/checkout', json('POST', details)),
  mine: () => api('/orders/my-orders'),
  get: (id) => api(`/orders/${id}`),
  cancel: (id) => api(`/orders/${id}/cancel`, { method: 'PATCH' }),
  all: (status = '') => api(`/orders${status ? `?status=${status}` : ''}`),
  updateStatus: (id, orderStatus) => api(`/orders/${id}/status`, json('PATCH', { orderStatus })),
}