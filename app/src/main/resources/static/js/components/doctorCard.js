/**
 * Doctor Card Component
 *
 * Renders a single doctor card with role-based actions:
 *  - Admin: can delete doctor
 *  - Patient (not logged in): prompted to log in before booking
 *  - Logged-in patient: can open booking overlay
 *
 * Dependencies (expected exports):
 *  - showBookingOverlay(doctor, patient) from ../pages/loggedPatient.js (or similar)
 *  - deleteDoctor(doctorId, token) from ../services/doctorServices.js
 *  - getPatientDetails(token) from ../services/patientServices.js
 *
 * Adjust import paths below to match actual project structure.
 */

// Try ES module imports; if the environment still uses classic scripts these can be commented out
// and the global functions will be used instead.
let showBookingOverlayFn;
let deleteDoctorFn;
let getPatientDetailsFn;

try {
  // Dynamic imports so failure won't crash if paths differ during early integration.
  // Update the paths if your actual structure is different.
  // IMPORTANT: For static bundlers, replace with static import statements.
  // Example static:
  // import { showBookingOverlay } from "../pages/loggedPatient.js";
  // import { deleteDoctor } from "../services/doctorServices.js";
  // import { getPatientDetails } from "../services/patientServices.js";
  // eslint-disable-next-line no-undef
  if (typeof import !== "undefined") {
    // (Left as placeholder; dynamic import removed for compatibility in many static setups)
  }
} catch (_) {
  /* ignore */
}

// Fallback to globals if modules not found
function resolveExternalFns() {
  if (!showBookingOverlayFn) {
    showBookingOverlayFn =
      typeof showBookingOverlay === "function"
        ? showBookingOverlay
        : (doctor, patient) => {
            console.warn(
              "showBookingOverlay function not found. Provide implementation.",
              { doctor, patient }
            );
            alert("Booking overlay not available yet.");
          };
  }
  if (!deleteDoctorFn) {
    deleteDoctorFn =
      typeof deleteDoctor === "function"
        ? deleteDoctor
        : async (id) => {
            console.warn("deleteDoctor API function missing. Id:", id);
            return { success: false, message: "deleteDoctor not implemented" };
          };
  }
  if (!getPatientDetailsFn) {
    getPatientDetailsFn =
      typeof getPatientDetails === "function"
        ? getPatientDetails
        : async () => {
            console.warn("getPatientDetails function missing.");
            return null;
          };
  }
}

resolveExternalFns();

/**
 * Normalize availability into an array of time slot strings.
 * @param {any} availability
 * @returns {string[]}
 */
function normalizeAvailability(availability) {
  if (!availability) return [];
  if (Array.isArray(availability)) return availability;
  if (typeof availability === "string") {
    // Assume comma or semicolon separated
    return availability
      .split(/[,;]+/)
      .map((s) => s.trim())
      .filter(Boolean);
  }
  return [];
}

/**
 * Create an element with classes and optional text.
 * @param {string} tag
 * @param {string|string[]} [classNames]
 * @param {string} [text]
 * @returns {HTMLElement}
 */
function el(tag, classNames, text) {
  const node = document.createElement(tag);
  if (classNames) {
    if (Array.isArray(classNames)) node.classList.add(...classNames);
    else node.classList.add(classNames);
  }
  if (text) node.textContent = text;
  return node;
}

/**
 * Main factory to build a doctor card.
 * @param {Object} doctor - Doctor data object.
 * @param {string|number} doctor.id
 * @param {string} doctor.name
 * @param {string} doctor.specialization
 * @param {string} doctor.email
 * @param {string[]|string} [doctor.availability] - Array or comma-separated string of time slots.
 * @param {Object} [options]
 * @param {HTMLElement} [options.container] - If provided, the card is appended to this container.
 * @param {Function} [options.onDelete] - Callback after successful deletion: (doctorId) => void
 * @param {Function} [options.onBook] - Callback before booking overlay: (doctor, patient) => boolean|void (return false to cancel default)
 * @returns {HTMLElement}
 */
