<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from './stores/authStore';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const handleLogout = async () => {
  await authStore.logout();
  router.push('/login');
};
</script>

<template>
  <!-- Jika di halaman login, tampilkan langsung router-view tanpa layout global -->
  <template v-if="route.name === 'login'">
    <router-view />
  </template>
  
  <!-- Jika di halaman biasa (dashboard/manajemen), tampilkan header, sidebar, footer global -->
  <template v-else>
    <header class="app-header">
      <div class="container header-container">
        <div class="logo-section">
          <div class="logo-symbol">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="logo-svg">
              <path stroke-linecap="round" stroke-linejoin="round" d="M14.25 9.75L16.5 12l-2.25 2.25m-4.5 0L7.5 12l2.25-2.25M6 20.25h12A2.25 2.25 0 0020.25 18V6A2.25 2.25 0 0018 3.75H6A2.25 2.25 0 003.75 6v12A2.25 2.25 0 006 20.25z" />
            </svg>
          </div>
          <span class="logo-text">POS <span class="accent-text">Monolith</span></span>
        </div>
        
        <nav class="nav-links">
          <router-link to="/" class="nav-item" active-class="active-nav">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="nav-icon">
              <path stroke-linecap="round" stroke-linejoin="round" d="M18 18.72a9.094 9.094 0 003.741-.479 3 3 0 00-4.682-2.72m.94 3.198l.001.031c0 .225-.012.447-.037.666A11.944 11.944 0 0112 21c-2.17 0-4.207-.576-5.963-1.584A6.062 6.062 0 016 18.719m12 0a5.971 5.971 0 00-.941-3.197m0 0A5.995 5.995 0 0012 12.75a5.995 5.995 0 00-5.058 2.772m0 0a3 3 0 00-4.681 2.72 8.986 8.986 0 003.74.477m.94-3.197a5.971 5.971 0 00-.94 3.197M15 6.75a3 3 0 11-6 0 3 3 0 016 0zm6 3a2.25 2.25 0 11-4.5 0 2.25 2.25 0 014.5 0zm-13.5 0a2.25 2.25 0 11-4.5 0 2.25 2.25 0 014.5 0z" />
            </svg>
            Manajemen Pengguna
          </router-link>
          <router-link to="/health" class="nav-item" active-class="active-nav">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="nav-icon">
              <path stroke-linecap="round" stroke-linejoin="round" d="M9 12.75L11.25 15 15 9.75M21 12c0 1.268-.63 2.39-1.593 3.068a3.745 3.745 0 01-1.043 3.296 3.745 3.745 0 01-3.296 1.043A3.745 3.745 0 0112 21c-1.268 0-2.39-.63-3.068-1.593a3.746 3.746 0 01-3.296-1.043 3.745 3.745 0 01-1.043-3.296A3.745 3.745 0 013 12c0-1.268.63-2.39 1.593-3.068a3.746 3.746 0 011.043-3.296 3.746 3.746 0 013.296-1.043A3.746 3.746 0 0112 3c1.268 0 2.39.63 3.068 1.593a3.746 3.746 0 013.296 1.043 3.746 3.746 0 011.043 3.296A3.745 3.745 0 0121 12z" />
            </svg>
            Integrasi Sistem
          </router-link>
          <!-- Button Logout -->
          <button @click="handleLogout" class="nav-item text-red-400 hover:text-red-300 hover:bg-red-950/20 border border-transparent">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="nav-icon text-red-400">
              <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 9V5.25A2.25 2.25 0 0013.5 3h-6a2.25 2.25 0 00-2.25 2.25v13.5A2.25 2.25 0 007.5 21h6a2.25 2.25 0 002.25-2.25V15M12 9l-3 3m0 0l3 3m-3-3h12.75" />
            </svg>
            Keluar
          </button>
        </nav>
      </div>
    </header>

    <main class="app-main">
      <div class="container">
        <router-view />
      </div>
    </main>

    <footer class="app-footer">
      <div class="container footer-container">
        <p>&copy; 2026 POS Monolith Architecture Mockup. Decoupled Frontend + Java RESTful API.</p>
        <div class="tech-tags">
          <span class="tech-tag">Spring Boot 3</span>
          <span class="tech-tag">Vue 3</span>
          <span class="tech-tag">TypeScript</span>
          <span class="tech-tag">Pinia</span>
        </div>
      </div>
    </footer>
  </template>
</template>

<style scoped>
.app-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(11, 15, 25, 0.7);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-bottom: 1px solid var(--border-color);
  padding: 1rem 0;
}

.header-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.logo-symbol {
  width: 2.25rem;
  height: 2.25rem;
  background: linear-gradient(135deg, var(--primary) 0%, #a855f7 100%);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
}

.logo-svg {
  width: 1.25rem;
  height: 1.25rem;
  color: #ffffff;
}

.logo-text {
  font-size: 1.25rem;
  font-weight: 700;
  color: #ffffff;
  letter-spacing: -0.025em;
}

.accent-text {
  color: var(--primary);
  background: linear-gradient(to right, var(--primary), #a855f7);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.nav-links {
  display: flex;
  gap: 0.75rem;
  align-items: center;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  color: var(--text-secondary);
  font-size: 0.9rem;
  font-weight: 500;
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);
  background: transparent;
  cursor: pointer;
}

.nav-item:hover {
  color: #ffffff;
  background: rgba(255, 255, 255, 0.03);
}

.nav-icon {
  width: 1.15rem;
  height: 1.15rem;
}

.active-nav {
  color: #ffffff;
  background: rgba(99, 102, 241, 0.08);
  border: 1px solid rgba(99, 102, 241, 0.2);
}

.app-main {
  flex-grow: 1;
}

.app-footer {
  border-top: 1px solid var(--border-color);
  padding: 1.5rem 0;
  background: rgba(17, 24, 39, 0.3);
  color: var(--text-muted);
  font-size: 0.85rem;
}

.footer-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 1rem;
}

.tech-tags {
  display: flex;
  gap: 0.5rem;
}

.tech-tag {
  background: rgba(75, 85, 99, 0.15);
  color: var(--text-secondary);
  padding: 0.2rem 0.5rem;
  border-radius: 4px;
  font-size: 0.75rem;
  border: 1px solid rgba(75, 85, 99, 0.2);
}
</style>
