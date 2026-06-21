import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

// Request interceptor for logging
api.interceptors.request.use(
  (config) => {
    console.log(`[API] ${config.method.toUpperCase()} ${config.url}`);
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    console.error('[API Error]', error.response?.data || error.message);
    return Promise.reject(error);
  }
);

// Patient APIs
export const patientApi = {
  addPatient: (patientData) => 
    api.post('/api/patient', patientData),
  
  getQueue: () => 
    api.get('/api/patient/queue'),
  
  callNext: () => 
    api.post('/api/patient/next'),
  
  skipPatient: (id) => 
    api.post(`/api/patient/${id}/skip`),
  
  completePatient: (id) => 
    api.post(`/api/patient/${id}/complete`),
  
  deletePatient: (id) => 
    api.delete(`/api/patient/${id}`),
  
  searchByPhone: (phone) => 
    api.get(`/api/patient/search?phone=${phone}`),
  
  getConsulting: () => 
    api.get('/api/patient/consulting')
};

export default api;