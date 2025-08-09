/**
 * Header rendering and session-aware navigation builder.
 *
 * Responsibilities:
 *  - Dynamically render the header based on user role & token.
 *  - Provide login / signup / add doctor / navigation buttons.
 *  - Gracefully handle expired sessions (role but missing token).
 *  - Expose utility logout functions.
 *
 * Assumptions:
 *  - A container with id="header" may exist in the DOM (if not, it will be created at the top of <body>).
 *  - modals.js exports openModal(type) (ES module) OR defines window.openModal if using classic scripts.
 *  - Roles used: "admin", "doctor", "loggedPatient", "patient" (pre-login patient context), or null/undefined.
 *  - localStorage keys:
 *      userRole -> one of the roles above
 *      token    -> session token for authenticated roles (admin, doctor, loggedPatient)
 *
 * You can adapt paths / role names as needed for your backend.
 */

 // If you are using ES modules, uncomment the import below.
 // import { openModal } from "./modals.js";

/**
 * Safely get a reference to openModal whether in module or global scope.
 * @returns {Function|undefined}
 */
function getOpenModalFn() {
  if (typeof openModal === "function") return openModal;
  if (typeof window !== "undefined" && typeof window.openModal === "function") return window.openModal;
  return undefined;
}

/**
 * Retrieve current role from localStorage.
 * @returns {string|null}
 */
function getCurrentRole() {
  return localStorage.getItem("userRole");
}

/**
 * Retrieve session token from localStorage.
 * @returns {string|null}
 */
function getToken() {
  return localStorage.getItem("token");
}

/**
 * Determines if a role requires a token to be considered valid.
 * @param {string|null} role
 * @returns {boolean}
 */
function roleRequiresToken(role) {
  return role === "admin" || role === "doctor" || role === "loggedPatient";
}

/**
 * Main entry: render the header once (idempotent; will replace existing header content each call).
 */
export function renderHeader() {
  ensureHeaderMountPoint();

  const headerDiv = document.getElementById("header");
  if (!headerDiv) {
    console.error("Failed to create or locate #header container.");
    return;
  }

  // If user is at root ("/" or ends with "/index.html" depending on hosting) reset role (as per original description)
  if (isRootLandingPage()) {
    localStorage.removeItem("userRole");
  }

  const role = getCurrentRole();
  const token = getToken();

  // Handle expired or invalid sessions (role present but missing token)
  if (roleRequiresToken(role) && !token) {
    localStorage.removeItem("userRole");
    localStorage.removeItem("token");
    alert("Session expired or invalid login. Please log in again.");
    redirectTo("/");
    return;
  }

  const headerEl = buildHeaderElement(role);
  headerDiv.innerHTML = "";
  headerDiv.appendChild(headerEl);

  attachHeaderButtonListeners(role);
}

/**
 * Programmatically build the header DOM for maintainability & safety.
 * @param {string|null} role
 * @returns {HTMLElement}
 */
function buildHeaderElement(role) {
  const header = document.createElement("header");
  header.className = "header";

  // Logo Section
  const logoSection = document.createElement("div");
  logoSection.className = "logo-section";

  const logoImg = document.createElement("img");
  logoImg.src = "../assets/images/logo/logo.png"; // Adjust path if needed.
  logoImg.alt = "Hospital CMS Logo";
  logoImg.className = "logo-img";

  const logoTitle = document.createElement("span");
  logoTitle.className = "logo-title";
  logoTitle.textContent = "Hospital CMS";

  logoSection.appendChild(logoImg);
  logoSection.appendChild(logoTitle);

  // Navigation
  const nav = document.createElement("nav");
  nav.setAttribute("aria-label", "Main Navigation");

  // Build nav items depending on role
  const fragment = document.createDocumentFragment();

  if (role === "admin") {
    fragment.appendChild(makeButton("Add Doctor", {
      id: "addDocBtn",
      className: "adminBtn",
      onClick: () => {
        const om = getOpenModalFn();
        if (om) om("addDoctor");
        else console.error("openModal function not found for Add Doctor.");
      }
    }));
    fragment.appendChild(makeAnchor("Logout", { onClick: logout }));
  } else if (role === "doctor") {
    fragment.appendChild(makeButton("Home", {
      className: "adminBtn",
      onClick: () => selectRole("doctor")
    }));
    fragment.appendChild(makeAnchor("Logout", { onClick: logout }));
  } else if (role === "loggedPatient") {
    fragment.appendChild(makeButton("Home", {
      id: "home",
      className: "adminBtn",
      onClick: () => redirectTo("/pages/loggedPatientDashboard.html")
    }));
    fragment.appendChild(makeButton("Appointments", {
      id: "patientAppointments",
      className: "adminBtn",
      onClick: () => redirectTo("/pages/patientAppointments.html")
    }));
    fragment.appendChild(makeAnchor("Logout", { onClick: logoutPatient }));
  } else {
    // Unauthenticated / pre-selection (role === null or "patient")
    fragment.appendChild(makeButton("Patient Login", {
      id: "patientLogin",
      className: "adminBtn",
      onClick: () => {
        const om = getOpenModalFn();
        if (om) om("patientLogin");
        else console.error("openModal function not found for Patient Login.");
      }
    }));
    fragment.appendChild(makeButton("Patient Sign Up", {
      id: "patientSignup",
      className: "adminBtn",
      onClick: () => {
        const om = getOpenModalFn();
        if (om) om("patientSignup");
        else console.error("openModal function not found for Patient Signup.");
      }
    }));
    // Provide doctor & admin login entry points
    fragment.appendChild(makeButton("Doctor Login", {
      id: "doctorLogin",
      className: "adminBtn",
      onClick: () => {
        const om = getOpenModalFn();
        if (om) om("doctorLogin");
        else console.error("openModal function not found for Doctor Login.");
      }
    }));
    fragment.appendChild(makeButton("Admin Login", {
      id: "adminLogin",
      className: "adminBtn",
      onClick: () => {
        const om = getOpenModalFn();
        if (om) om("adminLogin");
        else console.error("openModal function not found for Admin Login.");
      }
    }));
  }

  nav.appendChild(fragment);

  header.appendChild(logoSection);
  header.appendChild(nav);

  return header;
}

