import { createRouter, createWebHistory } from 'vue-router';
import UserManagementView from '../views/UserManagementView.vue';
import HealthCheckView from '../views/HealthCheckView.vue';
import AuthView from '../views/AuthView.vue';
import CashierView from '../views/CashierView.vue';
import InventoryView from '../views/InventoryView.vue';
import AnalyticsView from '../views/AnalyticsView.vue';
import SettingsView from '../views/SettingsView.vue';
import { useAuthStore } from '../stores/authStore';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: AuthView,
      meta: { guestOnly: true },
    },
    {
      path: '/',
      name: 'users',
      component: UserManagementView,
      meta: { requiresAuth: true },
    },
    {
      path: '/health',
      name: 'health',
      component: HealthCheckView,
      meta: { requiresAuth: true },
    },
    {
      path: '/cashier',
      name: 'cashier',
      component: CashierView,
      meta: { requiresAuth: true },
    },
    {
      path: '/inventory',
      name: 'inventory',
      component: InventoryView,
      meta: { requiresAuth: true },
    },
    {
      path: '/analytics',
      name: 'analytics',
      component: AnalyticsView,
      meta: { requiresAuth: true },
    },
    {
      path: '/settings',
      name: 'settings',
      component: SettingsView,
      meta: { requiresAuth: true },
    },
  ],
});

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next({ name: 'login' });
  } else if (to.meta.guestOnly && authStore.isAuthenticated) {
    next({ name: 'users' });
  } else {
    next();
  }
});

export default router;
