import { createRouter, createWebHistory } from 'vue-router';
import UserManagementView from '../views/UserManagementView.vue';
import HealthCheckView from '../views/HealthCheckView.vue';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'users',
      component: UserManagementView,
    },
    {
      path: '/health',
      name: 'health',
      component: HealthCheckView,
    },
  ],
});

export default router;
