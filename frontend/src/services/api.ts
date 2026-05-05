import axios from 'axios'

const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080'

const api = axios.create({
  baseURL: `${API_BASE}/api/v1`,
  headers: {
    'Content-Type': 'application/json',
  },
})

export const dashboardApi = {
  getSummary: () => api.get('/dashboard/summary'),
  getClusters: () => api.get('/dashboard/clusters'),
  getCompliance: () => api.get('/dashboard/compliance'),
  getAlerts: () => api.get('/dashboard/alerts'),
}

export const healthApi = {
  getClusterHealth: (id: string) => api.get(`/health/clusters/${id}`),
  getLatestHealth: (id: string) => api.get(`/health/clusters/${id}/latest`),
  getCritical: (hours = 24) => api.get(`/health/critical?hours=${hours}`),
  ingest: (data: any) => api.post('/health/ingest', data),
}

export const auditApi = {
  getEvents: (days = 30) => api.get(`/audit/events?days=${days}`),
  getDashboard: () => api.get('/audit/dashboard'),
  getFailed: (days = 7) => api.get(`/audit/failed?days=${days}`),
  recordEvent: (data: any) => api.post('/audit/events', data),
}

export const aiApi = {
  query: (query: string, clusterId?: string) => 
    api.post('/ai/query', { query, clusterId }),
  getStatus: () => api.get('/ai/status'),
}

export default api
