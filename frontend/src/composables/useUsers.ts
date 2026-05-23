import { ref, computed } from 'vue';
import { useUserStore } from '../stores/userStore';
import type { UserRequest, UserResponse } from '../types';

export function useUsers() {
  const store = useUserStore();
  
  const showModal = ref(false);
  const editingUser = ref<UserResponse | null>(null);
  
  const form = ref<UserRequest>({
    username: '',
    email: '',
    fullName: '',
    role: 'USER',
    active: true,
  });

  const users = computed(() => store.users);
  const loading = computed(() => store.loading);
  const error = computed(() => store.error);

  const resetForm = () => {
    form.value = {
      username: '',
      email: '',
      fullName: '',
      role: 'USER',
      active: true,
    };
    editingUser.value = null;
    store.clearError();
  };

  const openAddModal = () => {
    resetForm();
    showModal.value = true;
  };

  const openEditModal = (user: UserResponse) => {
    store.clearError();
    editingUser.value = user;
    form.value = {
      username: user.username,
      email: user.email,
      fullName: user.fullName,
      role: user.role,
      active: user.active,
    };
    showModal.value = true;
  };

  const closeModal = () => {
    showModal.value = false;
    resetForm();
  };

  const saveUser = async () => {
    try {
      if (editingUser.value) {
        await store.editUser(editingUser.value.id, form.value);
      } else {
        await store.addUser(form.value);
      }
      closeModal();
      return true;
    } catch (err) {
      // Error akan ditampung di store.error melalui interceptor
      return false;
    }
  };

  const deleteUser = async (id: number) => {
    if (confirm('Apakah Anda yakin ingin menghapus pengguna ini?')) {
      try {
        await store.removeUser(id);
        return true;
      } catch (err) {
        return false;
      }
    }
    return false;
  };

  return {
    users,
    loading,
    error,
    showModal,
    editingUser,
    form,
    openAddModal,
    openEditModal,
    closeModal,
    saveUser,
    deleteUser,
    clearError: store.clearError,
    fetchUsers: store.fetchUsers,
  };
}
