/**
 * Admin Dashboard Logic
 *
 * Features:
 *  - Load & render all doctors as cards.
 *  - Filter doctors by (name, time slot, specialization).
 *  - Add a new doctor via modal form (openModal('addDoctor')).
 *
 * Dependencies (ensure these scripts / modules are loaded before this file OR use proper imports):
 *  - services/doctorServices.js   (getDoctors, filterDoctors, saveDoctor)
 *  - components/doctorCard.js     (createDoctorCard)
 *  - components/modals.js         (openModal, optional closeModal)
 *
 * Expected DOM Elements (IDs / selectors):
 *  - #addDocBtn                (button to open Add Doctor modal; header may already provide this)
 *  - #doctorCardsContainer     (container where doctor cards are appended)
 *  - #searchDoctor             (text input for name search)
 *  - #filterTime               (select or input for time slot filter)
 *  - #filterSpecialization     (select for specialization)
 *
 * Modal Form Fields (inside Add Doctor modal):
 *  - #doctorName
 *  - #doctorEmail
 *  - #doctorPhone
 *  - #doctorPassword
 *  - #specialization
 *  - Checkboxes: name="availability" (time slots)
 *
 * LocalStorage:
 *  - token (admin auth token)
 *  - userRole ('admin' required for add/delete)
 */

import {
  getDoctors,
  filterDoctors,
  saveDoctor
} from "../services/doctorServices.js";
import { createDoctorCard } from "../components/doctorCard.js";

// If you use ESM import for openModal / closeModal uncomment:
// import { openModal, closeModal } from "../components/modals.js";

function getOpenModalFn() {
  if (typeof openModal === "function") return openModal;
  if (window.openModal) return window.openModal;
  return null;
}
function getCloseModalFn() {
  if (typeof closeModal === "function") return closeModal;
  if (window.closeModal) return window.closeModal;
  return () => {
    const modal = document.getElementById("modal");
    if (modal) modal.style.display = "none";
  };
}

/* -------------------- State & Selectors -------------------- */
const selectors = {
  cardsContainer: "#doctorCardsContainer",
  searchInput: "#searchDoctor",
  timeFilter: "#filterTime",
  specializationFilter: "#filterSpecialization",
  addDoctorBtn: "#addDocBtn",
  emptyStateClass: "empty-state"
};

/**
 * Utility to get container or create a fallback.
 * @returns {HTMLElement}
 */
