/**
 * Modal Manager: Dynamically renders different modal content blocks.
 *
 * Expected DOM structure in the page:
 * <div id="modal" class="modal">
 *   <div class="modal-overlay" data-close="true"></div>
 *   <div class="modal-dialog" role="dialog" aria-modal="true" aria-labelledby="modalTitle">
 *     <button id="closeModal" class="modal-close" aria-label="Close">&times;</button>
 *     <div id="modal-body"></div>
 *   </div>
 * </div>
 *
 * External handler functions (if globals):
 * - signupPatient()
 * - loginPatient()
 * - adminAddDoctor()
 * - adminLoginHandler()
 * - doctorLoginHandler()
 *
 * You can also inject handlers using setModalHandlers().
 */

const modalTemplates = {
  addDoctor: () => `
    <h2 id="modalTitle">Add Doctor</h2>
    <input type="text" id="doctorName" placeholder="Doctor Name" class="input-field">
    <select id="specialization" class="input-field select-dropdown">
      <option value="">Specialization</option>
      <option value="cardiologist">Cardiologist</option>
      <option value="dermatologist">Dermatologist</option>
      <option value="neurologist">Neurologist</option>
      <option value="pediatrician">Pediatrician</option>
      <option value="orthopedic">Orthopedic</option>
      <option value="gynecologist">Gynecologist</option>
      <option value="psychiatrist">Psychiatrist</option>
      <option value="dentist">Dentist</option>
      <option value="ophthalmologist">Ophthalmologist</option>
      <option value="ent">ENT Specialist</option>
      <option value="urologist">Urologist</option>
      <option value="oncologist">Oncologist</option>
      <option value="gastroenterologist">Gastroenterologist</option>
      <option value="general">General Physician</option>
    </select>
    <input type="email" id="doctorEmail" placeholder="Email" class="input-field">
    <input type="password" id="doctorPassword" placeholder="Password" class="input-field">
    <input type="text" id="doctorPhone" placeholder="Mobile No." class="input-field">
    <div class="availability-container">
      <label class="availabilityLabel">Select Availability:</label>
      <div class="checkbox-group">
        <label><input type="checkbox" name="availability" value="09:00-10:00"> 9:00 AM - 10:00 AM</label>
        <label><input type="checkbox" name="availability" value="10:00-11:00"> 10:00 AM - 11:00 AM</label>
        <label><input type="checkbox" name="availability" value="11:00-12:00"> 11:00 AM - 12:00 PM</label>
        <label><input type="checkbox" name="availability" value="12:00-13:00"> 12:00 PM - 1:00 PM</label>
      </div>
    </div>
    <button class="dashboard-btn" id="saveDoctorBtn">Save</button>
  `,
  patientLogin: () => `
    <h2 id="modalTitle">Patient Login</h2>
    <input type="text" id="email" placeholder="Email" class="input-field" autocomplete="username">
    <input type="password" id="password" placeholder="Password" class="input-field" autocomplete="current-password">
    <button class="dashboard-btn" id="loginBtn">Login</button>
  `,
  patientSignup: () => `
    <h2 id="modalTitle">Patient Signup</h2>
    <input type="text" id="name" placeholder="Name" class="input-field" autocomplete="name">
    <input type="email" id="email" placeholder="Email" class="input-field" autocomplete="email">
    <input type="password" id="password" placeholder="Password" class="input-field" autocomplete="new-password">
    <input type="text" id="phone" placeholder="Phone" class="input-field" autocomplete="tel">
    <input type="text" id="address" placeholder="Address" class="input-field" autocomplete="street-address">
    <button class="dashboard-btn" id="signupBtn">Signup</button>
  `,
  adminLogin: () => `
    <h2 id="modalTitle">Admin Login</h2>
    <input type="text" id="username" name="username" placeholder="Username" class="input-field" autocomplete="username">
    <input type="password" id="password" name="password" placeholder="Password" class="input-field" autocomplete="current-password">
    <button class="dashboard-btn" id="adminLoginBtn">Login</button>
  `,
  doctorLogin: () => `
    <h2 id="modalTitle">Doctor Login</h2>
    <input type="text" id="email" placeholder="Email" class="input-field" autocomplete="username">
    <input type="password" id="password" placeholder="Password" class="input-field" autocomplete="current-password">
    <button class="dashboard-btn" id="doctorLoginBtn">Login</button>
  `
};

/**
 * Holds optional injected handlers (dependency injection).
 */
