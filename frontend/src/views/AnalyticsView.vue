<script setup lang="ts">
import { computed } from 'vue';
import { useAuthStore } from '../stores/authStore';

const authStore = useAuthStore();

const isLocked = computed(() => {
  const user = authStore.currentUser;
  if (!user) return true;
  // Admin is bypass
  if (user.roles?.includes('ROLE_ADMIN')) return false;
  // Archer with Level 5+
  return !user.roles?.includes('ROLE_ARCHER') || user.level < 5;
});

const salesStats = [
  { time: '09:00', sales: '$140.00', orders: 12 },
  { time: '12:00', sales: '$580.00', orders: 38 },
  { time: '15:00', sales: '$320.00', orders: 22 },
  { time: '18:00', sales: '$920.00', orders: 54 }
];
</script>

<template>
  <div class="analytics-container">
    <!-- Locked Screen -->
    <div v-if="isLocked" class="locked-card">
      <div class="lock-icon">🔒</div>
      <h2>Analytics Chamber Locked</h2>
      <p class="warning-text">You do not meet the level or class requirements to view sales reports.</p>
      
      <div class="requirements">
        <div class="req-title">Requirements:</div>
        <div class="req-item" :class="{ met: authStore.currentUser?.roles?.includes('ROLE_ARCHER') || authStore.currentUser?.roles?.includes('ROLE_ADMIN') }">
          🎯 Class: Archer (Your class: {{ authStore.currentUser?.role ? authStore.currentUser.role.replace('ROLE_', '') : 'None' }})
        </div>
        <div class="req-item" :class="{ met: authStore.currentUser && authStore.currentUser.level >= 5 }">
          📈 Level: 5+ (Your level: Lvl {{ authStore.currentUser?.level || 1 }})
        </div>
      </div>
      <p class="tip-text">Tip: Gain EXP by checking out orders in the Kasir menu!</p>
    </div>

    <!-- Active Analytics Dashboard -->
    <div v-else class="analytics-dashboard card">
      <div class="dashboard-header">
        <h2 class="card-title">🎯 Archer Eagle-Eye (Sales Analytics)</h2>
        <span class="access-badge">ARCHER ACCESS GRANTED</span>
      </div>

      <p class="subtitle">Monitor real-time revenue performance and metrics.</p>

      <div class="metrics-grid">
        <div class="metric-card">
          <div class="metric-label">Total Revenue</div>
          <div class="metric-value">$1,960.00</div>
          <div class="metric-change">+15.4% from yesterday</div>
        </div>
        <div class="metric-card">
          <div class="metric-label">Orders Completed</div>
          <div class="metric-value">126</div>
          <div class="metric-change">+8.2% from yesterday</div>
        </div>
      </div>

      <div class="sales-table-container">
        <h3>Hourly Sales</h3>
        <div class="sales-list">
          <div class="sales-header">
            <span>Time</span>
            <span>Total Sales</span>
            <span>Order Qty</span>
          </div>
          
          <div v-for="stat in salesStats" :key="stat.time" class="sales-row">
            <span class="stat-time">{{ stat.time }}</span>
            <span class="stat-sales">{{ stat.sales }}</span>
            <span class="stat-orders">{{ stat.orders }} orders</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.analytics-container {
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

.tip-text {
  font-size: 0.8rem;
  color: var(--text-muted);
  font-style: italic;
}

/* Dashboard Styles */
.analytics-dashboard {
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
  background: rgba(16, 185, 129, 0.15);
  border: 1px solid rgba(16, 185, 129, 0.3);
  color: #10b981;
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

.metrics-grid {
  display: grid;
  grid-template-cols: 1fr;
  gap: 1.5rem;
  margin-bottom: 2rem;
}

@media (min-width: 576px) {
  .metrics-grid {
    grid-template-cols: 1fr 1fr;
  }
}

.metric-card {
  background: rgba(255, 255, 255, 0.02);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: var(--radius-md);
  padding: 1.5rem;
}

.metric-label {
  font-size: 0.8rem;
  font-weight: 700;
  text-transform: uppercase;
  color: var(--text-muted);
  margin-bottom: 0.5rem;
}

.metric-value {
  font-size: 2rem;
  font-weight: 800;
  color: #ffffff;
  margin-bottom: 0.5rem;
}

.metric-change {
  font-size: 0.75rem;
  color: #10b981;
  font-weight: 600;
}

.sales-table-container h3 {
  font-size: 1.1rem;
  font-weight: 700;
  color: #ffffff;
  margin-bottom: 1rem;
}

.sales-list {
  background: rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.02);
  border-radius: var(--radius-md);
  overflow: hidden;
}

.sales-header {
  display: grid;
  grid-template-cols: 1fr 1fr 1fr;
  padding: 1rem;
  background: rgba(255, 255, 255, 0.03);
  font-size: 0.8rem;
  font-weight: 700;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.sales-row {
  display: grid;
  grid-template-cols: 1fr 1fr 1fr;
  padding: 1.25rem 1rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.03);
  align-items: center;
  font-size: 0.9rem;
}

.sales-row:last-child {
  border-bottom: none;
}

.stat-time {
  color: #ffffff;
  font-weight: 600;
}

.stat-sales {
  color: var(--text-secondary);
  font-weight: 600;
}

.stat-orders {
  color: var(--text-muted);
}
</style>
