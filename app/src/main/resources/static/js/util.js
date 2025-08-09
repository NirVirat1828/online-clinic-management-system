// Generic helpers (loading, notifications, simple formatting)

export function qs(sel, root = document) {
  return root.querySelector(sel);
}

export function qsa(sel, root = document) {
  return Array.from(root.querySelectorAll(sel));
}

export function createEl(tag, options = {}) {
  const el = document.createElement(tag);
  Object.entries(options).forEach(([k, v]) => {
    if (k === 'class') el.className = v;
    else if (k === 'text') el.textContent = v;
    else if (k === 'html') el.innerHTML = v;
    else el.setAttribute(k, v);
  });
  return el;
}

export function showLoading(targetId) {
  const target = qs(`#${targetId}`);
  if (!target) return;
  target.setAttribute('data-prev-html', target.innerHTML);
  target.innerHTML = `<div class="loading">Loading...</div>`;
}

export function hideLoading(targetId) {
  const target = qs(`#${targetId}`);
  if (!target) return;
  const prev = target.getAttribute('data-prev-html');
  if (prev !== null) target.innerHTML = prev;
}

export function showToast(message, type = 'info', timeout = 3000) {
  let host = qs('#toast-host');
  if (!host) {
    host = createEl('div', { id: 'toast-host', class: 'toast-host' });
    document.body.appendChild(host);
  }
  const toast = createEl('div', { class: `toast toast--${type}`, text: message });
  host.appendChild(toast);
  setTimeout(() => toast.remove(), timeout);
}

export function requireAuth(redirect = '/') {
  const token = localStorage.getItem('authToken');
  if (!token) {
    window.location.href = redirect;
  }
}