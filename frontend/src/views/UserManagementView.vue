<script setup lang="ts">
import { onMounted, computed } from 'vue';
import { useUsers } from '../composables/useUsers';
import UserTable from '../components/UserTable.vue';
import UserForm from '../components/UserForm.vue';

const {
  users,
  loading,
  error,
  showModal,
  editingUser,
  form,
  openAddModal,
  openEditModal,
  closeModal,
  saveUser,
  deleteUser,
  fetchUsers
} = useUsers();

onMounted(() => {
  fetchUsers();
});

// Perhitungan metrik statistik untuk visualisasi dashboard premium
const totalUsers = computed(() => users.value.length);
const activeUsersCount = computed(() => users.value.filter(u => u.active).length);
const adminsCount = computed(() => users.value.filter(u => u.role === 'ADMIN').length);
</script>

<template>
  <div class="view-container fade-in">
    <!-- Header Dashboard -->
    <div class="dashboard-header">
      <div>
        <h1 class="view-title">Dashboard Pengguna</h1>
        <p class="view-subtitle">Kelola otorisasi, peran, dan identitas pengguna aplikasi POS</p>
      </div>
      <button @click="openAddModal" class="btn btn-primary btn-add">
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="add-icon">
          <path stroke-linecap="round" stroke-linejoin="round" d="M12 4.5v15m7.5-7.5h-15" />
        </svg>
        Tambah Pengguna
      </button>
    </div>

    <!-- Panel Statistik Premium (Estetika visual premium) -->
    <div class="stats-grid">
      <div class="stat-card glass-card">
        <div class="stat-icon-wrapper blue">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="stat-icon">
            <path stroke-linecap="round" stroke-linejoin="round" d="M15 19.128a9.38 9.38 0 002.625.372 9.337 9.337 0 004.121-.952 4.125 4.125 0 00-7.533-2.493M15 19.128v-.003c0-1.113-.285-2.16-.786-3.07M15 19.128v.109A11.386 11.386 0 0110.089 21c-2.243 0-4.32-.647-6.08-1.758V19.13c0-2.153 1.253-4.1 3.259-4.83a4.864 4.864 0 0110.025 0c2.006.73 3.259 2.677 3.259 4.83v.002z" />
          </svg>
        </div>
        <div class="stat-info">
          <span class="stat-label">Total Pengguna</span>
          <span class="stat-value">{{ totalUsers }}</span>
        </div>
      </div>

      <div class="stat-card glass-card">
        <div class="stat-icon-wrapper green">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="stat-icon">
            <path stroke-linecap="round" stroke-linejoin="round" d="M9 12.75L11.25 15 15 9.75M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
        </div>
        <div class="stat-info">
          <span class="stat-label">Pengguna Aktif</span>
          <span class="stat-value">{{ activeUsersCount }}</span>
        </div>
      </div>

      <div class="stat-card glass-card">
        <div class="stat-icon-wrapper purple">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="stat-icon">
            <path stroke-linecap="round" stroke-linejoin="round" d="M9 12.75L11.25 15 15 9.75m-3-7.036A11.959 11.959 0 013.598 6 11.99 11.99 0 003 9.749c0 5.592 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.31-.21-2.571-.598-3.751h-.152c-3.196 0-6.1-1.248-8.25-3.285z" />
          </svg>
        </div>
        <div class="stat-info">
          <span class="stat-label">Administrator</span>
          <span class="stat-value">{{ adminsCount }}</span>
        </div>
      </div>
    </div>

    <!-- Alert Kesalahan Jaringan / Server Global (RFC 7807) -->
    <div v-if="error && !showModal" class="alert alert-danger-global fade-in">
      <div class="alert-icon">
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="alert-svg">
          <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v3.75m9-.75a9 9 0 11-18 0 9 9 0 0118 0zm-9 3.75h.008v.008H12v-.008z" />
        </svg>
      </div>
      <div>
        <h4>Gagal sinkronisasi data: {{ error.title || 'Kesalahan API' }} (HTTP {{ error.status }})</h4>
        <p>{{ error.detail }}</p>
      </div>
    </div>

    <!-- Tabel Pengguna -->
    <UserTable
      :users="users"
      :loading="loading"
      @edit="openEditModal"
      @delete="deleteUser"
    />

    <!-- Dialog Formulir (Modal) -->
    <UserForm
      v-if="showModal"
      v-model="form"
      :error="error"
      :loading="loading"
      :is-edit="!!editingUser"
      @submit="saveUser"
      @cancel="closeModal"
    />
  </div>
</template>

<style scoped>
.view-container {
  padding: 2rem 0;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 1rem;
}

.view-title {
  font-size: 1.75rem;
  font-weight: 700;
  margin-bottom: 0.25rem;
}

.view-subtitle {
  color: var(--text-secondary);
  font-size: 0.95rem;
}

.btn-add {
  gap: 0.5rem;
}

.add-icon {
  width: 1.2rem;
  height: 1.2rem;
}

/* Statistik Grid */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1.25rem;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 1.25rem;
  padding: 1.25rem 1.5rem;
  background: rgba(31, 41, 55, 0.25);
  border: 1px solid var(--border-color);
}

.stat-icon-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 3rem;
  height: 3rem;
  border-radius: var(--radius-md);
}

.stat-icon-wrapper.blue {
  background: rgba(99, 102, 241, 0.15);
  color: #a5b4fc;
}

.stat-icon-wrapper.green {
  background: rgba(16, 185, 129, 0.15);
  color: #a7f3d0;
}

.stat-icon-wrapper.purple {
  background: rgba(168, 85, 247, 0.15);
  color: #f3e8ff;
}

.stat-icon {
  width: 1.5rem;
  height: 1.5rem;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-label {
  font-size: 0.85rem;
  color: var(--text-secondary);
}

.stat-value {
  font-size: 1.75rem;
  font-weight: 700;
  color: #ffffff;
  line-height: 1.2;
}

/* Global Alert */
.alert-danger-global {
  display: flex;
  gap: 1rem;
  background: rgba(239, 68, 68, 0.12);
  border: 1px solid rgba(239, 68, 68, 0.25);
  color: #fca5a5;
  padding: 1rem 1.5rem;
  border-radius: var(--radius-lg);
  align-items: flex-start;
}

.alert-icon {
  flex-shrink: 0;
  margin-top: 0.15rem;
}

.alert-svg {
  width: 1.5rem;
  height: 1.5rem;
}

.alert-danger-global h4 {
  font-size: 1rem;
  margin-bottom: 0.25rem;
  color: #ffffff;
}

.alert-danger-global p {
  font-size: 0.88rem;
  color: #fca5a5;
}
</style>
