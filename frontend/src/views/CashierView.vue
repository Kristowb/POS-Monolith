<script setup lang="ts">
import { ref } from 'vue';
import { useAuthStore } from '../stores/authStore';

const authStore = useAuthStore();
const expGainedMsg = ref('');
const showLevelUp = ref(false);

const cartItems = ref([
  { id: 1, name: 'Espresso', price: 3.5, qty: 2 },
  { id: 2, name: 'Croissant', price: 4.0, qty: 1 }
]);

const products = [
  { id: 1, name: 'Espresso', price: 3.5, icon: '☕' },
  { id: 2, name: 'Croissant', price: 4.0, icon: '🥐' },
  { id: 3, name: 'Matcha Latte', price: 4.5, icon: '🍵' },
  { id: 4, name: 'Muffin', price: 3.0, icon: '🧁' }
];

const total = ref(11.0);

const addToCart = (product: any) => {
  const existing = cartItems.value.find(item => item.id === product.id);
  if (existing) {
    existing.qty++;
  } else {
    cartItems.value.push({ ...product, qty: 1 });
  }
  recalculateTotal();
};

const recalculateTotal = () => {
  total.value = cartItems.value.reduce((sum, item) => sum + item.price * item.qty, 0);
};

const clearCart = () => {
  cartItems.value = [];
  total.value = 0;
};

const handleCheckout = async () => {
  if (cartItems.value.length === 0) return;
  
  const oldLevel = authStore.currentUser?.level || 1;
  const oldExp = authStore.currentUser?.experience || 0;
  
  // Checkout gives 15 EXP
  const updatedUser = await authStore.addExperience(15);
  
  if (updatedUser) {
    expGainedMsg.value = "+15 EXP Gained! (Checkout Success)";
    setTimeout(() => {
      expGainedMsg.value = '';
    }, 3000);

    if (updatedUser.level > oldLevel) {
      showLevelUp.value = true;
      setTimeout(() => {
        showLevelUp.value = false;
      }, 5000);
    }
    clearCart();
  }
};
</script>

<template>
  <div class="cashier-container">
    <!-- Level Up Banner -->
    <Transition name="bounce">
      <div v-if="showLevelUp" class="level-up-modal">
        <div class="level-up-content">
          <div class="crown-icon">👑</div>
          <h2>LEVEL UP!</h2>
          <p class="new-level-text">You reached Level {{ authStore.currentUser?.level }}</p>
          <p class="desc-text">New abilities and menus might be unlocked!</p>
        </div>
      </div>
    </Transition>

    <!-- EXP Notification Toast -->
    <Transition name="fade">
      <div v-if="expGainedMsg" class="exp-toast">
        ⚡ {{ expGainedMsg }}
      </div>
    </Transition>

    <div class="grid-layout">
      <!-- Left: Products Grid -->
      <div class="card products-card">
        <h2 class="card-title">NovaPOS Cashier Menu</h2>
        <div class="products-grid">
          <button 
            v-for="p in products" 
            :key="p.id" 
            @click="addToCart(p)"
            class="product-button"
          >
            <span class="product-icon">{{ p.icon }}</span>
            <span class="product-name">{{ p.name }}</span>
            <span class="product-price">${{ p.price.toFixed(2) }}</span>
          </button>
        </div>
      </div>

      <!-- Right: Cart & Receipt -->
      <div class="card cart-card">
        <h2 class="card-title">Active Order</h2>
        <div class="cart-items">
          <div v-if="cartItems.length === 0" class="empty-cart">
            🛒 Cart is empty. Select items to checkout.
          </div>
          <div v-else class="cart-list">
            <div v-for="item in cartItems" :key="item.id" class="cart-item">
              <div class="item-name">{{ item.name }} x{{ item.qty }}</div>
              <div class="item-subtotal">${{ (item.price * item.qty).toFixed(2) }}</div>
            </div>
          </div>
        </div>

        <div class="cart-summary">
          <div class="summary-row total-row">
            <span>Total:</span>
            <span>${{ total.toFixed(2) }}</span>
          </div>
          
          <div class="button-group">
            <button @click="clearCart" class="btn btn-secondary" :disabled="cartItems.length === 0">
              Clear
            </button>
            <button @click="handleCheckout" class="btn btn-primary" :disabled="cartItems.length === 0">
              Checkout (Gain +15 EXP)
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.cashier-container {
  padding: 1.5rem 0;
  position: relative;
}

