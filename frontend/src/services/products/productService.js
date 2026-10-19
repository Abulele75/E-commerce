import { api, json } from '../api/apiClient.js'

export const productService = {
  list: () => api('/productcatalog'),
  get: (id) => api(`/productcatalog/${id}`),
  create: (product) => api('/productcatalog', json('POST', product)),
  update: (id, product) => api(`/productcatalog/${id}`, json('PUT', product)),
  remove: (id) => api(`/productcatalog/${id}`, { method: 'DELETE' }),
}