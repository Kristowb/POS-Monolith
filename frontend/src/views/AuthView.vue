<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/authStore';
import type { LoginRequest, RegisterRequest } from '../types';

const router = useRouter();
const authStore = ref(useAuthStore());

// Mode: 'signin' atau 'signup'
const authMode = ref<'signin' | 'signup'>('signup');

// Tema Preview: 'dark' (SpaceX) atau 'light' (Pastel)
const themeMode = ref<'dark' | 'light'>('dark');

// State Form
const form = reactive({
  fullName: '',
  username: '',
  email: '',
  password: '',
});

const showPassword = ref(false);
const showGoogleToast = ref(false);
const googleToastMsg = ref('');

// Handler ubah mode tab
const setMode = (mode: 'signin' | 'signup') => {
  authMode.value = mode;
  authStore.value.clearError();
};

// Toggle password
const togglePasswordVisibility = () => {
  showPassword.value = !showPassword.value;
};

// Toggle Tema Preview
const toggleTheme = () => {
  themeMode.value = themeMode.value === 'dark' ? 'light' : 'dark';
};

onMounted(() => {
  renderGoogleButton();
});

const renderGoogleButton = () => {
  const google = (window as any).google;
  if (google) {
    google.accounts.id.initialize({
      client_id: import.meta.env.VITE_GOOGLE_CLIENT_ID || 'your-google-client-id.apps.googleusercontent.com',
      callback: handleGoogleLoginCallback
    });

    google.accounts.id.renderButton(
      document.getElementById('google-signin-btn'),
      { 
        theme: 'filled_black', 
        size: 'large', 
        width: 384, 
        logo_alignment: 'left',
        text: 'continue_with'
      }
    );
  } else {
    setTimeout(renderGoogleButton, 500);
  }
};

const handleGoogleLoginCallback = async (response: any) => {
  const idToken = response.credential;
  authStore.value.clearError();
  try {
    await authStore.value.loginWithGoogle(idToken);
    router.push('/');
  } catch (err) {
    console.error('Google login failed:', err);
  }
};

// Submit handler
const handleSubmit = async () => {
  authStore.value.clearError();

  if (authMode.value === 'signup') {
    // Registrasi
    if (!form.username || !form.email || !form.fullName || !form.password) {
      return;
    }
    const registerRequest: RegisterRequest = {
      username: form.username,
      email: form.email,
      fullName: form.fullName,
      password: form.password,
      role: 'ROLE_USER', // Peran default
    };

    try {
      await authStore.value.register(registerRequest);
      router.push('/');
    } catch (err) {
      console.error('Registrasi gagal:', err);
    }
  } else {
    // Login
    if (!form.email || !form.password) {
      return;
    }
    const loginRequest: LoginRequest = {
      usernameOrEmail: form.email,
      password: form.password,
    };

    try {
      await authStore.value.login(loginRequest);
      router.push('/');
    } catch (err) {
      console.error('Login gagal:', err);
    }
  }
};
</script>

