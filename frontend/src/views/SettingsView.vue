<script setup lang="ts">
import { computed, ref } from 'vue';
import { useAuthStore } from '../stores/authStore';

const authStore = useAuthStore();
const successMsg = ref('');

const isLocked = computed(() => {
  const user = authStore.currentUser;
  if (!user) return true;
  return !user.roles?.includes('ROLE_ADMIN') || user.level < 10;
});

const handleLevelUpSim = async () => {
  const updatedUser = await authStore.simulateLevelUp();
  if (updatedUser) {
    successMsg.value = `Level Up Sim Success! New Level: ${updatedUser.level}`;
    setTimeout(() => {
      successMsg.value = '';
    }, 3000);
  }
};
</script>

<template>
  <div class="settings-container">
    <!-- Locked Screen -->
    <div v-if="isLocked" class="locked-card">
      <div class="lock-icon">🔒</div>
      <h2>System Core Locked</h2>
      <p class="warning-text">You must be the ultimate Admin at Level 10+ to adjust core configurations.</p>
      
      <div class="requirements">
        <div class="req-title">Requirements:</div>
        <div class="req-item" :class="{ met: authStore.currentUser?.roles?.includes('ROLE_ADMIN') }">
          👑 Class: Admin (Your class: {{ authStore.currentUser?.role ? authStore.currentUser.role.replace('ROLE_', '') : 'None' }})
        </div>
        <div class="req-item" :class="{ met: authStore.currentUser && authStore.currentUser.level >= 10 }">
          📈 Level: 10+ (Your level: Lvl {{ authStore.currentUser?.level || 1 }})
        </div>
      </div>
      
      <!-- Quick Simulation Button for Testing (available to all users on locked screen!) -->
      <div class="simulation-box">
        <p>Developer Testing Panel:</p>
        <button @click="handleLevelUpSim" class="btn btn-simulation">
          ⚡ Force Level Up (Skip grinding)
        </button>
        <p v-if="successMsg" class="success-text">{{ successMsg }}</p>
      </div>
    </div>

    <!-- Active Settings Dashboard -->
    <div v-else class="settings-dashboard card">
      <div class="dashboard-header">
        <h2 class="card-title">👑 System Core Settings</h2>
        <span class="access-badge">ADMIN ACCESS GRANTED</span>
      </div>

      <p class="subtitle">Configure core system variables and RPG parameters.</p>

      <div class="settings-grid">
        <div class="setting-row">
          <div class="setting-info">
            <span class="setting-name">EXP multiplier</span>
            <span class="setting-desc">Adjust amount of experience gained per transaction.</span>
          </div>
          <select class="setting-select">
            <option value="1">1.0x (Standard)</option>
            <option value="1.5">1.5x (Fast)</option>
            <option value="2">2.0x (Double)</option>
          </select>
        </div>
        
        <div class="setting-row">
          <div class="setting-info">
            <span class="setting-name">Maintenance mode</span>
            <span class="setting-desc">Block normal users from performing transactions.</span>
          </div>
          <input type="checkbox" class="setting-checkbox" />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.settings-container {
  padding: 1.5rem 0;
}

.card {
  background: rgba(17, 24, 39, 0.4);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  padding: 2rem;
  backdrop-filter: blur(12px);
}

.locked-card {
  background: rgba(17, 24, 39, 0.6);
  border: 1px solid rgba(239, 68, 68, 0.2);
  border-radius: var(--radius-lg);
  padding: 3rem 2rem;
  text-align: center;
  backdrop-filter: blur(12px);
  max-width: 500px;
  margin: 2rem auto;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5);
}

.lock-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.08); }
}

h2 {
  font-size: 1.5rem;
  font-weight: 800;
  color: #ffffff;
  margin-bottom: 0.5rem;
}

.warning-text {
  color: var(--text-secondary);
  font-size: 0.9rem;
  margin-bottom: 1.75rem;
}

.requirements {
  background: rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: var(--radius-md);
  padding: 1.25rem;
  text-align: left;
  margin-bottom: 1.5rem;
}

.req-title {
  font-size: 0.8rem;
  font-weight: 700;
  text-transform: uppercase;
  color: var(--text-muted);
  margin-bottom: 0.75rem;
}

.req-item {
  font-size: 0.85rem;
  font-weight: 600;
  color: #ef4444;
  margin-bottom: 0.5rem;
}

.req-item.met {
  color: #10b981;
}

.simulation-box {
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  padding-top: 1.5rem;
  margin-top: 1rem;
}

.simulation-box p {
  font-size: 0.8rem;
  font-weight: 700;
  color: var(--text-muted);
  margin-bottom: 0.5rem;
  text-transform: uppercase;
}

.btn {
  padding: 0.75rem 1.25rem;
  border-radius: var(--radius-md);
  font-weight: 700;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid transparent;
}

.btn-simulation {
  background: linear-gradient(135deg, var(--primary) 0%, #a855f7 100%);
  color: #ffffff;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
}

.btn-simulation:hover {
  transform: translateY(-1px);
}

.success-text {
  font-size: 0.8rem;
  color: #10b981;
  font-weight: 700;
  margin-top: 0.5rem !important;
}

/* Dashboard Styles */
.settings-dashboard {
  color: #ffffff;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  padding-bottom: 0.75rem;
  margin-bottom: 0.5rem;
}

.card-title {
  font-size: 1.5rem;
  font-weight: 800;
  margin: 0;
}

.access-badge {
  background: rgba(168, 85, 247, 0.15);
  border: 1px solid rgba(168, 85, 247, 0.3);
  color: #a855f7;
  font-size: 0.7rem;
  font-weight: 800;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  letter-spacing: 0.05em;
}

.subtitle {
  color: var(--text-secondary);
  font-size: 0.9rem;
  margin-bottom: 2rem;
}

.settings-grid {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.setting-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 1.5rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.03);
}

.setting-row:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.setting-info {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.setting-name {
  font-size: 1rem;
  font-weight: 600;
  color: #ffffff;
}

.setting-desc {
  font-size: 0.8rem;
  color: var(--text-secondary);
}

.setting-select {
  background: #111726;
  border: 1px solid rgba(255, 255, 255, 0.08);
  color: #ffffff;
  padding: 0.5rem;
  border-radius: 4px;
  font-size: 0.85rem;
  outline: none;
}

.setting-checkbox {
  width: 1.25rem;
  height: 1.25rem;
  cursor: pointer;
}
</style>
