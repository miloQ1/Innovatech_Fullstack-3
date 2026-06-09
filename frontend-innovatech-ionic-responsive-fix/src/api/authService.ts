import type { AuthResponse, LoginRequest, RegisterRequest, User } from '../types/auth';
import { apiClient, ApiClientError } from './apiClient';

function uniquePayloads(identifier: string, password: string): Record<string, string>[] {
  const trimmed = identifier.trim();
  const basePayloads = [
    { identifier: trimmed, password },
    { usernameOrEmail: trimmed, password },
    { userNameOrEmail: trimmed, password },
    { login: trimmed, password },
    trimmed.includes('@')
      ? { email: trimmed, password }
      : { userName: trimmed, password },
  ];

  const seen = new Set<string>();
  return basePayloads.filter((payload) => {
    const key = JSON.stringify(payload);
    if (seen.has(key)) return false;
    seen.add(key);
    return true;
  });
}

export const authService = {
  async login(data: LoginRequest): Promise<AuthResponse> {
    let lastError: unknown;

    for (const payload of uniquePayloads(data.identifier, data.password)) {
      try {
        return await apiClient.post<AuthResponse>('/api/auth/login', payload);
      } catch (err) {
        lastError = err;
        // Si el backend responde 401/403, las credenciales son inválidas: no insistimos.
        if (err instanceof ApiClientError && (err.status === 401 || err.status === 403)) break;
      }
    }

    throw lastError instanceof Error ? lastError : new Error('No se pudo iniciar sesión');
  },

  register(data: RegisterRequest): Promise<AuthResponse> {
    return apiClient.post<AuthResponse>('/api/auth/register', data);
  },

  me(): Promise<User> {
    return apiClient.get<User>('/api/auth/me', true);
  },

  logout(refreshToken: string): Promise<void> {
    return apiClient.post<void>('/api/auth/logout', { refreshToken });
  },
  
  getUserByUsername(userName: string): Promise<User> {
    return apiClient.get<User>(`/api/users/username/${userName}`, true);
  },
};