const injectedHandlers = {
  signupPatient: null,
  loginPatient: null,
  adminAddDoctor: null,
  adminLoginHandler: null,
  doctorLoginHandler: null
};

/**
 * Inject custom handler functions if you want to avoid relying on global scope.
 * @param {Object} handlers
 */
export function setModalHandlers(handlers = {}) {
  Object.keys(injectedHandlers).forEach((k) => {
    if (typeof handlers[k] === "function") {
      injectedHandlers[k] = handlers[k];
    }
  });
}

/**
 * Open a modal of a given type and attach relevant event listeners.
 * @param {string} type - One of the keys defined in modalTemplates.
 */
export function openModal(type) {
  if (!modalTemplates[type]) {
    console.warn(`openModal: Unknown modal type '${type}'.`);
    return;
  }

  const modal = document.getElementById("modal");
  const modalBody = document.getElementById("modal-body");
  const closeBtn = document.getElementById("closeModal");

  if (!modal || !modalBody || !closeBtn) {
    console.error("Modal structure not found in DOM. Ensure required elements exist.");
    return;
  }

  modalBody.innerHTML = modalTemplates[type]();

  // Show modal
  modal.style.display = "block";
  modal.setAttribute("data-open", "true");

  // Focus first input (if any)
  const firstInput = modalBody.querySelector("input, button, select, textarea");
  if (firstInput) {
    setTimeout(() => firstInput.focus(), 0);
  }

  // Close handlers
  const closeModal = () => {
    modal.style.display = "none";
    modal.removeAttribute("data-open");
    // Basic focus return (optional: store previously focused element before open)
    document.activeElement?.blur();
  };

  closeBtn.onclick = closeModal;

  // Overlay click (if overlay exists with data-close attribute)
  modal.querySelectorAll("[data-close='true']").forEach((el) =>
    el.addEventListener("click", (e) => {
      if (e.target === el) closeModal();
    })
  );

  // ESC key close
  const escListener = (e) => {
    if (e.key === "Escape") {
      closeModal();
      document.removeEventListener("keydown", escListener);
    }
  };
  document.addEventListener("keydown", escListener);

  // Attach action button listeners
  attachActionListeners(type, closeModal);
}

/**
 * Internal: attaches listeners for specific modal types.
 * @param {string} type
 * @param {Function} closeModal
 */
function attachActionListeners(type, closeModal) {
  switch (type) {
    case "patientSignup": {
      const btn = document.getElementById("signupBtn");
      if (btn) {
        btn.addEventListener("click", () => {
          const handler = injectedHandlers.signupPatient || window.signupPatient;
          if (typeof handler === "function") {
            handler();
          } else {
            console.error("signupPatient handler not found.");
          }
        });
      }
      break;
    }
    case "patientLogin": {
      const btn = document.getElementById("loginBtn");
      if (btn) {
        btn.addEventListener("click", () => {
          const handler = injectedHandlers.loginPatient || window.loginPatient;
            if (typeof handler === "function") {
              handler();
            } else {
              console.error("loginPatient handler not found.");
            }
        });
      }
      break;
    }
    case "addDoctor": {
      const btn = document.getElementById("saveDoctorBtn");
      if (btn) {
        btn.addEventListener("click", () => {
          const handler = injectedHandlers.adminAddDoctor || window.adminAddDoctor;
          if (typeof handler === "function") {
            handler();
          } else {
            console.error("adminAddDoctor handler not found.");
          }
        });
      }
      break;
    }
    case "adminLogin": {
      const btn = document.getElementById("adminLoginBtn");
      if (btn) {
        btn.addEventListener("click", () => {
          const handler = injectedHandlers.adminLoginHandler || window.adminLoginHandler;
          if (typeof handler === "function") {
            handler();
          } else {
            console.error("adminLoginHandler not found.");
          }
        });
      }
      break;
    }
    case "doctorLogin": {
      const btn = document.getElementById("doctorLoginBtn");
      if (btn) {
        btn.addEventListener("click", () => {
          const handler = injectedHandlers.doctorLoginHandler || window.doctorLoginHandler;
          if (typeof handler === "function") {
            handler();
          } else {
            console.error("doctorLoginHandler not found.");
          }
        });
      }
      break;
    }
    default:
      // No specific actions needed
      break;
  }
}

/**
 * Optional utility to programmatically close the modal from outside.
 */
export function closeModal() {
  const modal = document.getElementById("modal");
  if (modal) {
    modal.style.display = "none";
    modal.removeAttribute("data-open");
  }
}