<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { healthService } from '../services/healthService';
import type { HealthCheckDto, ProblemDetail } from '../types';

const health = ref<HealthCheckDto | null>(null);
const loading = ref(false);
const error = ref<ProblemDetail | null>(null);

const checkSystemHealth = async () => {
  loading.value = true;
  error.value = null;
  try {
    health.value = await healthService.getHealth();
  } catch (err: any) {
    error.value = err as ProblemDetail;
    health.value = null;
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  checkSystemHealth();
});
</script>

<template>
  <div class="health-container fade-in">
    <div class="glass-card health-card">
      <div class="card-header">
        <h2 class="card-title">Status Integrasi Sistem</h2>
        <p class="card-subtitle">Periksa konektivitas real-time antara Frontend Vue.js dan Backend Java Spring Boot</p>
      </div>

      <div class="status-display">
        <!-- Loading State -->
        <div v-if="loading" class="state-wrapper">
          <div class="pulse-loader"></div>
          <p class="state-text">Melakukan ping ke server...</p>
        </div>

        <!-- Success (Server Online) -->
        <div v-else-if="health" class="state-wrapper">
          <div class="status-indicator online">
            <span class="ping-wave"></span>
            <span class="ping-dot"></span>
          </div>
          <span class="status-badge online">ONLINE</span>
          <h3 class="status-text">Server Aktif & Berjalan</h3>
          
          <div class="details-grid">
            <div class="detail-item">
              <span class="detail-label">Status Backend</span>
              <span class="detail-value text-success">{{ health.status }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Waktu Server</span>
              <span class="detail-value">{{ health.timestamp }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Versi Aplikasi</span>
              <span class="detail-value version-tag">{{ health.version }}</span>
            </div>
          </div>
        </div>

        <!-- Error (Server Offline / Network Error) -->
        <div v-else-if="error" class="state-wrapper">
          <div class="status-indicator offline">
            <span class="ping-dot"></span>
          </div>
          <span class="status-badge offline">OFFLINE</span>
          <h3 class="status-text text-danger">Koneksi Gagal</h3>
          
          <div class="error-box">
            <h4>{{ error.title || 'Kesalahan Server' }} (HTTP {{ error.status || 'Jaringan' }})</h4>
            <p>{{ error.detail }}</p>
            <span class="error-hint">Pastikan backend Spring Boot Anda berjalan pada port 8080.</span>
          </div>
        </div>
      </div>

      <div class="card-footer">
        <button @click="checkSystemHealth" class="btn btn-primary btn-refresh" :disabled="loading">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="refresh-icon" :class="{ 'spinning': loading }">
            <path stroke-linecap="round" stroke-linejoin="round" d="M16.023 9.348h4.992v-.001M2.985 19.644v-4.992m0 0h4.992m-4.993 0l3.181 3.183a8.25 8.25 0 0013.803-3.7M4.031 9.865a8.25 8.25 0 0113.803-3.7l3.181 3.182m0-4.991v4.99" />
          </svg>
          Periksa Ulang Koneksi
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.health-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 3rem 0;
}

.health-card {
  width: 100%;
  max-width: 600px;
  text-align: center;
  padding: 2.5rem;
}

.card-header {
  margin-bottom: 2rem;
}

.card-title {
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
}

.card-subtitle {
  color: var(--text-secondary);
  font-size: 0.9rem;
  line-height: 1.4;
}

.status-display {
  background: rgba(17, 24, 39, 0.4);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  padding: 2.5rem 1.5rem;
  margin-bottom: 2rem;
  min-height: 250px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.state-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  width: 100%;
}

.state-text {
  color: var(--text-secondary);
  font-size: 0.95rem;
}

/* Indicators and badges */
.status-indicator {
  position: relative;
  width: 20px;
  height: 20px;
  margin-bottom: 0.25rem;
}

.status-indicator .ping-dot {
  position: absolute;
  top: 5px;
  left: 5px;
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.status-indicator.online .ping-dot {
  background-color: var(--success);
}

.status-indicator.offline .ping-dot {
  background-color: var(--danger);
}

.status-indicator.online .ping-wave {
  position: absolute;
  top: 0;
  left: 0;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background-color: var(--success);
  opacity: 0.4;
  animation: ping 1.5s cubic-bezier(0, 0, 0.2, 1) infinite;
}

@keyframes ping {
  75%, 100% {
    transform: scale(2.2);
    opacity: 0;
  }
}

.status-badge {
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 0.05em;
  padding: 0.25rem 0.6rem;
  border-radius: var(--radius-sm);
}

.status-badge.online {
  background: rgba(16, 185, 129, 0.15);
  color: var(--success);
  border: 1px solid rgba(16, 185, 129, 0.3);
}

.status-badge.offline {
  background: rgba(239, 68, 68, 0.15);
  color: var(--danger);
  border: 1px solid rgba(239, 68, 68, 0.3);
}

.status-text {
  font-size: 1.2rem;
  font-weight: 600;
  color: #ffffff;
}

.text-success {
  color: var(--success);
}

.text-danger {
  color: var(--danger);
}

/* details grid */
.details-grid {
  width: 100%;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1rem;
  margin-top: 1.5rem;
  border-top: 1px solid rgba(75, 85, 99, 0.15);
  padding-top: 1.5rem;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
}

.detail-label {
  font-size: 0.75rem;
  color: var(--text-secondary);
}

.detail-value {
  font-size: 0.9rem;
  font-weight: 500;
  color: #ffffff;
}

.version-tag {
  font-family: monospace;
  background: rgba(255, 255, 255, 0.05);
  padding: 0.1rem 0.35rem;
  border-radius: 4px;
}

/* Error Box */
.error-box {
  background: rgba(239, 68, 68, 0.08);
  border: 1px solid rgba(239, 68, 68, 0.2);
  border-radius: var(--radius-md);
  padding: 1.25rem;
  width: 100%;
  text-align: left;
}

.error-box h4 {
  font-size: 0.95rem;
  color: #fca5a5;
  margin-bottom: 0.35rem;
}

.error-box p {
  font-size: 0.85rem;
  color: var(--text-secondary);
  line-height: 1.4;
  margin-bottom: 0.5rem;
}

.error-hint {
  font-size: 0.78rem;
  color: var(--text-muted);
  display: block;
}

/* Refresh Button */
.btn-refresh {
  gap: 0.5rem;
  width: 100%;
}

.refresh-icon {
  width: 1.1rem;
  height: 1.1rem;
}

.refresh-icon.spinning {
  animation: spin 1s linear infinite;
}

.pulse-loader {
  width: 2rem;
  height: 2rem;
  border-radius: 50%;
  background: var(--primary);
  opacity: 0.6;
  animation: pulse-loader-anim 1.2s ease-in-out infinite alternate;
}

@keyframes pulse-loader-anim {
  from { transform: scale(0.8); opacity: 0.3; }
  to { transform: scale(1.2); opacity: 0.9; }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
