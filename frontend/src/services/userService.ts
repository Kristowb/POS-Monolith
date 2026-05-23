import api from './api';
import type { UserRequest, UserResponse } from '../types';

export const userService = {
  async getAllUsers(): Promise<UserResponse[]> {
    const response = await api.get<UserResponse[]>('/users');
    return response.data;
  },

  async getUserById(id: number): Promise<UserResponse> {
    const response = await api.get<UserResponse>(`/users/${id}`);
    return response.data;
  },

  async createUser(request: UserRequest): Promise<UserResponse> {
    const response = await api.post<UserResponse>('/users', request);
    return response.data;
  },

  async updateUser(id: number, request: UserRequest): Promise<UserResponse> {
    const response = await api.put<UserResponse>(`/users/${id}`, request);
    return response.data;
  },

  async deleteUser(id: number): Promise<void> {
    await api.delete(`/users/${id}`);
  }
};
