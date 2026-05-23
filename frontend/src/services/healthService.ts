import api from './api';
import type { HealthCheckDto } from '../types';

export const healthService = {
  async getHealth(): Promise<HealthCheckDto> {
    const response = await api.get<HealthCheckDto>('/health');
    return response.data;
  }
};
