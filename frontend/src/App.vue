<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from './stores/authStore';
import api from './services/api';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const menus = ref<any[]>([]);

const fetchMenus = async () => {
  if (authStore.isAuthenticated) {
    try {
      const response = await api.get('/menus');
      menus.value = response.data;
    } catch (err) {
      console.error('Gagal memuat menu dinamis:', err);
    }
  } else {
    menus.value = [];
  }
};

onMounted(() => {
  fetchMenus();
});

watch(() => authStore.isAuthenticated, () => {
  fetchMenus();
});

// Refresh menu saat level naik
watch(() => authStore.currentUser?.level, () => {
  fetchMenus();
});

const handleLogout = async () => {
  await authStore.logout();
  router.push('/login');
};

const getUserClassColor = (roleCode?: string) => {
  if (!roleCode) return 'color-knight';
  if (roleCode.includes('KNIGHT')) return 'color-knight';
  if (roleCode.includes('ARCHER')) return 'color-archer';
  if (roleCode.includes('WARRIOR')) return 'color-warrior';
  if (roleCode.includes('ADMIN')) return 'color-admin';
  return 'color-knight';
};

const getCleanClassName = (roleCode?: string) => {
  if (!roleCode) return 'User';
  return roleCode.replace('ROLE_', '');
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
          <router-link 
            v-if="authStore.isAuthenticated && authStore.currentUser?.roles?.includes('ROLE_ADMIN')" 
            to="/" 
            class="nav-item" 
            active-class="active-nav"
          >
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="nav-icon">
              <path stroke-linecap="round" stroke-linejoin="round" d="M18 18.72a9.094 9.094 0 003.741-.479 3 3 0 00-4.682-2.72m.94 3.198l.001.031c0 .225-.012.447-.037.666A11.944 11.944 0 0112 21c-2.17 0-4.207-.576-5.963-1.584A6.062 6.062 0 016 18.719m12 0a5.971 5.971 0 00-.941-3.197m0 0A5.995 5.995 0 0012 12.75a5.995 5.995 0 00-5.058 2.772m0 0a3 3 0 00-4.681 2.72 8.986 8.986 0 003.74.477m.94-3.197a5.971 5.971 0 00-.94 3.197M15 6.75a3 3 0 11-6 0 3 3 0 016 0zm6 3a2.25 2.25 0 11-4.5 0 2.25 2.25 0 014.5 0zm-13.5 0a2.25 2.25 0 11-4.5 0 2.25 2.25 0 014.5 0z" />
            </svg>
            Manajemen Pengguna
          </router-link>
          
          <router-link 
            v-if="authStore.isAuthenticated && authStore.currentUser?.roles?.includes('ROLE_ADMIN')" 
            to="/health" 
            class="nav-item" 
            active-class="active-nav"
          >
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="nav-icon">
              <path stroke-linecap="round" stroke-linejoin="round" d="M9 12.75L11.25 15 15 9.75M21 12c0 1.268-.63 2.39-1.593 3.068a3.745 3.745 0 01-1.043 3.296 3.745 3.745 0 01-3.296 1.043A3.745 3.745 0 0112 21c-1.268 0-2.39-.63-3.068-1.593a3.746 3.746 0 01-3.296-1.043 3.745 3.745 0 01-1.043-3.296A3.745 3.745 0 013 12c0-1.268.63-2.39 1.593-3.068a3.746 3.746 0 011.043-3.296 3.746 3.746 0 013.296-1.043A3.746 3.746 0 0112 3c1.268 0 2.39.63 3.068 1.593a3.746 3.746 0 013.296 1.043 3.746 3.746 0 011.043 3.296A3.745 3.745 0 0121 12z" />
            </svg>
            Integrasi Sistem
          </router-link>

          <!-- Dynamic RPG Menus -->
          <router-link 
            v-for="menu in menus" 
            :key="menu.id" 
            :to="menu.path" 
            class="nav-item" 
            active-class="active-nav"
          >
            <!-- Dynamic Icon SVGs -->
            <svg v-if="menu.icon === 'shopping-cart'" class="nav-icon" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" d="M2.25 3h1.386c.51 0 .955.343 1.087.835l.383 1.437M7.5 14.25a3 3 0 00-3 3h15.75m-12.75-3h11.218c1.121-2.3 2.1-4.684 2.924-7.138a60.114 60.114 0 00-16.536-1.84M7.5 14.25L5.106 5.272M6 20.25a.75.75 0 11-1.5 0 .75.75 0 011.5 0zm12.75 0a.75.75 0 11-1.5 0 .75.75 0 011.5 0z" />
            </svg>
            <svg v-else-if="menu.icon === 'archive'" class="nav-icon" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" d="M20.25 7.5l-.625 10.632a2.25 2.25 0 01-2.247 2.118H6.622a2.25 2.25 0 01-2.247-2.118L3.75 7.5M10 11.25h4M3.375 7.5h17.25c.621 0 1.125-.504 1.125-1.125v-1.5c0-.621-.504-1.125-1.125-1.125H3.375c-.621 0-1.125.504-1.125 1.125v1.5c0 .621.504 1.125 1.125 1.125z" />
            </svg>
            <svg v-else-if="menu.icon === 'bar-chart-2'" class="nav-icon" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" d="M3 13.125C3 12.504 3.504 12 4.125 12h2.25c.621 0 1.125.504 1.125 1.125v6.75C7.5 20.496 6.996 21 6.375 21h-2.25A1.125 1.125 0 013 19.875v-6.75zM9.75 8.625c0-.621.504-1.125 1.125-1.125h2.25c.621 0 1.125.504 1.125 1.125v11.25c0 .621-.504 1.125-1.125 1.125h-2.25a1.125 1.125 0 01-1.125-1.125V8.625zM16.5 4.125c0-.621.504-1.125 1.125-1.125h2.25C20.496 3 21 3.504 21 4.125v15.75c0 .621-.504 1.125-1.125 1.125h-2.25a1.125 1.125 0 01-1.125-1.125V4.125z" />
            </svg>
            <svg v-else class="nav-icon" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" d="M9.594 3.94c.09-.542.56-.94 1.11-.94h2.593c.55 0 1.02.398 1.11.94l.213 1.281c.063.374.313.686.645.87.074.04.147.083.22.127.324.196.72.257 1.075.124l1.217-.456a1.125 1.125 0 011.37.49l1.296 2.247a1.125 1.125 0 01-.26 1.43l-1.003.828c-.293.241-.438.613-.43.992a7.723 7.723 0 010 .255c-.008.378.137.75.43.991l1.004.827c.424.35.534.954.26 1.43l-1.298 2.247a1.125 1.125 0 01-1.369.491l-1.217-.456c-.355-.133-.75-.072-1.076.124a6.57 6.57 0 01-.22.128c-.331.183-.581.495-.644.869l-.213 1.28c-.09.543-.56.94-1.11.94h-2.594c-.55 0-1.02-.398-1.11-.94l-.213-1.281c-.062-.374-.312-.686-.644-.87a6.52 6.52 0 01-.22-.127c-.325-.196-.72-.257-1.076-.124l-1.217.456a1.125 1.125 0 01-1.369-.49l-1.297-2.247a1.125 1.125 0 01.26-1.43l1.004-.827c.292-.24.437-.613.43-.992a6.932 6.932 0 010-.255c.007-.378-.138-.75-.43-.992l-1.004-.827a1.125 1.125 0 01-.26-1.43l1.297-2.247a1.125 1.125 0 011.37-.491l1.216.456c.356.133.751.072 1.076-.124.072-.044.146-.087.22-.128.332-.183.582-.495.645-.869l.214-1.28z" />
              <path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
            </svg>
            {{ menu.title }}
          </router-link>

          <!-- RPG Profile Widget -->
          <div v-if="authStore.isAuthenticated && authStore.currentUser" class="rpg-profile-widget">
            <div class="profile-info">
              <span class="user-class" :class="getUserClassColor(authStore.currentUser.role)">
                {{ getCleanClassName(authStore.currentUser.role) }}
              </span>
              <span class="user-level">Lvl {{ authStore.currentUser.level }}</span>
            </div>
            <div class="exp-bar-container" :title="`EXP: ${authStore.currentUser.experience} / ${authStore.currentUser.level * 100}`">
              <div class="exp-bar" :style="{ width: `${(authStore.currentUser.experience / (authStore.currentUser.level * 100)) * 100}%` }"></div>
            </div>
          </div>

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

.rpg-profile-widget {
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: var(--radius-md);
  padding: 0.25rem 0.5rem;
  min-width: 110px;
}

.profile-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 0.5rem;
}

.user-class {
  font-size: 0.7rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.user-level {
  font-size: 0.7rem;
  font-weight: 800;
  color: var(--primary);
}

.exp-bar-container {
  width: 100%;
  height: 4px;
  background: rgba(255, 255, 255, 0.10);
  border-radius: 2px;
  overflow: hidden;
}

.exp-bar {
  height: 100%;
  background: linear-gradient(90deg, #10b981, #34d399);
  transition: width 0.3s ease;
}

/* Class colors */
.color-knight {
  color: #3b82f6; /* Blue */
}
.color-archer {
  color: #10b981; /* Emerald */
}
.color-warrior {
  color: #f59e0b; /* Amber */
}
.color-admin {
  color: #a855f7; /* Purple */
}
</style>
