// SkillBridge AI - Authentication & Session Management Module

const TOKEN_KEY = 'skillbridge_token';
const USER_KEY = 'skillbridge_user';

// Token and User session storage helpers
function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

function getUser() {
  const userJson = localStorage.getItem(USER_KEY);
  if (!userJson) return null;
  try {
    return JSON.parse(userJson);
  } catch {
    return null;
  }
}

function setSession(token, user) {
  localStorage.setItem(TOKEN_KEY, token);
  localStorage.setItem(USER_KEY, JSON.stringify(user));
}

function clearSession() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(USER_KEY);
}

function isLoggedIn() {
  const token = getToken();
  const user = getUser();
  return Boolean(token && user);
}

function logout() {
  clearSession();
  window.location.href = '/login.html';
}

// Authenticated wrapper for fetch that automatically attaches JWT
async function authFetch(url, options = {}) {
  const token = getToken();
  const headers = Object.assign({}, options.headers || {});

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const response = await fetch(url, { ...options, headers });

  // Handle expired/invalid token
  if (response.status === 401 && !url.includes('/api/auth/login')) {
    clearSession();
    window.location.href = '/login.html?expired=true';
    throw new Error('Your session has expired. Please sign in again.');
  }

  return response;
}

// UI Alert Helper
function displayAlert(containerId, type, message) {
  const alertEl = document.getElementById(containerId);
  if (!alertEl) return;
  alertEl.className = `alert alert-${type} mt-3 mb-0 alert-banner d-flex align-items-center`;
  alertEl.innerHTML = `
    <i class="bi ${type === 'success' ? 'bi-check-circle-fill text-success' : 'bi-exclamation-triangle-fill text-danger'} me-2 fs-5"></i>
    <span>${escapeHtml(message)}</span>
  `;
  alertEl.classList.remove('d-none');
}

function hideAlert(containerId) {
  const alertEl = document.getElementById(containerId);
  if (alertEl) alertEl.classList.add('d-none');
}

function escapeHtml(value) {
  return String(value ?? '').replace(/[&<>"']/g, c => ({
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#39;'
  }[c]));
}

// ==========================================
// Login Page Controller (if on login.html)
// ==========================================
const loginForm = document.getElementById('loginForm');
if (loginForm) {
  // If already logged in, redirect to dashboard
  if (isLoggedIn()) {
    window.location.href = '/';
  }

  // Check URL params for session expiration notice
  const urlParams = new URLSearchParams(window.location.search);
  if (urlParams.get('expired') === 'true') {
    displayAlert('loginAlert', 'warning', 'Your session has expired. Please sign in again.');
  } else if (urlParams.get('registered') === 'true') {
    displayAlert('loginAlert', 'success', 'Registration successful! Please sign in with your credentials.');
  }

  // Quick preset demo account fills
  const fillStudentBtn = document.getElementById('fillDemoStudent');
  if (fillStudentBtn) {
    fillStudentBtn.addEventListener('click', () => {
      document.getElementById('email').value = 'student@skillbridge.ai';
      document.getElementById('password').value = 'Student@123';
    });
  }

  const fillAdminBtn = document.getElementById('fillDemoAdmin');
  if (fillAdminBtn) {
    fillAdminBtn.addEventListener('click', () => {
      document.getElementById('email').value = 'admin@skillbridge.ai';
      document.getElementById('password').value = 'Admin@123';
    });
  }

  // Password visibility toggle
  const togglePwdBtn = document.getElementById('togglePassword');
  if (togglePwdBtn) {
    togglePwdBtn.addEventListener('click', () => {
      const pwdInput = document.getElementById('password');
      const isPwd = pwdInput.type === 'password';
      pwdInput.type = isPwd ? 'text' : 'password';
      togglePwdBtn.innerHTML = isPwd ? '<i class="bi bi-eye-slash"></i>' : '<i class="bi bi-eye"></i>';
    });
  }

  loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    hideAlert('loginAlert');

    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;
    const submitBtn = document.getElementById('loginSubmitBtn');

    if (!email || !password) {
      displayAlert('loginAlert', 'danger', 'Please enter both email and password.');
      return;
    }

    submitBtn.disabled = true;
    submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2" role="status"></span>Signing in...';

    try {
      const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
      });

      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.message || 'Invalid email or password.');
      }

      // Store JWT token and user info
      setSession(data.token, {
        id: data.id,
        fullName: data.fullName,
        email: data.email,
        role: data.role
      });

      displayAlert('loginAlert', 'success', 'Login successful! Redirecting to your dashboard...');
      setTimeout(() => {
        window.location.href = '/';
      }, 700);
    } catch (err) {
      displayAlert('loginAlert', 'danger', err.message || 'Unable to connect to SkillBridge AI backend.');
      submitBtn.disabled = false;
      submitBtn.innerHTML = '<i class="bi bi-box-arrow-in-right me-1"></i> Sign In to SkillBridge';
    }
  });
}

// ==========================================
// Registration Page Controller (if on register.html)
// ==========================================
const registerForm = document.getElementById('registerForm');
if (registerForm) {
  // If already logged in, redirect to dashboard
  if (isLoggedIn()) {
    window.location.href = '/';
  }

  const pwd = document.getElementById('regPassword');
  const confirmPwd = document.getElementById('regConfirmPassword');
  const matchIndicator = document.getElementById('passwordMatchIndicator');

  function checkPasswordMatch() {
    if (!matchIndicator) return;
    if (!pwd.value || !confirmPwd.value) {
      matchIndicator.textContent = '';
      return;
    }
    if (pwd.value === confirmPwd.value) {
      matchIndicator.textContent = '✓ Passwords match';
      matchIndicator.className = 'form-text text-success small';
    } else {
      matchIndicator.textContent = '✗ Passwords do not match';
      matchIndicator.className = 'form-text text-danger small';
    }
  }

  if (pwd && confirmPwd) {
    pwd.addEventListener('input', checkPasswordMatch);
    confirmPwd.addEventListener('input', checkPasswordMatch);
  }

  registerForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    hideAlert('registerAlert');

    const fullName = document.getElementById('fullName').value.trim();
    const email = document.getElementById('regEmail').value.trim();
    const password = pwd.value;
    const confirmPassword = confirmPwd.value;
    const submitBtn = document.getElementById('registerSubmitBtn');

    if (!fullName || !email || !password || !confirmPassword) {
      displayAlert('registerAlert', 'danger', 'All fields are required.');
      return;
    }

    if (password.length < 6) {
      displayAlert('registerAlert', 'danger', 'Password must be at least 6 characters long.');
      return;
    }

    if (password !== confirmPassword) {
      displayAlert('registerAlert', 'danger', 'Passwords do not match. Please re-enter.');
      return;
    }

    submitBtn.disabled = true;
    submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2" role="status"></span>Creating account...';

    try {
      const response = await fetch('/api/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ fullName, email, password, confirmPassword })
      });

      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.message || 'Registration failed.');
      }

      // Automatically store token session and direct to dashboard
      setSession(data.token, {
        id: data.id,
        fullName: data.fullName,
        email: data.email,
        role: data.role
      });

      displayAlert('registerAlert', 'success', 'Account created successfully! Loading your personal dashboard...');
      setTimeout(() => {
        window.location.href = '/';
      }, 900);
    } catch (err) {
      displayAlert('registerAlert', 'danger', err.message || 'Unable to register at this time.');
      submitBtn.disabled = false;
      submitBtn.innerHTML = '<i class="bi bi-person-plus-fill me-1"></i> Create Student Account';
    }
  });
}