/**
 * Attach listeners which depend on dynamic elements post-render
 * (Currently most clicks are attached inline in creation; this is a placeholder
 * if you decide to move to event delegation or have dynamic toggles.)
 * @param {string|null} role
 */
function attachHeaderButtonListeners(role) {
  // Example: If you want to add extra logic for some role post-render:
  // if (role === "admin") { ... }
}

/**
 * Utility: Create a button element.
 * @param {string} text
 * @param {Object} opts
 * @returns {HTMLButtonElement}
 */
function makeButton(text, opts = {}) {
  const btn = document.createElement("button");
  btn.type = "button";
  btn.textContent = text;
  if (opts.id) btn.id = opts.id;
  if (opts.className) btn.className = opts.className;
  if (typeof opts.onClick === "function") {
    btn.addEventListener("click", opts.onClick);
  }
  return btn;
}

/**
 * Utility: Create an anchor acting like a button (no navigation).
 * @param {string} text
 * @param {Object} opts
 * @returns {HTMLAnchorElement}
 */
function makeAnchor(text, opts = {}) {
  const a = document.createElement("a");
  a.href = "#";
  a.textContent = text;
  a.role = "button";
  if (opts.id) a.id = opts.id;
  if (opts.className) a.className = opts.className;
  if (typeof opts.onClick === "function") {
    a.addEventListener("click", (e) => {
      e.preventDefault();
      opts.onClick(e);
    });
  }
  return a;
}

/**
 * Determine if current page is considered "root" for session reset.
 * Adjust logic based on deployment (e.g., maybe just check pathname === '/' ).
 */
function isRootLandingPage() {
  const path = window.location.pathname;
  return path === "/" || path.endsWith("/index.html");
}

/**
 * Redirect helper.
 * @param {string} to
 */
function redirectTo(to) {
  window.location.href = to;
}

/**
 * Ensure a #header mount point exists (create if needed).
 */
function ensureHeaderMountPoint() {
  if (!document.getElementById("header")) {
    const div = document.createElement("div");
    div.id = "header";
    // Insert at top of body
    document.body.insertBefore(div, document.body.firstChild);
  }
}

/**
 * Generic logout (admin / doctor or any token-based role).
 * Clears session and navigates to root.
 */
export function logout() {
  localStorage.removeItem("userRole");
  localStorage.removeItem("token");
  redirectTo("/");
}

/**
 * Patient-specific logout.
 * If you want different behavior (e.g., redirect to splash page) adjust as needed.
 */
export function logoutPatient() {
  localStorage.removeItem("token");
  localStorage.removeItem("userRole");
  redirectTo("/");
}

/**
 * Example placeholder for selecting a role (e.g., "doctor").
 * Adjust to your real role selection logic or remove if not needed.
 * @param {string} role
 */
export function selectRole(role) {
  localStorage.setItem("userRole", role);
  renderHeader();
}

/**
 * OPTIONAL: Expose functions on window for non-module pages (backward compatibility).
 * Remove if using strict ES module imports everywhere.
 */
(function exposeGlobalsIfNeeded() {
  if (typeof window !== "undefined") {
    window.renderHeader = window.renderHeader || renderHeader;
    window.logout = window.logout || logout;
    window.logoutPatient = window.logoutPatient || logoutPatient;
    window.selectRole = window.selectRole || selectRole;
  }
})();

/**
 * Auto-render on DOMContentLoaded (can be removed if you prefer manual control).
 */
if (document.readyState === "loading") {
  document.addEventListener("DOMContentLoaded", renderHeader);
} else {
  // Document already loaded
  renderHeader();
}