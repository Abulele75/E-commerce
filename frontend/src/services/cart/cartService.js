import { api, json } from '../api/apiClient.js'

export const cartService = {
  get: () => api('/cart'),
  add: (productId, quantity = 1) => api('/cart/items', json('POST', { productId, quantity })),
  update: (itemId, quantity) => api(`/cart/items/${itemId}`, json('PUT', { quantity })),
  remove: (itemId) => api(`/cart/items/${itemId}`, { method: 'DELETE' }),
  clear: () => api('/cart/clear', { method: 'DELETE' }),
}