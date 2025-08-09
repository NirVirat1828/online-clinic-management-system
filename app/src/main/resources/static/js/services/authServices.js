import { apiFetch } from './http.js';

export async function adminLogin(principal, password) {
  const res = await apiFetch('/auth/admin/login', {
    method: 'POST',
    body: { principal, password }
  });
  storeAuth(res, 'ADMIN');
  return res;
}

export async function patientLogin(email, password) {
  const res = await apiFetch('/auth/patient/login', {
    method: 'POST',
    body: { email, password }
  });
  storeAuth(res, 'PATIENT');
  return res;
}

// Placeholder if you later add doctor login separately
export async function doctorLogin(principal, password) {
  const res = await apiFetch('/auth/admin/login', { // adapt if doctor endpoint added
    method: 'POST',
    body: { principal, password }
  });
  storeAuth(res, 'DOCTOR');
  return res;
}

export function logout() {
  localStorage.removeItem('authToken');
  localStorage.removeItem('role');
  localStorage.removeItem('userId');
  window.location.href = '/';
}

function storeAuth(res, fallbackRole) {
  // Expecting { token, role, userId } or similar
  if (!res) return;
  if (res.token) localStorage.setItem('authToken', res.token);
  if (res.role) localStorage.setItem('role', res.role);
  else if (fallbackRole) localStorage.setItem('role', fallbackRole);
  if (res.userId) localStorage.setItem('userId', res.userId);
}