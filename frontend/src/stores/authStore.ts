import { defineStore } from 'pinia';
import api from '../services/api';
import type { LoginRequest, RegisterRequest, AuthResponse, UserResponse, ProblemDetail } from '../types';

interface AuthState {
  token: string | null;
  currentUser: UserResponse | null;
  loading: boolean;
  error: ProblemDetail | null;
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    token: localStorage.getItem('token'),
    currentUser: (() => {
      const userStr = localStorage.getItem('user');
      try {
        return userStr ? JSON.parse(userStr) : null;
      } catch {
        return null;
      }
    })(),
    loading: false,
    error: null,
  }),

  getters: {
    isAuthenticated(): boolean {
      return !!this.token;
    },
  },

  actions: {
    clearError() {
      this.error = null;
    },

    async register(request: RegisterRequest) {
      this.loading = true;
      this.error = null;
      try {
        const response = await api.post<AuthResponse>('/auth/register', request);
        const { token, user } = response.data;
        
        this.token = token;
        this.currentUser = user;
        
        localStorage.setItem('token', token);
        localStorage.setItem('user', JSON.stringify(user));
        
        return user;
      } catch (err: any) {
        this.error = err as ProblemDetail;
        throw err;
      } finally {
        this.loading = false;
      }
    },

    async login(request: LoginRequest) {
      this.loading = true;
      this.error = null;
      try {
        const response = await api.post<AuthResponse>('/auth/login', request);
        const { token, user } = response.data;
        
        this.token = token;
        this.currentUser = user;
        
        localStorage.setItem('token', token);
        localStorage.setItem('user', JSON.stringify(user));
        
        return user;
      } catch (err: any) {
        this.error = err as ProblemDetail;
        throw err;
      } finally {
        this.loading = false;
      }
    },

    async logout() {
      this.loading = true;
      this.error = null;
      try {
        // Coba panggil logout API ke backend untuk blacklist token secara persisten
        if (this.token) {
          await api.post('/auth/logout');
        }
      } catch (err) {
        console.warn('Gagal memblacklist token di backend (kemungkinan sudah kadaluarsa):', err);
      } finally {
        // Selalu hapus token secara lokal terlepas dari status API
        this.token = null;
        this.currentUser = null;
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        this.loading = false;
      }
    },
  },
});
