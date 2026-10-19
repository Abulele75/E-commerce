import { api, json } from '../api/apiClient.js'

export const feedbackService = {
  all: () => api('/feedback'),
  get: (id) => api(`/feedback/${id}`),
  byProduct: (productId) => api(`/feedback/product/${encodeURIComponent(productId)}`),
  createProductReview: (review) => api('/feedback/product-reviews', json('POST', review)),
  createDeliveryReview: (review) => api('/feedback/delivery-reviews', json('POST', review)),
  remove: (id) => api(`/feedback/${id}`, { method: 'DELETE' }),
}