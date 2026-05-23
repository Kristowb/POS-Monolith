// Impor tipe data yang di-generate otomatis dari DTO Java
import type { HealthCheckDto, UserRequest, UserResponse } from './backend-models';

export type { HealthCheckDto, UserRequest, UserResponse };

// RFC 7807 Problem Details interface (untuk penanganan error global)
export interface ProblemDetail {
  type?: string;
  title?: string;
  status?: number;
  detail?: string;
  instance?: string;
  timestamp?: string;
  invalid_params?: Record<string, string>;
  [key: string]: any; // Untuk field kustom tambahan
}
