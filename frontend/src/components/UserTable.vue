<script setup lang="ts">
import type { UserResponse } from '../types';

defineProps<{
  users: UserResponse[];
  loading: boolean;
}>();

const emit = defineEmits<{
  (e: 'edit', user: UserResponse): void;
  (e: 'delete', id: number): void;
}>();
</script>

<template>
  <div class="table-container glass-card fade-in">
    <div v-if="loading && users.length === 0" class="loading-state">
      <div class="spinner"></div>
      <p>Memuat data pengguna...</p>
    </div>

    <div v-else-if="users.length === 0" class="empty-state">
      <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="empty-icon">
        <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 6a3.75 3.75 0 11-7.5 0 3.75 3.75 0 017.5 0zM4.501 20.118a7.5 7.5 0 0114.998 0A17.933 17.933 0 0112 21.75c-2.676 0-5.216-.584-7.499-1.632z" />
      </svg>
      <p>Belum ada pengguna terdaftar.</p>
    </div>

    <table v-else class="user-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>Nama Lengkap</th>
          <th>Username</th>
          <th>Email</th>
          <th>Peran</th>
          <th>Status</th>
          <th style="text-align: right;">Aksi</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="user in users" :key="user.id" class="table-row">
          <td>#{{ user.id }}</td>
          <td>
            <span class="user-name">{{ user.fullName }}</span>
          </td>
          <td><span class="user-username">@{{ user.username }}</span></td>
          <td>{{ user.email }}</td>
          <td>
            <span :class="['badge', user.role.toLowerCase()]">{{ user.role }}</span>
          </td>
          <td>
            <span :class="['status-dot', user.active ? 'active' : 'inactive']"></span>
            {{ user.active ? 'Aktif' : 'Nonaktif' }}
          </td>
          <td style="text-align: right;">
            <div class="action-buttons">
              <button @click="emit('edit', user)" class="action-btn edit-btn" title="Edit Pengguna">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="btn-icon">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M16.862 4.487l1.687-1.688a1.875 1.875 0 112.652 2.652L6.832 19.82a4.5 4.5 0 01-1.897 1.13l-2.685.8.8-2.685a4.5 4.5 0 011.13-1.897L16.863 4.487zm0 0L19.5 7.125" />
                </svg>
              </button>
              <button @click="emit('delete', user.id)" class="action-btn delete-btn" title="Hapus Pengguna">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="btn-icon">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M14.74 9l-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 01-2.244 2.077H8.084a2.25 2.25 0 01-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 00-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 013.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 00-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 00-7.5 0" />
                </svg>
              </button>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.table-container {
  overflow-x: auto;
  margin-top: 1rem;
}

.user-table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
}

th {
  padding: 1rem;
  font-size: 0.8rem;
  text-transform: uppercase;
  color: var(--text-secondary);
  font-weight: 600;
  letter-spacing: 0.05em;
  border-bottom: 1px solid var(--border-color);
}

td {
  padding: 1.25rem 1rem;
  font-size: 0.95rem;
  border-bottom: 1px solid rgba(75, 85, 99, 0.15);
  vertical-align: middle;
}

.table-row {
  transition: background-color var(--transition-fast);
}

.table-row:hover {
  background-color: rgba(99, 102, 241, 0.04);
}

.user-name {
  font-weight: 500;
  color: #ffffff;
}

.user-username {
  color: var(--primary);
  font-family: monospace;
}

.badge {
  display: inline-flex;
  align-items: center;
  padding: 0.25rem 0.6rem;
  border-radius: 9999px;
  font-size: 0.75rem;
  font-weight: 600;
  letter-spacing: 0.025em;
}

.badge.admin {
  background-color: rgba(99, 102, 241, 0.15);
  color: #a5b4fc;
  border: 1px solid rgba(99, 102, 241, 0.3);
}

.badge.user {
  background-color: rgba(243, 244, 246, 0.05);
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
}

.status-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-right: 0.5rem;
}

.status-dot.active {
  background-color: var(--success);
  box-shadow: 0 0 8px var(--success);
}

.status-dot.inactive {
  background-color: var(--text-muted);
}

.action-buttons {
  display: inline-flex;
  gap: 0.5rem;
  justify-content: flex-end;
}

.action-btn {
  background: transparent;
  border: 1px solid var(--border-color);
  color: var(--text-secondary);
  padding: 0.5rem;
  border-radius: var(--radius-sm);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-fast);
}

.action-btn:hover {
  color: #ffffff;
}

.edit-btn:hover {
  background: rgba(99, 102, 241, 0.2);
  border-color: var(--primary);
}

.delete-btn:hover {
  background: rgba(239, 68, 68, 0.2);
  border-color: var(--danger);
}

.btn-icon {
  width: 1.1rem;
  height: 1.1rem;
}

.loading-state, .empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  color: var(--text-secondary);
  text-align: center;
}

.empty-icon {
  width: 3.5rem;
  height: 3.5rem;
  color: var(--text-muted);
  margin-bottom: 1rem;
}

.spinner {
  border: 3px solid rgba(99, 102, 241, 0.1);
  border-top: 3px solid var(--primary);
  border-radius: 50%;
  width: 2.5rem;
  height: 2.5rem;
  animation: spin 1s linear infinite;
  margin-bottom: 1rem;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
