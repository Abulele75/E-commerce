import { api, json } from '../api/apiClient.js'

export const deliveryService = {
  all: () => api('/deliveries'),
  get: (id) => api(`/deliveries/${id}`),
  create: (delivery) => api('/deliveries', json('POST', delivery)),
  update: (id, delivery) => api(`/deliveries/${id}`, json('PUT', delivery)),
  remove: (id) => api(`/deliveries/${id}`, { method: 'DELETE' }),
  addTracking: (id, event) => api(`/deliveries/${id}/tracking`, json('POST', event)),
  tracking: (id) => api(`/deliveries/${id}/tracking`),
  trackNumber: (number) => api(`/deliveries/tracking-number/${encodeURIComponent(number)}`),
  trackOrder: (orderId) => api(`/deliveries/order/${encodeURIComponent(orderId)}/tracking`),
}