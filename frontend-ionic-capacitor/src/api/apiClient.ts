import { API_BASE_URL } from '../config/backend';

export type ApiErrorBody = {
  message?: string;
  error?: string;
  status?: number;
  timestamp?: string | number;
};

export class ApiClientError extends Error {
  status: number;
  body?: ApiErrorBody | string;

  constructor(message: string, status: number, body?: ApiErrorBody | string) {
    super(message);
    this.name = 'ApiClientError';
    this.status = status;
    this.body = body;
  }
}

function buildUrl(path: string) {
  const normalizedPath = path.startsWith('/') ? path : `/${path}`;
  return `${API_BASE_URL}${normalizedPath}`;
}

function clearSession() {
  localStorage.removeItem('accessToken');
  localStorage.removeItem('refreshToken');
  localStorage.removeItem('userId');
  localStorage.removeItem('userName');
}

async function readError(res: Response): Promise<{ message: string; body?: ApiErrorBody | string }> {
  const fallback = res.status === 503
    ? 'El servicio no está disponible temporalmente. Revisa que el BFF y el microservicio correspondiente estén levantados.'
    : `HTTP ${res.status}`;

  const contentType = res.headers.get('content-type') ?? '';

  try {
    if (contentType.includes('application/json')) {
      const data = (await res.json()) as ApiErrorBody;
      return { message: data.message ?? data.error ?? fallback, body: data };
    }

    const text = await res.text();
    return { message: text || fallback, body: text };
  } catch {
    return { message: fallback };
  }
}

async function tryRefreshToken(): Promise<boolean> {
  const refreshToken = localStorage.getItem('refreshToken');
  if (!refreshToken) return false;

  try {
    const res = await fetch(buildUrl('/api/auth/refresh-token'), {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ refreshToken }),
    });

    if (!res.ok) return false;

    const data = await res.json();
    if (!data.accessToken) return false;

    localStorage.setItem('accessToken', data.accessToken);
    if (data.refreshToken) localStorage.setItem('refreshToken', data.refreshToken);
    return true;
  } catch {
    return false;
  }
}

async function request<T>(
  path: string,
  options: RequestInit = {},
  withAuth = false,
  retry = true,
): Promise<T> {
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(options.headers as Record<string, string>),
  };

  if (withAuth) {
    const token = localStorage.getItem('accessToken');
    if (token) headers.Authorization = `Bearer ${token}`;
  }

  const userId = localStorage.getItem('userId');
  if (userId) headers['X-User-Id'] = userId;

  const userName = localStorage.getItem('userName');
  if (userName) headers['X-User-Name'] = userName;

  const res = await fetch(buildUrl(path), { ...options, headers });

  if (res.status === 401 && withAuth && retry) {
    const refreshed = await tryRefreshToken();
    if (refreshed) return request<T>(path, options, withAuth, false);

    clearSession();
    window.location.href = '/login';
    throw new ApiClientError('Sesión expirada. Vuelve a iniciar sesión.', 401);
  }

  if (res.status === 204) return undefined as T;

  if (!res.ok) {
    const { message, body } = await readError(res);
    throw new ApiClientError(message, res.status, body);
  }

  const contentType = res.headers.get('content-type') ?? '';
  if (!contentType.includes('application/json')) return undefined as T;

  return res.json() as Promise<T>;
}

export const apiClient = {
  get<T>(path: string, withAuth = false) {
    return request<T>(path, { method: 'GET' }, withAuth);
  },
  post<T>(path: string, body: unknown, withAuth = false) {
    return request<T>(path, { method: 'POST', body: JSON.stringify(body) }, withAuth);
  },
  put<T>(path: string, body: unknown, withAuth = false) {
    return request<T>(path, { method: 'PUT', body: JSON.stringify(body) }, withAuth);
  },
  patch<T>(path: string, body: unknown, withAuth = false) {
    return request<T>(path, { method: 'PATCH', body: JSON.stringify(body) }, withAuth);
  },
  delete<T>(path: string, withAuth = false) {
    return request<T>(path, { method: 'DELETE' }, withAuth);
  },
};
