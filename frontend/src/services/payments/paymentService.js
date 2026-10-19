import { api, json } from '../api/apiClient.js'

export const paymentService = {
  payCard: (details) => api('/payments/card', json('POST', details)),
  payWallet: (details) => api('/payments/wallet', json('POST', details)),
  get: (id) => api(`/payments/${id}`),
  forOrder: (id) => api(`/payments/order/${id}`),
  mine: () => api('/payments/my-payments'),
  all: () => api('/payments'),
  refund: (id) => api(`/payments/${id}/refund`, { method: 'POST' }),
}