<template>
  <div class="grid grid-cols-1 lg:grid-cols-12 min-h-screen bg-[#070b13] text-gray-100 font-sans overflow-x-hidden relative">
    
    <!-- Toast Notifikasi untuk Google -->
    <Transition name="fade">
      <div v-if="showGoogleToast" class="fixed top-5 left-1/2 -translate-x-1/2 z-50 bg-blue-600/90 backdrop-blur-md text-white px-5 py-3 rounded-lg shadow-xl border border-blue-400/30 flex items-center gap-3 max-w-md text-sm text-center">
        <svg class="w-5 h-5 flex-shrink-0 animate-bounce" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
        <span>{{ googleToastMsg }}</span>
      </div>
    </Transition>

    <!-- KIRI: FORM PANEL (5 Kolom) -->
    <div class="lg:col-span-5 flex flex-col justify-between p-8 lg:p-12 bg-[#090d16] border-r border-gray-800/40 relative z-10">
      
      <!-- Logo Atas -->
      <div class="flex items-center gap-3">
        <div class="w-8 h-8 rounded-lg bg-blue-600 flex items-center justify-center shadow-lg shadow-blue-500/20">
          <!-- Lightning Icon -->
          <svg class="w-4 h-4 text-white" fill="currentColor" viewBox="0 0 24 24">
            <path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z"/>
          </svg>
        </div>
        <span class="text-xl font-bold tracking-tight text-white">NovaPOS</span>
      </div>

      <!-- Main Form Container -->
      <div class="my-auto py-8 max-w-sm w-full mx-auto">
        <div class="space-y-2 mb-8">
          <h1 class="text-3xl font-extrabold tracking-tight text-white">
            {{ authMode === 'signup' ? 'Create account' : 'Welcome back' }}
          </h1>
          <p class="text-gray-400 text-sm">
            {{ authMode === 'signup' ? 'Start your free 14-day trial' : 'Please log in to manage your system' }}
          </p>
        </div>

        <!-- Tab Switcher -->
        <div class="bg-[#111726] p-1 rounded-xl flex gap-1 mb-6 border border-gray-800/30">
          <button 
            type="button"
            @click="setMode('signin')" 
            :class="[
              'flex-1 text-center py-2.5 text-xs font-semibold rounded-lg transition-all duration-300',
              authMode === 'signin' 
                ? 'bg-[#18233c] text-white shadow-md' 
                : 'text-gray-400 hover:text-gray-200'
            ]"
          >
            Sign in
          </button>
          <button 
            type="button"
            @click="setMode('signup')" 
            :class="[
              'flex-1 text-center py-2.5 text-xs font-semibold rounded-lg transition-all duration-300',
              authMode === 'signup' 
                ? 'bg-[#18233c] text-white shadow-md' 
                : 'text-gray-400 hover:text-gray-200'
            ]"
          >
            Sign up
          </button>
        </div>

        <!-- Google Auth Button (Official GSI Button) -->
        <div class="w-full flex justify-center mb-4">
          <div id="google-signin-btn"></div>
        </div>

        <!-- Divider -->
        <div class="relative flex items-center my-6">
          <div class="flex-grow border-t border-gray-800/60"></div>
          <span class="flex-shrink mx-4 text-gray-500 text-xs font-semibold uppercase tracking-wider">or</span>
          <div class="flex-grow border-t border-gray-800/60"></div>
        </div>

        <!-- REST API Alert Error (RFC 7807) -->
        <div v-if="authStore.error" class="bg-red-500/10 border border-red-500/30 rounded-xl p-4 mb-5 text-sm">
          <div class="flex items-start gap-2.5 text-red-400">
            <svg class="w-5 h-5 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/>
            </svg>
            <div>
              <p class="font-bold text-red-300">{{ authStore.error.title || 'Gagal' }}</p>
              <p class="text-xs text-red-200 mt-1">{{ authStore.error.detail || 'Terjadi kesalahan sistem.' }}</p>
            </div>
          </div>
        </div>

        <!-- Form Autentikasi -->
        <form @submit.prevent="handleSubmit" class="space-y-4">
          <!-- Full Name (Register Only) -->
          <div v-if="authMode === 'signup'" class="space-y-1">
            <label class="block text-xs font-semibold text-gray-400">Full Name *</label>
            <input 
              v-model="form.fullName"
              type="text" 
              required
              placeholder="Your full name"
              class="w-full bg-[#111726] border border-gray-800/60 focus:border-blue-500/60 focus:ring-1 focus:ring-blue-500/20 text-white rounded-xl py-3 px-4 text-sm outline-none transition-all duration-200 placeholder-gray-600"
            />
          </div>

          <!-- Username (Register Only) -->
          <div v-if="authMode === 'signup'" class="space-y-1">
            <label class="block text-xs font-semibold text-gray-400">Username *</label>
            <input 
              v-model="form.username"
              type="text" 
              required
              placeholder="yourusername"
              class="w-full bg-[#111726] border border-gray-800/60 focus:border-blue-500/60 focus:ring-1 focus:ring-blue-500/20 text-white rounded-xl py-3 px-4 text-sm outline-none transition-all duration-200 placeholder-gray-600"
            />
          </div>

          <!-- Email (Input Utama) -->
          <div class="space-y-1">
            <label class="block text-xs font-semibold text-gray-400">
              {{ authMode === 'signup' ? 'Email *' : 'Email atau Username *' }}
            </label>
            <input 
              v-model="form.email"
              :type="authMode === 'signup' ? 'email' : 'text'"
              required
              :placeholder="authMode === 'signup' ? 'you@company.com' : 'you@company.com / username'"
              class="w-full bg-[#111726] border border-gray-800/60 focus:border-blue-500/60 focus:ring-1 focus:ring-blue-500/20 text-white rounded-xl py-3 px-4 text-sm outline-none transition-all duration-200 placeholder-gray-600"
            />
          </div>

          <!-- Password -->
          <div class="space-y-1">
            <label class="block text-xs font-semibold text-gray-400">Password *</label>
            <div class="relative">
              <input 
                v-model="form.password"
                :type="showPassword ? 'text' : 'password'" 
                required
                placeholder="••••••••"
                class="w-full bg-[#111726] border border-gray-800/60 focus:border-blue-500/60 focus:ring-1 focus:ring-blue-500/20 text-white rounded-xl py-3 px-4 pr-11 text-sm outline-none transition-all duration-200 placeholder-gray-600"
              />
              <!-- Toggle Password Eye Icon -->
              <button 
                type="button"
                @click="togglePasswordVisibility"
                class="absolute right-4 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-300"
              >
                <!-- Eye Open -->
                <svg v-if="showPassword" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                </svg>
                <!-- Eye Slashed -->
                <svg v-else class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.542-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l18 18" />
                </svg>
              </button>
            </div>
          </div>

          <!-- Submit Button -->
          <button 
            type="submit"
            :disabled="authStore.loading"
            class="w-full bg-blue-600 hover:bg-blue-700 disabled:bg-blue-600/50 text-white rounded-xl py-3 px-4 text-sm font-semibold flex items-center justify-center gap-2 transition-all duration-200 mt-2 shadow-lg shadow-blue-600/10 cursor-pointer"
          >
            <!-- Spinner Loading -->
            <svg v-if="authStore.loading" class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            <span>
              {{ authMode === 'signup' ? 'Create account' : 'Sign in' }}
            </span>
            <span v-if="!authStore.loading" class="text-xs">&gt;</span>
          </button>
        </form>

        <!-- Toggle Switch di bawah tombol -->
        <p class="text-center text-xs text-gray-500 mt-6">
          {{ authMode === 'signup' ? 'Already have an account?' : "Don't have an account?" }}
          <button 
            @click="setMode(authMode === 'signup' ? 'signin' : 'signup')" 
            class="text-blue-500 hover:underline font-semibold ml-1 cursor-pointer bg-transparent border-none"
          >
            {{ authMode === 'signup' ? 'Sign in' : 'Sign up' }}
          </button>
        </p>
      </div>

      <!-- Footer Policy -->
      <div class="text-[10px] text-gray-600 leading-relaxed text-center lg:text-left mt-8">
        By continuing, you agree to our 
        <a href="#" class="underline hover:text-gray-400">Terms of Service</a> and 
        <a href="#" class="underline hover:text-gray-400">Privacy Policy</a>.
      </div>
    </div>

    <!-- KANAN: LIVE PREVIEW PANEL (7 Kolom) -->
    <!-- SpaceX Dark / Pastel Light Theme Container -->
    <div 
      :class="[
        'hidden lg:flex lg:col-span-7 flex-col justify-between p-12 transition-all duration-700 relative overflow-hidden',
        themeMode === 'dark' 
          ? 'bg-[#030611] text-gray-300' 
          : 'bg-gradient-to-tr from-pink-100/90 via-blue-50 to-orange-100/70 text-slate-800'
      ]"
    >
      
      <!-- Partikel Bintang Kelip untuk SpaceX Dark Theme -->
      <div v-if="themeMode === 'dark'" class="absolute inset-0 z-0 pointer-events-none">
        <div class="absolute w-[2px] h-[2px] bg-white rounded-full top-[15%] left-[25%] opacity-60 animate-pulse-slow"></div>
        <div class="absolute w-[3px] h-[3px] bg-blue-300 rounded-full top-[45%] left-[15%] opacity-40 animate-pulse-slow"></div>
        <div class="absolute w-[2px] h-[2px] bg-white rounded-full top-[75%] left-[35%] opacity-80 animate-pulse"></div>
        <div class="absolute w-[3px] h-[3px] bg-white rounded-full top-[25%] left-[65%] opacity-50 animate-pulse-slow"></div>
        <div class="absolute w-[2px] h-[2px] bg-blue-400 rounded-full top-[60%] left-[80%] opacity-70 animate-pulse"></div>
        <div class="absolute w-[1.5px] h-[1.5px] bg-white rounded-full top-[85%] left-[70%] opacity-90 animate-pulse"></div>
        <!-- Gradasi Cahaya Galaksi -->
        <div class="absolute w-[400px] h-[400px] bg-blue-900/10 rounded-full -top-40 -right-40 filter blur-[100px] pointer-events-none"></div>
      </div>

      <!-- Preview Header -->
      <div class="flex items-center justify-between relative z-10">
        <div class="space-y-0.5">
          <div class="flex items-center gap-2">
            <!-- Mini lightning icon -->
            <div :class="['w-5 h-5 rounded flex items-center justify-center', themeMode === 'dark' ? 'bg-blue-600' : 'bg-blue-600/90']">
              <svg class="w-2.5 h-2.5 text-white" fill="currentColor" viewBox="0 0 24 24">
                <path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z"/>
              </svg>
            </div>
            <span :class="['text-sm font-bold tracking-tight', themeMode === 'dark' ? 'text-white' : 'text-slate-900']">NovaPOS</span>
          </div>
          <div :class="['text-[9px] font-mono tracking-widest uppercase', themeMode === 'dark' ? 'text-blue-500/80' : 'text-blue-600/80']">
            POINT OF SALE · ENTERPRISE
          </div>
        </div>

        <!-- TOGGLE BUTTON (SpaceX / Pastel) -->
        <button 
          type="button"
          @click="toggleTheme"
          :class="[
            'border rounded-full py-1.5 px-4 text-[10px] font-bold tracking-wider flex items-center gap-2.5 transition-all duration-300 hover:scale-105 cursor-pointer shadow-sm',
            themeMode === 'dark' 
              ? 'border-gray-800 bg-gray-950/40 text-gray-300 hover:border-gray-700' 
              : 'border-slate-300 bg-white/70 text-slate-800 hover:bg-white hover:border-slate-400'
          ]"
        >
          <span>{{ themeMode === 'dark' ? 'DARK · SPACEX' : 'LIGHT · PASTEL' }}</span>
          
          <!-- Sun/Moon Icon -->
          <svg v-if="themeMode === 'dark'" class="w-3.5 h-3.5 text-yellow-400 animate-spin-slow" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M10 2a1 1 0 011 1v1a1 1 0 11-2 0V3a1 1 0 011-1zm4 8a4 4 0 11-8 0 4 4 0 018 0zm-.464-5.536a1 1 0 011.414 0L16.364 5.9a1 1 0 11-1.414 1.414L13.536 5.9a1 1 0 010-1.414zM16 9a1 1 0 011-1h1a1 1 0 11-2 0h-1a1 1 0 01-1-1zM3 10a1 1 0 011-1h1a1 1 0 11-2 0H4a1 1 0 01-1-1zm2.536 4.95a1 1 0 011.414 0L8.364 16.36a1 1 0 11-1.414 1.414L5.536 16.36a1 1 0 010-1.414zm9.9-1.414a1 1 0 010 1.414l-1.42 1.42a1 1 0 11-1.41-1.41l1.41-1.42a1 1 0 011.42 0zM8.364 3.636a1 1 0 010 1.414L6.95 6.464a1 1 0 11-1.414-1.414l1.414-1.414a1 1 0 011.414 0z" clip-rule="evenodd" />
          </svg>
          <svg v-else class="w-3.5 h-3.5 text-blue-600" fill="currentColor" viewBox="0 0 20 20">
            <path d="M17.293 13.293A8 8 0 016.707 2.707a8.001 8.001 0 1010.586 10.586z" />
          </svg>
        </button>
      </div>

      <!-- Preview Body (Live Dashboard Layout) -->
      <div class="my-auto py-10 max-w-xl w-full mx-auto relative z-10">
        
        <!-- Live Badge -->
        <div class="flex items-center gap-2 mb-6">
          <span class="relative flex h-2 w-2">
            <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-blue-400 opacity-75"></span>
            <span class="relative inline-flex rounded-full h-2 w-2 bg-blue-500"></span>
          </span>
          <span :class="['text-[10px] font-bold tracking-widest uppercase', themeMode === 'dark' ? 'text-blue-400' : 'text-blue-600']">
            LIVE DASHBOARD PREVIEW
          </span>
        </div>

        <!-- 3 Metrik Grid -->
        <div class="grid grid-cols-3 gap-4 mb-6">
          
          <!-- Metrik 1 -->
          <div 
            :class="[
              'p-4 rounded-xl border transition-all duration-500 shadow-lg',
              themeMode === 'dark' 
                ? 'bg-[#080d19]/80 border-gray-800/80 hover:border-blue-500/30' 
                : 'bg-white/70 border-white/80 hover:border-slate-300'
            ]"
          >
            <div :class="['text-[10px] font-semibold mb-1', themeMode === 'dark' ? 'text-gray-500' : 'text-slate-500']">Daily Revenue</div>
            <div :class="['text-lg font-bold tracking-tight mb-1', themeMode === 'dark' ? 'text-white' : 'text-slate-950']">$18,420</div>
            <div class="text-[10px] text-green-500 font-bold flex items-center gap-0.5">
              <span>+12.4%</span>
            </div>
          </div>

          <!-- Metrik 2 -->
          <div 
            :class="[
              'p-4 rounded-xl border transition-all duration-500 shadow-lg',
              themeMode === 'dark' 
                ? 'bg-[#080d19]/80 border-gray-800/80 hover:border-blue-500/30' 
                : 'bg-white/70 border-white/80 hover:border-slate-300'
            ]"
          >
            <div :class="['text-[10px] font-semibold mb-1', themeMode === 'dark' ? 'text-gray-500' : 'text-slate-500']">Transactions</div>
            <div :class="['text-lg font-bold tracking-tight mb-1', themeMode === 'dark' ? 'text-white' : 'text-slate-950']">1,284</div>
            <div class="text-[10px] text-green-500 font-bold flex items-center gap-0.5">
              <span>+8.1%</span>
            </div>
          </div>

          <!-- Metrik 3 -->
          <div 
            :class="[
              'p-4 rounded-xl border transition-all duration-500 shadow-lg',
              themeMode === 'dark' 
                ? 'bg-[#080d19]/80 border-gray-800/80 hover:border-blue-500/30' 
                : 'bg-white/70 border-white/80 hover:border-slate-300'
            ]"
          >
            <div :class="['text-[10px] font-semibold mb-1', themeMode === 'dark' ? 'text-gray-500' : 'text-slate-500']">Avg Ticket</div>
            <div :class="['text-lg font-bold tracking-tight mb-1', themeMode === 'dark' ? 'text-white' : 'text-slate-950']">$14.35</div>
            <div class="text-[10px] text-red-500/90 font-bold flex items-center gap-0.5">
              <span>-1.2%</span>
            </div>
          </div>
        </div>

        <!-- Testimonial Box (Sofia Reyes) -->
        <div 
          :class="[
            'p-6 rounded-2xl border transition-all duration-500 shadow-xl',
            themeMode === 'dark' 
              ? 'bg-[#080d19]/85 border-gray-800/90' 
              : 'bg-white/80 border-white/90'
          ]"
        >
          <!-- Stars -->
          <div class="flex gap-1 mb-4 text-yellow-400">
            <svg class="w-4 h-4 fill-current" viewBox="0 0 20 20"><path d="M10 15l-5.878 3.09 1.123-6.545L.489 6.91l6.572-.955L10 0l2.939 5.955 6.572.955-4.756 4.635 1.123 6.545z"/></svg>
            <svg class="w-4 h-4 fill-current" viewBox="0 0 20 20"><path d="M10 15l-5.878 3.09 1.123-6.545L.489 6.91l6.572-.955L10 0l2.939 5.955 6.572.955-4.756 4.635 1.123 6.545z"/></svg>
            <svg class="w-4 h-4 fill-current" viewBox="0 0 20 20"><path d="M10 15l-5.878 3.09 1.123-6.545L.489 6.91l6.572-.955L10 0l2.939 5.955 6.572.955-4.756 4.635 1.123 6.545z"/></svg>
            <svg class="w-4 h-4 fill-current" viewBox="0 0 20 20"><path d="M10 15l-5.878 3.09 1.123-6.545L.489 6.91l6.572-.955L10 0l2.939 5.955 6.572.955-4.756 4.635 1.123 6.545z"/></svg>
            <svg class="w-4 h-4 fill-current" viewBox="0 0 20 20"><path d="M10 15l-5.878 3.09 1.123-6.545L.489 6.91l6.572-.955L10 0l2.939 5.955 6.572.955-4.756 4.635 1.123 6.545z"/></svg>
          </div>

          <!-- Quote -->
          <p :class="['text-sm italic leading-relaxed mb-6 font-medium', themeMode === 'dark' ? 'text-gray-200' : 'text-slate-800']">
            "NovaPOS cut our checkout time by 40% and gave us real-time insights we never had before. It just works."
          </p>

          <!-- Profile -->
          <div class="flex items-center gap-3">
            <div class="w-9 h-9 rounded-full bg-blue-600 flex items-center justify-center text-xs font-bold text-white shadow-md shadow-blue-500/25">
              SR
            </div>
            <div>
              <div :class="['text-xs font-bold', themeMode === 'dark' ? 'text-white' : 'text-slate-900']">Sofia Reyes</div>
              <div :class="['text-[10px]', themeMode === 'dark' ? 'text-gray-500' : 'text-slate-500']">Owner, Mercado Central</div>
            </div>
          </div>
        </div>

      </div>

      <!-- Preview Footer -->
      <div class="flex items-center gap-2 relative z-10">
        <!-- blinking status dot -->
        <span class="relative flex h-2 w-2">
          <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-green-400 opacity-75"></span>
          <span class="relative inline-flex rounded-full h-2 w-2 bg-green-500"></span>
        </span>
        <span :class="['text-[10px] font-bold tracking-wide', themeMode === 'dark' ? 'text-gray-500' : 'text-slate-500']">
          All systems operational
        </span>
      </div>

    </div>

  </div>
</template>

<style scoped>
/* Transisi Toast fade */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translate(-50%, -20px);
}

/* Kustomisasi spin lambat untuk Sun Icon */
.animate-spin-slow {
  animation: spin 8s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>
