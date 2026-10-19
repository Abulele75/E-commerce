import { api } from '../api/apiClient.js'

export const wishlistService = {
  list: () => api('/wishlist'),
  add: (productId) => api(`/wishlist/items/${encodeURIComponent(productId)}`, { method: 'PUT' }),
  remove: (productId) => api(`/wishlist/items/${encodeURIComponent(productId)}`, { method: 'DELETE' }),
}
