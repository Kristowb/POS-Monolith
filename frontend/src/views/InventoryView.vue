<script setup lang="ts">
import { computed } from 'vue';
import { useAuthStore } from '../stores/authStore';

const authStore = useAuthStore();

const isLocked = computed(() => {
  const user = authStore.currentUser;
  if (!user) return true;
  // Admin is bypass
  if (user.roles?.includes('ROLE_ADMIN')) return false;
  // Warrior with Level 3+
  return !user.roles?.includes('ROLE_WARRIOR') || user.level < 3;
});

const stockItems = [
  { id: 1, name: 'Coffee Beans (Arabica)', stock: '45 kg', status: 'In Stock' },
  { id: 2, name: 'Paper Cups (12oz)', stock: '1,200 pcs', status: 'In Stock' },
  { id: 3, name: 'Croissants (Frozen)', stock: '12 pcs', status: 'Low Stock' },
  { id: 4, name: 'Matcha Powder', stock: '2 kg', status: 'In Stock' }
];
</script>

<template>
  <div class="inventory-container">
    <!-- Locked Screen -->
    <div v-if="isLocked" class="locked-card">
      <div class="lock-icon">🔒</div>
      <h2>Inventory Dungeon Locked</h2>
      <p class="warning-text">You do not meet the level or class requirements to enter this warehouse.</p>
      
      <div class="requirements">
        <div class="req-title">Requirements:</div>
        <div class="req-item" :class="{ met: authStore.currentUser?.roles?.includes('ROLE_WARRIOR') || authStore.currentUser?.roles?.includes('ROLE_ADMIN') }">
          🛡️ Class: Warrior (Your class: {{ authStore.currentUser?.role ? authStore.currentUser.role.replace('ROLE_', '') : 'None' }})
        </div>
        <div class="req-item" :class="{ met: authStore.currentUser && authStore.currentUser.level >= 3 }">
          📈 Level: 3+ (Your level: Lvl {{ authStore.currentUser?.level || 1 }})
        </div>
      </div>
      <p class="tip-text">Tip: Gain EXP by checking out orders in the Kasir menu!</p>
    </div>

    <!-- Active Inventory Dashboard -->
    <div v-else class="inventory-dashboard card">
      <div class="dashboard-header">
        <h2 class="card-title">🛡️ Warrior Stockroom (Inventory)</h2>
        <span class="access-badge">WARRIOR ACCESS GRANTED</span>
      </div>

      <p class="subtitle">Manage raw ingredients and physical store assets.</p>

      <div class="stock-list">
        <div class="stock-header">
          <span>Item Name</span>
          <span>Stock Qty</span>
          <span>Status</span>
        </div>
        
        <div v-for="item in stockItems" :key="item.id" class="stock-row">
          <span class="item-name">{{ item.name }}</span>
          <span class="item-qty">{{ item.stock }}</span>
          <span 
            class="item-status" 
            :class="item.status === 'Low Stock' ? 'low-stock' : 'in-stock'"
          >
            {{ item.status }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.inventory-container {
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
  color: #ef4444; /* red by default */
  margin-bottom: 0.5rem;
}

.req-item.met {
  color: #10b981; /* green if met */
}

.tip-text {
  font-size: 0.8rem;
  color: var(--text-muted);
  font-style: italic;
}

/* Dashboard Styles */
.inventory-dashboard {
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
  background: rgba(245, 158, 11, 0.15);
  border: 1px solid rgba(245, 158, 11, 0.3);
  color: #f59e0b;
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

.stock-list {
  background: rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.02);
  border-radius: var(--radius-md);
  overflow: hidden;
}

.stock-header {
  display: grid;
  grid-template-cols: 2fr 1fr 1fr;
  padding: 1rem;
  background: rgba(255, 255, 255, 0.03);
  font-size: 0.8rem;
  font-weight: 700;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.stock-row {
  display: grid;
  grid-template-cols: 2fr 1fr 1fr;
  padding: 1.25rem 1rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.03);
  align-items: center;
  font-size: 0.9rem;
}

.stock-row:last-child {
  border-bottom: none;
}

.item-name {
  color: #ffffff;
  font-weight: 600;
}

.item-qty {
  color: var(--text-secondary);
  font-weight: 600;
}

.item-status {
  font-size: 0.75rem;
  font-weight: 700;
  padding: 0.2rem 0.5rem;
  border-radius: 4px;
  width: fit-content;
}

.in-stock {
  background: rgba(16, 185, 129, 0.15);
  color: #10b981;
}

.low-stock {
  background: rgba(239, 68, 68, 0.15);
  color: #ef4444;
}
</style>