function getCardsContainer() {
  let el = document.querySelector(selectors.cardsContainer);
  if (!el) {
    el = document.createElement("div");
    el.id = selectors.cardsContainer.replace(/^#/, "");
    document.body.appendChild(el);
  }
  return el;
}

/* -------------------- Core Rendering Functions -------------------- */

/**
 * Fetch all doctors and render them.
 */
export async function loadDoctorCards() {
  const container = getCardsContainer();
  showLoading(container);

  try {
    const { doctors } = await getDoctors();
    renderDoctorCards(doctors || []);
  } catch (err) {
    console.error("loadDoctorCards error:", err);
    container.innerHTML =
      '<p class="error-msg">Failed to load doctors. Please refresh.</p>';
  }
}

/**
 * Render a supplied list of doctors as cards.
 * @param {Array} doctors
 */
export function renderDoctorCards(doctors) {
  const container = getCardsContainer();
  container.innerHTML = "";

  if (!Array.isArray(doctors) || doctors.length === 0) {
    const empty = document.createElement("p");
    empty.className = selectors.emptyStateClass;
    empty.textContent = "No doctors available.";
    container.appendChild(empty);
    return;
  }

  const fragment = document.createDocumentFragment();
  doctors.forEach((doc) => {
    const card = createDoctorCard(doc, {
      onDelete: (deletedId) => {
        console.log("Doctor deleted:", deletedId);
        // Optionally refetch or just remove card (already removed by component)
        if (!container.querySelector(".doctor-card")) {
          renderDoctorCards([]);
        }
      }
    });
    fragment.appendChild(card);
  });
  container.appendChild(fragment);
}

/**
 * Display a temporary loading state.
 * @param {HTMLElement} container
 */
function showLoading(container) {
  container.innerHTML = '<p class="loading">Loading doctors...</p>';
}

/* -------------------- Filtering Logic -------------------- */

/**
 * Called on any filter input change to fetch & display filtered doctors.
 */
export async function filterDoctorsOnChange() {
  const nameInput = document.querySelector(selectors.searchInput);
  const timeInput = document.querySelector(selectors.timeFilter);
  const specInput = document.querySelector(selectors.specializationFilter);

  const name = nameInput?.value.trim() || "";
  const time = timeInput?.value.trim() || "";
  const specialization = specInput?.value.trim() || "";

  const container = getCardsContainer();
  showLoading(container);

  try {
    const { doctors } = await filterDoctors({
      name,
      time,
      specialization
    });

    if (!doctors || doctors.length === 0) {
      container.innerHTML =
        '<p class="empty-state">No doctors found with the given filters.</p>';
      return;
    }

    renderDoctorCards(doctors);
  } catch (err) {
    console.error("filterDoctorsOnChange error:", err);
    alert("Failed to filter doctors. Please try again later.");
    loadDoctorCards(); // fallback to full list
  }
}

/* -------------------- Add Doctor (Admin) -------------------- */

/**
 * Collect form data from Add Doctor modal and submit to API.
 * Exposed globally so modals.js can invoke via save button.
 */
export async function adminAddDoctor() {
  const token = localStorage.getItem("token");
  const role = localStorage.getItem("userRole");

  if (role !== "admin") {
    alert("Only admins can add doctors.");
    return;
  }
  if (!token) {
    alert("Admin session expired. Please log in again.");
    return;
  }

  const nameEl = document.getElementById("doctorName");
  const emailEl = document.getElementById("doctorEmail");
  const phoneEl = document.getElementById("doctorPhone");
  const passEl = document.getElementById("doctorPassword");
  const specEl = document.getElementById("specialization");

  const availabilityEls = document.querySelectorAll(
    "input[name='availability']:checked"
  );

  const name = nameEl?.value.trim();
  const email = emailEl?.value.trim();
  const phone = phoneEl?.value.trim();
  const password = passEl?.value || "";
  const specialization = specEl?.value || "";
  const availability = Array.from(availabilityEls).map((cb) => cb.value);

  // Simple validations
  const errors = [];
  if (!name) errors.push("Doctor name is required.");
  if (!specialization) errors.push("Specialization is required.");
  if (!email) errors.push("Email is required.");
  if (!password) errors.push("Password is required.");

  if (errors.length > 0) {
    alert(errors.join("\n"));
    return;
  }

  const doctorPayload = {
    name,
    specialization,
    email,
    phone,
    password,
    availability
  };

  // Disable save button while processing
  const saveBtn = document.getElementById("saveDoctorBtn");
  if (saveBtn) {
    saveBtn.disabled = true;
    saveBtn.textContent = "Saving...";
  }

  try {
    const resp = await saveDoctor(doctorPayload, token);
    if (resp.success) {
      alert(resp.message || "Doctor added successfully.");
      // Close modal and refresh list
      getCloseModalFn()();
      await loadDoctorCards();
    } else {
      alert(resp.message || "Failed to add doctor.");
    }
  } catch (err) {
    console.error("adminAddDoctor error:", err);
    alert("Unexpected error adding doctor.");
  } finally {
    if (saveBtn) {
      saveBtn.disabled = false;
      saveBtn.textContent = "Save";
    }
  }
}

/* -------------------- Event Wiring -------------------- */

/**
 * Attach filtering event listeners (idempotent).
 */
function attachFilterListeners() {
  const nameInput = document.querySelector(selectors.searchInput);
  const timeInput = document.querySelector(selectors.timeFilter);
  const specInput = document.querySelector(selectors.specializationFilter);

  if (nameInput) {
    nameInput.addEventListener("input", debounce(filterDoctorsOnChange, 300));
  }
  if (timeInput) {
    timeInput.addEventListener("change", filterDoctorsOnChange);
  }
  if (specInput) {
    specInput.addEventListener("change", filterDoctorsOnChange);
  }
}

/**
 * Debounce helper to limit frequent calls.
 */
function debounce(fn, wait = 250) {
  let t;
  return (...args) => {
    clearTimeout(t);
    t = setTimeout(() => fn.apply(null, args), wait);
  };
}

/**
 * Setup Add Doctor button listener (if present outside header).
 */
function attachAddDoctorListener() {
  const addBtn = document.querySelector(selectors.addDoctorBtn);
  const om = getOpenModalFn();
  if (addBtn && om) {
    addBtn.addEventListener("click", () => om("addDoctor"));
  }
}

/* -------------------- Initialization -------------------- */

function initAdminDashboard() {
  const role = localStorage.getItem("userRole");
  if (role !== "admin") {
    console.warn("Admin Dashboard initialized without admin role.");
  }
  attachAddDoctorListener();
  attachFilterListeners();
  loadDoctorCards();
}

// Initialize after DOM is ready
if (document.readyState === "loading") {
  document.addEventListener("DOMContentLoaded", initAdminDashboard);
} else {
  initAdminDashboard();
}

/* -------------------- Global Exposure (for modal integration) -------------------- */
if (typeof window !== "undefined") {
  window.adminAddDoctor = adminAddDoctor;
  window.loadDoctorCards = loadDoctorCards; // Optional exposure
}