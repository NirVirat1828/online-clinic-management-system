// Centralized fetch helper for all API calls
const API_BASE = '/api';

function buildUrl(path, params) {
  if (!params) return path;
  const qs = new URLSearchParams(
    Object.entries(params).filter(([, v]) => v !== null && v !== undefined && v !== '')
  ).toString();
  return qs ? `${path}?${qs}` : path;
}

export async function apiFetch(path, { method = 'GET', body, headers = {}, params } = {}) {
  const token = localStorage.getItem('authToken');
  const finalHeaders = {
    'Content-Type': 'application/json',
    ...headers
  };
  if (token) finalHeaders.Authorization = `Bearer ${token}`;

  const url = buildUrl(path.startsWith('/api') ? path : `${API_BASE}${path.startsWith('/') ? '' : '/'}${path}`, params);

  const resp = await fetch(url, {
    method,
    headers: finalHeaders,
    body: body ? JSON.stringify(body) : undefined
  });

  const isJson = resp.headers.get('content-type')?.includes('application/json');
  const data = isJson ? await resp.json().catch(() => null) : null;

  if (resp.status === 401) {
    // optional: auto logout
    console.warn('Unauthorized - clearing session.');
    // window.location.href = '/';
  }
  if (!resp.ok) {
    throw { status: resp.status, data };
  }
  return data;
}