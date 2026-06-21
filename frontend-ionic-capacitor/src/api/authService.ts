import type { AuthResponse, LoginRequest, RegisterRequest, User } from '../types/auth';
import { apiClient, ApiClientError } from './apiClient';

export const authService = {
  login(data: LoginRequest): Promise<AuthResponse> {
    return apiClient.post<AuthResponse>('/api/auth/login', {
      identifier: data.identifier.trim(),
      password: data.password,
    });
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
    return apiClient.get<User>(`/api/users/username/${encodeURIComponent(userName.trim())}`, true);
  },

  getAllUsers(): Promise<User[]> {
    return apiClient.get<User[]>('/api/users', true);
  },

  async findUser(term: string): Promise<User> {
    const query = term.trim();
    if (!query) throw new ApiClientError('Debes ingresar un usuario, correo o nombre.', 400);

    try {
      return await this.getUserByUsername(query);
    } catch (error) {
      if (!(error instanceof ApiClientError) || error.status !== 404) throw error;
    }

    const normalized = query.toLowerCase();
    const users = await this.getAllUsers();
    const found = users.find((item) => {
      const fullName = `${item.firstName ?? ''} ${item.lastName ?? ''}`.trim().toLowerCase();
      return item.userName.toLowerCase() === normalized
        || item.email.toLowerCase() === normalized
        || fullName === normalized
        || fullName.replace(/\s+/g, '') === normalized.replace(/\s+/g, '');
    });

    if (!found) throw new ApiClientError('Usuario no encontrado.', 404);
    return found;
  },
};
