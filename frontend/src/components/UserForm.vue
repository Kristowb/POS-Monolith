<script setup lang="ts">
import { computed } from 'vue';
import type { UserRequest, ProblemDetail } from '../types';

const props = defineProps<{
  modelValue: UserRequest;
  error: ProblemDetail | null;
  loading: boolean;
  isEdit: boolean;
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: UserRequest): void;
  (e: 'submit'): void;
  (e: 'cancel'): void;
}>();

const form = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
});

// Helper untuk mengambil error khusus per field dari RFC 7807 invalid_params
const getFieldError = (field: string): string | undefined => {
  return props.error?.invalid_params?.[field];
};
</script>

<template>
  <div class="modal-backdrop">
    <div class="modal-content glass-card fade-in">
      <div class="modal-header">
        <h3>{{ isEdit ? 'Perbarui Pengguna' : 'Tambah Pengguna Baru' }}</h3>
        <button @click="emit('cancel')" class="close-header-btn">&times;</button>
      </div>

      <!-- General RFC 7807 Error Alert -->
      <div v-if="error && !error.invalid_params" class="alert alert-danger">
        <strong>{{ error.title || 'Terjadi Kesalahan' }}</strong>
        <p>{{ error.detail }}</p>
      </div>

      <form @submit.prevent="emit('submit')" class="form">
        <div class="form-group">
          <label class="form-label" for="username">Username</label>
          <input
            id="username"
            v-model="form.username"
            type="text"
            class="form-input"
            placeholder="Masukkan username"
            :disabled="loading || isEdit"
            required
          />
          <span v-if="getFieldError('username')" class="field-error-text">
            {{ getFieldError('username') }}
          </span>
        </div>

        <div class="form-group">
          <label class="form-label" for="fullName">Nama Lengkap</label>
          <input
            id="fullName"
            v-model="form.fullName"
            type="text"
            class="form-input"
            placeholder="Masukkan nama lengkap"
            :disabled="loading"
            required
          />
          <span v-if="getFieldError('fullName')" class="field-error-text">
            {{ getFieldError('fullName') }}
          </span>
        </div>

        <div class="form-group">
          <label class="form-label" for="email">Alamat Email</label>
          <input
            id="email"
            v-model="form.email"
            type="email"
            class="form-input"
            placeholder="nama@email.com"
            :disabled="loading"
            required
          />
          <span v-if="getFieldError('email')" class="field-error-text">
            {{ getFieldError('email') }}
          </span>
        </div>

        <div class="form-group">
          <label class="form-label" for="role">Peran / Role</label>
          <select id="role" v-model="form.role" class="form-select" :disabled="loading">
            <option value="USER">USER</option>
            <option value="ADMIN">ADMIN</option>
          </select>
          <span v-if="getFieldError('role')" class="field-error-text">
            {{ getFieldError('role') }}
          </span>
        </div>

        <div class="form-group checkbox-group">
          <label class="checkbox-label">
            <input
              type="checkbox"
              v-model="form.active"
              class="form-checkbox"
              :disabled="loading"
            />
            <span class="checkbox-custom-label">Pengguna ini Aktif</span>
          </label>
        </div>

        <div class="modal-footer">
          <button type="button" @click="emit('cancel')" class="btn btn-secondary" :disabled="loading">
            Batal
          </button>
          <button type="submit" class="btn btn-primary" :disabled="loading">
            <span v-if="loading" class="btn-spinner"></span>
            {{ isEdit ? 'Simpan Perubahan' : 'Tambah Pengguna' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(11, 15, 25, 0.8);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 1rem;
}

.modal-content {
  width: 100%;
  max-width: 500px;
  border-radius: var(--radius-lg);
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.5), 0 10px 10px -5px rgba(0, 0, 0, 0.5);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid var(--border-color);
}

.close-header-btn {
  background: transparent;
  border: none;
  color: var(--text-secondary);
  font-size: 1.75rem;
  cursor: pointer;
  line-height: 1;
  transition: color var(--transition-fast);
}

.close-header-btn:hover {
  color: #ffffff;
}

.alert {
  padding: 0.75rem 1rem;
  border-radius: var(--radius-md);
  margin-bottom: 1.25rem;
  font-size: 0.9rem;
}

.alert-danger {
  background: rgba(239, 68, 68, 0.15);
  border: 1px solid rgba(239, 68, 68, 0.3);
  color: #fca5a5;
}

.alert p {
  margin-top: 0.25rem;
  font-size: 0.85rem;
}

.field-error-text {
  display: block;
  color: #fca5a5;
  font-size: 0.8rem;
  margin-top: 0.35rem;
}

.checkbox-group {
  margin-top: 1rem;
  margin-bottom: 1.5rem;
}

.checkbox-label {
  display: inline-flex;
  align-items: center;
  cursor: pointer;
  user-select: none;
}

.form-checkbox {
  width: 1.15rem;
  height: 1.15rem;
  background: rgba(17, 24, 39, 0.8);
  border: 1px solid var(--border-color);
  border-radius: 4px;
  margin-right: 0.6rem;
  accent-color: var(--primary);
  cursor: pointer;
}

.checkbox-custom-label {
  font-size: 0.95rem;
  color: var(--text-primary);
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  padding-top: 1.25rem;
  border-top: 1px solid var(--border-color);
}

.btn-spinner {
  display: inline-block;
  width: 1rem;
  height: 1rem;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top: 2px solid #ffffff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-right: 0.5rem;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