export function createDoctorCard(doctor, options = {}) {
  if (!doctor || typeof doctor !== "object") {
    throw new Error("createDoctorCard: doctor must be an object.");
  }
  const {
    id,
    name = "Unknown Doctor",
    specialization = "General",
    email = "N/A",
    availability
  } = doctor;

  const { container, onDelete, onBook } = options;

  const role = localStorage.getItem("userRole"); // admin | doctor | loggedPatient | patient | null
  const token = localStorage.getItem("token");

  const card = el("div", "doctor-card");
  card.dataset.id = id;

  // Info Section
  const info = el("div", "doctor-info");

  const nameEl = el("h3", "doctor-name", name);
  const specEl = el("p", "doctor-spec");
  specEl.innerHTML = `<strong>Specialization:</strong> ${escapeHtml(specialization)}`;

  const emailEl = el("p", "doctor-email");
  emailEl.innerHTML = `<strong>Email:</strong> ${escapeHtml(email)}`;

  const availabilityList = el("ul", "availability-list");
  const availabilityTitle = el("p", "availability-title", "Available Slots:");
  const slots = normalizeAvailability(availability);
  if (slots.length === 0) {
    const li = el("li", "no-availability", "No availability listed");
    availabilityList.appendChild(li);
  } else {
    slots.forEach((slot) => {
      const li = el("li", "availability-slot", slot);
      availabilityList.appendChild(li);
    });
  }

  info.appendChild(nameEl);
  info.appendChild(specEl);
  info.appendChild(emailEl);
  info.appendChild(availabilityTitle);
  info.appendChild(availabilityList);

  // Actions Section
  const actions = el("div", "doctor-actions");

  if (role === "admin") {
    // Delete button
    const delBtn = el("button", ["btn", "btn-danger"], "Delete");
    delBtn.addEventListener("click", async () => {
      if (!confirm(`Delete Dr. ${name}? This action cannot be undone.`)) return;
      const adminToken = token;
      if (!adminToken) {
        alert("Admin token missing. Please log in again.");
        return;
      }
      delBtn.disabled = true;
      delBtn.textContent = "Deleting...";
      try {
        const resp = await safeCallDeleteDoctor(id, adminToken);
        if (resp.success) {
          alert("Doctor deleted successfully.");
          card.remove();
          if (typeof onDelete === "function") onDelete(id);
        } else {
          alert(resp.message || "Failed to delete doctor.");
        }
      } catch (err) {
        console.error(err);
        alert("Unexpected error deleting doctor.");
      } finally {
        // If still in DOM (not removed)
        if (card.isConnected) {
          delBtn.disabled = false;
          delBtn.textContent = "Delete";
        }
      }
    });
    actions.appendChild(delBtn);
  } else if (role === "loggedPatient") {
    const bookBtn = el("button", ["btn", "btn-primary"], "Book Now");
    bookBtn.addEventListener("click", async () => {
      if (!token) {
        alert("Session expired. Please log in again.");
        redirectTo("/"); // adjust path if needed
        return;
      }
      bookBtn.disabled = true;
      bookBtn.textContent = "Loading...";
      try {
        const patient = await getPatientDetailsFn(token);
        if (!patient) {
          alert("Could not load patient details. Please retry.");
          return;
        }
        if (typeof onBook === "function") {
          const proceed = onBook(doctor, patient);
          if (proceed === false) return; // Allow external cancellation
        }
        showBookingOverlayFn(doctor, patient);
      } catch (err) {
        console.error(err);
        alert("Error preparing booking. Please try again.");
      } finally {
        bookBtn.disabled = false;
        bookBtn.textContent = "Book Now";
      }
    });
    actions.appendChild(bookBtn);
  } else {
    // Not logged in patient or other roles (including null)
    const bookBtn = el("button", ["btn", "btn-secondary"], "Book Now");
    bookBtn.addEventListener("click", () => {
      alert("Please log in as a patient to book an appointment.");
      // Optionally open patient login modal automatically:
      const openModalFn =
        typeof openModal === "function"
          ? openModal
          : typeof window !== "undefined" && typeof window.openModal === "function"
          ? window.openModal
          : null;
      if (openModalFn) openModalFn("patientLogin");
    });
    actions.appendChild(bookBtn);
  }

  card.appendChild(info);
  card.appendChild(actions);

  if (container instanceof HTMLElement) {
    container.appendChild(card);
  }

  return card;
}

/**
 * Wrap deleteDoctor API with consistent response shape.
 * @param {string|number} doctorId
 * @param {string} token
 * @returns {Promise<{success:boolean,message?:string}>}
 */
async function safeCallDeleteDoctor(doctorId, token) {
  try {
    resolveExternalFns();
    const result = await deleteDoctorFn(doctorId, token);
    // Accept several return conventions
    if (result && typeof result === "object") {
      if ("success" in result) return result;
      // If API returns e.g. {status:'OK'} treat as success
      if (result.status === "OK" || result.status === 200) {
        return { success: true };
      }
    }
    // If function returned void assume success
    if (result === undefined) {
      return { success: true };
    }
    return { success: false, message: "Unknown deleteDoctor response format." };
  } catch (err) {
    console.error("safeCallDeleteDoctor error:", err);
    return { success: false, message: err.message || "Deletion failed." };
  }
}

/**
 * Simple redirect helper.
 * @param {string} to
 */
function redirectTo(to) {
  window.location.href = to;
}

/**
 * Basic HTML escaper for small inline insertions.
 * @param {string} str
 * @returns {string}
 */
function escapeHtml(str) {
  if (str == null) return "";
  return String(str)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#39;");
}

/**
 * Optional: expose globally if needed for non-module consumption.
 */
(function exposeGlobal() {
  if (typeof window !== "undefined") {
    if (!window.createDoctorCard) window.createDoctorCard = createDoctorCard;
  }
})();
