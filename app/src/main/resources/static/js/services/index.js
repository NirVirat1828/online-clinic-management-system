/**
 * Login Handlers Module
 *
 * Responsibilities:
 *  - Bind Admin & Doctor login buttons to open their respective modals.
 *  - Provide adminLoginHandler() and doctorLoginHandler() functions
 *    that perform authentication, store token, and set the active role.
 *
 * Assumptions:
 *  - config.js exports: API_BASE_URL
 *  - modals.js exports: openModal(type)  (or defines window.openModal)
 *  - header.js exports: selectRole(role) (or defines window.selectRole)
 *
 * Endpoints (adjust if your backend differs):
 *  - Admin login:  POST  {API_BASE_URL}/admin/login
 *  - Doctor login: POST  {API_BASE_URL}/doctor/login
 *
 * LocalStorage Keys:
 *  - token: authentication token (valid for admin/doctor/loggedPatient)
 *  - userRole: role string ("admin" | "doctor" | ...)
 */

import { API_BASE_URL } from "../config.js"; // Adjust path if needed
// If using ES modules and openModal is exported:
// import { openModal } from "../components/modals.js";

/* -------------------- Endpoint Constants -------------------- */
export const ADMIN_LOGIN_API = `${API_BASE_URL}/admin/login`;
export const DOCTOR_LOGIN_API = `${API_BASE_URL}/doctor/login`;

/* -------------------- Utility Accessors -------------------- */
function getOpenModalFn() {
  if (typeof openModal === "function") return openModal;
  if (typeof window !== "undefined" && typeof window.openModal === "function")
    return window.openModal;
  return null;
}

function getSelectRoleFn() {
  if (typeof selectRole === "function") return selectRole;
  if (typeof window !== "undefined" && typeof window.selectRole === "function")
    return window.selectRole;
  return null;
}

/* -------------------- Button Binding on Load -------------------- */
window.addEventListener("load", () => {
  const adminBtn = document.getElementById("adminLogin");
  const doctorBtn = document.getElementById("doctorLogin");

  const om = getOpenModalFn();

  if (adminBtn && om) {
    adminBtn.addEventListener("click", () => om("adminLogin"));
  }
  if (doctorBtn && om) {
    doctorBtn.addEventListener("click", () => om("doctorLogin"));
  }
});

/* -------------------- Admin Login Handler -------------------- */
/**
 * Triggered when admin submits login form inside the modal.
 * Expected input fields (inside modal):
 *  - #username
 *  - #password
 *
 * Sets localStorage token & userRole, then calls selectRole('admin').
 */
async function adminLoginHandler() {
  const usernameInput = document.getElementById("username");
  const passwordInput = document.getElementById("password");

  const username = usernameInput?.value?.trim() || "";
  const password = passwordInput?.value || "";

  if (!username || !password) {
    alert("Please enter username and password.");
    return;
  }

  const payload = { username, password };

  try {
    const resp = await fetch(ADMIN_LOGIN_API, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Accept: "application/json"
      },
      body: JSON.stringify(payload)
    });

    if (!resp.ok) {
      // Attempt to parse error body
      let errMsg = "Invalid admin credentials.";
      try {
        const errBody = await resp.json();
        if (errBody && (errBody.message || errBody.error)) {
          errMsg = errBody.message || errBody.error;
        }
      } catch (_) {
        /* ignore parse errors */
      }
      alert(errMsg);
      return;
    }

    const data = await resp.json();
    const token = data?.token;
    if (!token) {
      alert("Login succeeded but token not received.");
      return;
    }

    localStorage.setItem("token", token);
    localStorage.setItem("userRole", "admin");

    const selectRoleFn = getSelectRoleFn();
    if (selectRoleFn) {
      selectRoleFn("admin");
    } else {
      // Fallback: simple reload
      window.location.reload();
    }
  } catch (err) {
    console.error("Admin login error:", err);
    alert("Unable to login right now. Please try again later.");
  }
}

/* -------------------- Doctor Login Handler -------------------- */
/**
 * Triggered when doctor submits login form inside the modal.
 * Expected input fields (inside modal):
 *  - #email
 *  - #password
 *
 * Sets localStorage token & userRole, then calls selectRole('doctor').
 */
async function doctorLoginHandler() {
  // Note: In separate modals email/password IDs may overlap; scoping by modal container is better.
  const emailInput = document.getElementById("email");
  const passwordInput = document.getElementById("password");

  const email = emailInput?.value?.trim() || "";
  const password = passwordInput?.value || "";

  if (!email || !password) {
    alert("Please enter email and password.");
    return;
  }

  const payload = { email, password };

  try {
    const resp = await fetch(DOCTOR_LOGIN_API, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Accept: "application/json"
      },
      body: JSON.stringify(payload)
    });

    if (!resp.ok) {
      let errMsg = "Invalid doctor credentials.";
      try {
        const errBody = await resp.json();
        if (errBody && (errBody.message || errBody.error)) {
          errMsg = errBody.message || errBody.error;
        }
      } catch (_) {
        /* ignore */
      }
      alert(errMsg);
      return;
    }

    const data = await resp.json();
    const token = data?.token;
    if (!token) {
      alert("Login succeeded but token not received.");
      return;
    }

    localStorage.setItem("token", token);
    localStorage.setItem("userRole", "doctor");

    const selectRoleFn = getSelectRoleFn();
    if (selectRoleFn) {
      selectRoleFn("doctor");
    } else {
      window.location.reload();
    }
  } catch (err) {
    console.error("Doctor login error:", err);
    alert("Unable to login right now. Please try again later.");
  }
}

/* -------------------- Expose to Global Scope -------------------- */
// These must be globally accessible because the modal markup attaches listeners after rendering.
if (typeof window !== "undefined") {
  window.adminLoginHandler = adminLoginHandler;
  window.doctorLoginHandler = doctorLoginHandler;
}

export { adminLoginHandler, doctorLoginHandler };