/**
 * Appointments Page Logic
 *
 * Features:
 *  - Load appointments for a selected date (default: today).
 *  - Filter by patient name via search bar (live as user types).
 *  - Quick "Today" button to jump back to current date.
 *
 * Assumptions / External Dependencies:
 *  - getAllAppointments(date, patientNameOrNullLiteral, token) -> Promise<{ appointments: Array }>
 *          or returns an array directly. If patientName is empty, backend expects literal "null".
 *  - createPatientRow(appointment, patientObj) -> <tr> element representing one appointment.
 *  - renderContent() sets up any surrounding layout (optional; called on DOMContentLoaded if present).
 *
 * Expected DOM Elements (IDs):
 *  - #appointmentsTbody         (tbody where rows are appended)
 *  - #patientSearch             (text input for patient name filtering)
 *  - #todayBtn                  (button to reset date to today)
 *  - #appointmentDate           (input[type="date"] for selecting target date)
 *
 * LocalStorage:
 *  - token (auth token used when fetching appointments)
 *
 * Global Exposure:
 *  - loadAppointments() available on window for manual refresh if needed.
 */

import { getAllAppointments } from "../services/appointmentServices.js"; // Adjust path if needed
import { createPatientRow } from "../components/patientRow.js";          // Adjust path if needed

/* -------------------- State -------------------- */
let selectedDate = getTodayISO();
let patientName = "null"; // Literal string "null" when no filter
let token = null;

/* -------------------- DOM Helpers -------------------- */
const qs = (sel) => document.querySelector(sel);

/**
 * Get (or lazily find) the table body.
 * @returns {HTMLElement}
 */
function getTableBody() {
  let tbody = qs("#appointmentsTbody");
  if (!tbody) {
    // Fallback creation (optional)
    const table = document.createElement("table");
    table.className = "appointments-table";
    tbody = document.createElement("tbody");
    tbody.id = "appointmentsTbody";
    table.appendChild(tbody);
    document.body.appendChild(table);
  }
  return tbody;
}

/**
 * Create a <tr> with a single message cell spanning all columns.
 * @param {string} message
 * @param {number} [colSpan=6]
 * @param {string} [className]
 */
function makeMessageRow(message, colSpan = 6, className = "message-row") {
  const tr = document.createElement("tr");
  const td = document.createElement("td");
  td.colSpan = colSpan;
  td.textContent = message;
  td.className = className;
  tr.appendChild(td);
  return tr;
}

/* -------------------- Core Logic -------------------- */

/**
 * Fetch and render appointments for current state (selectedDate, patientName).
 * Exposed globally.
 */
export async function loadAppointments() {
  const tbody = getTableBody();
  tbody.innerHTML = "";
  tbody.appendChild(makeMessageRow("Loading appointments...", 6, "loading-row"));

  try {
    const response = await getAllAppointments(
      selectedDate,
      patientName,
      token
    );

    // Normalize response structure
    const appointments = Array.isArray(response)
      ? response
      : response?.appointments || [];

    tbody.innerHTML = "";

    if (!appointments.length) {
      tbody.appendChild(
        makeMessageRow(
          patientName !== "null"
            ? "No appointments match the selected filters."
            : "No appointments found for today.",
          6,
          "empty-row"
        )
      );
      return;
    }

    appointments.forEach((appt) => {
      // Prepare 'patient' object (based on description)
      const patientObj = {
        id: appt.patientId ?? appt.patient?.id ?? appt.id,
        name: appt.patientName ?? appt.patient?.name ?? "Unknown",
        phone: appt.patientPhone ?? appt.patient?.phone ?? "N/A",
        email: appt.patientEmail ?? appt.patient?.email ?? "N/A"
      };

      try {
        const row = createPatientRow(appt, patientObj);
        tbody.appendChild(row);
      } catch (rowErr) {
        console.error("Error creating patient row:", rowErr, appt);
      }
    });
  } catch (err) {
    console.error("loadAppointments error:", err);
    tbody.innerHTML = "";
    tbody.appendChild(
      makeMessageRow(
        "Error loading appointments. Try again later.",
        6,
        "error-row"
      )
    );
  }
}

/* -------------------- Event Wiring -------------------- */

/**
 * Attach listeners to search input, today button, and date picker.
 */
function attachEventListeners() {
  const searchInput = qs("#patientSearch");
  const todayBtn = qs("#todayBtn");
  const datePicker = qs("#appointmentDate");

  if (datePicker) {
    datePicker.value = selectedDate;
    datePicker.addEventListener("change", () => {
      if (datePicker.value) {
        selectedDate = datePicker.value;
        loadAppointments();
      }
    });
  }

  if (todayBtn) {
    todayBtn.addEventListener("click", () => {
      selectedDate = getTodayISO();
      if (datePicker) datePicker.value = selectedDate;
      loadAppointments();
    });
  }

  if (searchInput) {
    searchInput.addEventListener(
      "input",
      debounce(() => {
        const val = searchInput.value.trim();
        patientName = val ? val : "null";
        loadAppointments();
      }, 300)
    );
  }
}

/* -------------------- Utilities -------------------- */

/**
 * Return today's date formatted as YYYY-MM-DD in local time.
 */
function getTodayISO() {
  const now = new Date();
  const offset = now.getTimezoneOffset();
  // Adjust to remove timezone offset impact on ISO substring
  const local = new Date(now.getTime() - offset * 60000);
  return local.toISOString().slice(0, 10);
}

/**
 * Debounce helper.
 * @param {Function} fn
 * @param {number} wait
 */
function debounce(fn, wait = 250) {
  let t;
  return (...args) => {
    clearTimeout(t);
    t = setTimeout(() => fn.apply(null, args), wait);
  };
}

/* -------------------- Initialization -------------------- */

function initAppointmentsPage() {
  token = localStorage.getItem("token");
  attachEventListeners();
  loadAppointments();
}

document.addEventListener("DOMContentLoaded", () => {
  // Optional layout prep
  if (typeof renderContent === "function") {
    try {
      renderContent();
    } catch (e) {
      console.warn("renderContent() failed or not defined:", e);
    }
  }
  initAppointmentsPage();
});

/* -------------------- Global Exposure -------------------- */
if (typeof window !== "undefined") {
  window.loadAppointments = loadAppointments;
}