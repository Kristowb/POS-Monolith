import axios, { AxiosError } from 'axios';
import type { ProblemDetail } from '../types';

const api = axios.create({
  baseURL: '/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor untuk menangani error response secara konsisten berbasis RFC 7807
api.interceptors.response.use(
  (response) => response,
  (error: AxiosError<ProblemDetail>) => {
    let normalizedError: ProblemDetail = {
      status: 500,
      title: 'Kesalahan Sistem',
      detail: 'Terjadi kesalahan tak terduga pada server.',
    };

    if (error.response && error.response.data) {
      const data = error.response.data;
      normalizedError = {
        type: data.type,
        title: data.title || 'REST Error',
        status: data.status || error.response.status,
        detail: data.detail || error.message,
        invalid_params: data.invalid_params,
        timestamp: data.timestamp,
      };
    } else if (error.request) {
      normalizedError = {
        status: 0,
        title: 'Kesalahan Jaringan',
        detail: 'Tidak dapat terhubung ke server. Pastikan backend Anda aktif.',
      };
    }

    return Promise.reject(normalizedError);
  }
);

export default api;
