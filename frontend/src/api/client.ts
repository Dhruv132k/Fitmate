import axios from 'axios';
import type {
  AuthResponse,
  CandidateCard,
  IncomingLike,
  MatchResponse,
  RegisterPayload,
  SwipeDirection,
  SwipeResult,
  UserProfile,
} from './types';

const TOKEN_KEY = 'fitmate.token';

export const tokenStore = {
  get: () => localStorage.getItem(TOKEN_KEY),
  set: (token: string) => localStorage.setItem(TOKEN_KEY, token),
  clear: () => localStorage.removeItem(TOKEN_KEY),
};

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? '/api',
  headers: { 'Content-Type': 'application/json' },
});

// OAuth is a full page browser redirect to the backend so it needs
// the backend's own origin rather than the /api proxy path
export const oauthBaseUrl = import.meta.env.VITE_OAUTH_BASE_URL ?? 'http://localhost:8080';

export function oauthLoginUrl(provider: 'google' | 'github'): string {
  return `${oauthBaseUrl}/oauth2/authorization/${provider}`;
}

api.interceptors.request.use((config) => {
  const token = tokenStore.get();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export function extractErrorMessage(error: unknown, fallback = 'Something went wrong'): string {
  if (axios.isAxiosError(error)) {
    const data = error.response?.data as { message?: string } | undefined;
    return data?.message ?? error.message ?? fallback;
  }
  return fallback;
}

export const authApi = {
  register: (payload: RegisterPayload) =>
    api.post<AuthResponse>('/auth/register', payload).then((r) => r.data),
  login: (email: string, password: string) =>
    api.post<AuthResponse>('/auth/login', { email, password }).then((r) => r.data),
};

export const profileApi = {
  me: () => api.get<UserProfile>('/profile/me').then((r) => r.data),
  update: (payload: Partial<UserProfile>) =>
    api.put<UserProfile>('/profile/me', payload).then((r) => r.data),
};

export const discoveryApi = {
  feed: () => api.get<CandidateCard[]>('/discovery/feed').then((r) => r.data),
};

export const swipeApi = {
  swipe: (targetId: number, direction: SwipeDirection) =>
    api.post<SwipeResult>('/swipes', { targetId, direction }).then((r) => r.data),
};

export const matchApi = {
  list: () => api.get<MatchResponse[]>('/matches').then((r) => r.data),
};

export const likesApi = {
  received: () => api.get<IncomingLike[]>('/likes/received').then(r => r.data),
}