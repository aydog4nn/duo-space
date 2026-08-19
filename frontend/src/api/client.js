const API_PREFIX = '/api/v1';

export class ApiError extends Error {
  constructor(message, status) {
    super(message);
    this.status = status;
  }
}

export async function apiRequest(path, token, options = {}) {
  const headers = { 'Content-Type': 'application/json', ...options.headers };
  if (token) headers.Authorization = `Bearer ${token}`;

  const response = await fetch(`${API_PREFIX}${path}`, { ...options, headers });
  const body = response.status === 204 ? null : await response.json().catch(() => null);

  if (!response.ok) throw new ApiError(body?.message || 'İşlem tamamlanamadı.', response.status);
  return body;
}