.grid-layout {
  display: grid;
  grid-template-cols: 1fr;
  gap: 1.5rem;
}

@media (min-width: 768px) {
  .grid-layout {
    grid-template-cols: 2fr 1fr;
  }
}

.card {
  background: rgba(17, 24, 39, 0.4);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  padding: 1.5rem;
  backdrop-filter: blur(12px);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.card-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: #ffffff;
  margin-bottom: 1.25rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  padding-bottom: 0.75rem;
}

.products-grid {
  display: grid;
  grid-template-cols: repeat(auto-fill, minmax(140px, 1fr));
  gap: 1rem;
}

.product-button {
  background: rgba(255, 255, 255, 0.02);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: var(--radius-md);
  padding: 1.25rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
  transition: all 0.2s ease;
  cursor: pointer;
}

.product-button:hover {
  background: rgba(99, 102, 241, 0.08);
  border-color: rgba(99, 102, 241, 0.3);
  transform: translateY(-2px);
}

.product-icon {
  font-size: 2rem;
}

.product-name {
  font-size: 0.9rem;
  font-weight: 600;
  color: #ffffff;
}

.product-price {
  font-size: 0.85rem;
  color: var(--text-secondary);
}

.cart-card {
  min-height: 350px;
}

.cart-items {
  flex-grow: 1;
  overflow-y: auto;
  margin-bottom: 1.5rem;
}

.empty-cart {
  color: var(--text-muted);
  text-align: center;
  font-size: 0.9rem;
  padding: 3rem 0;
}

.cart-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.cart-item {
  display: flex;
  justify-content: space-between;
  font-size: 0.9rem;
  border-bottom: 1px dashed rgba(255, 255, 255, 0.05);
  padding-bottom: 0.5rem;
}

.item-name {
  color: #ffffff;
}

.item-subtotal {
  font-weight: 600;
  color: var(--text-secondary);
}

.cart-summary {
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  padding-top: 1rem;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 1rem;
}

.total-row {
  font-size: 1.2rem;
  font-weight: 800;
  color: #ffffff;
}

.button-group {
  display: flex;
  gap: 0.75rem;
}

.btn {
  flex: 1;
  padding: 0.75rem;
  border-radius: var(--radius-md);
  font-weight: 600;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid transparent;
}

.btn-primary {
  background: var(--primary);
  color: #ffffff;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.2);
}

.btn-primary:hover:not(:disabled) {
  background: #4f46e5;
  transform: translateY(-1px);
}

.btn-secondary {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.1);
  color: #ffffff;
}

.btn-secondary:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.08);
}

.btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

/* EXP Notification Toast */
.exp-toast {
  position: fixed;
  top: 5.5rem;
  right: 2rem;
  background: rgba(16, 185, 129, 0.9);
  color: #ffffff;
  padding: 0.75rem 1.25rem;
  border-radius: var(--radius-md);
  font-weight: 700;
  font-size: 0.85rem;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
  z-index: 999;
}

/* Level Up Modal styles */
.level-up-modal {
  position: fixed;
  inset: 0;
  background: rgba(3, 7, 18, 0.85);
  backdrop-filter: blur(8px);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.level-up-content {
  background: linear-gradient(135deg, #1e1b4b 0%, #0f0b26 100%);
  border: 2px solid #eab308;
  border-radius: var(--radius-lg);
  padding: 3rem;
  text-align: center;
  box-shadow: 0 10px 30px rgba(234, 179, 8, 0.2);
  max-width: 400px;
  width: 90%;
}

.crown-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
  animation: float 2s ease-in-out infinite;
}

h2 {
  font-size: 2.25rem;
  font-weight: 900;
  color: #eab308;
  letter-spacing: 0.05em;
  margin-bottom: 0.5rem;
}

.new-level-text {
  font-size: 1.5rem;
  font-weight: 800;
  color: #ffffff;
  margin-bottom: 1rem;
}

.desc-text {
  color: var(--text-secondary);
  font-size: 0.9rem;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

/* Transitions */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s, transform 0.3s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

.bounce-enter-active {
  animation: bounce-in 0.5s;
}
.bounce-leave-active {
  animation: bounce-in 0.5s reverse;
}

@keyframes bounce-in {
  0% { transform: scale(0.3); opacity: 0; }
  50% { transform: scale(1.05); }
  70% { transform: scale(0.9); }
  100% { transform: scale(1); opacity: 1; }
}
</style>
