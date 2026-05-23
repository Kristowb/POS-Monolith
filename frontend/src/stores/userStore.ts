import { defineStore } from 'pinia';
import { userService } from '../services/userService';
import type { UserRequest, UserResponse, ProblemDetail } from '../types';

interface UserState {
  users: UserResponse[];
  currentUser: UserResponse | null;
  loading: boolean;
  error: ProblemDetail | null;
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    users: [],
    currentUser: null,
    loading: false,
    error: null,
  }),

  actions: {
    clearError() {
      this.error = null;
    },

    async fetchUsers() {
      this.loading = true;
      this.error = null;
      try {
        this.users = await userService.getAllUsers();
      } catch (err: any) {
        this.error = err as ProblemDetail;
      } finally {
        this.loading = false;
      }
    },

    async fetchUserById(id: number) {
      this.loading = true;
      this.error = null;
      try {
        this.currentUser = await userService.getUserById(id);
      } catch (err: any) {
        this.error = err as ProblemDetail;
      } finally {
        this.loading = false;
      }
    },

    async addUser(request: UserRequest) {
      this.loading = true;
      this.error = null;
      try {
        const newUser = await userService.createUser(request);
        this.users.push(newUser);
        return newUser;
      } catch (err: any) {
        this.error = err as ProblemDetail;
        throw err;
      } finally {
        this.loading = false;
      }
    },

    async editUser(id: number, request: UserRequest) {
      this.loading = true;
      this.error = null;
      try {
        const updatedUser = await userService.updateUser(id, request);
        const index = this.users.findIndex((u) => u.id === id);
        if (index !== -1) {
          this.users[index] = updatedUser;
        }
        if (this.currentUser?.id === id) {
          this.currentUser = updatedUser;
        }
        return updatedUser;
      } catch (err: any) {
        this.error = err as ProblemDetail;
        throw err;
      } finally {
        this.loading = false;
      }
    },

    async removeUser(id: number) {
      this.loading = true;
      this.error = null;
      try {
        await userService.deleteUser(id);
        this.users = this.users.filter((u) => u.id !== id);
        if (this.currentUser?.id === id) {
          this.currentUser = null;
        }
      } catch (err: any) {
        this.error = err as ProblemDetail;
        throw err;
      } finally {
        this.loading = false;
      }
    },
  },
});
