/**
 * Create a table row element representing an appointment.
 *
 * @param {Object} appointment - Appointment data object.
 * @param {string|number} appointment.id - Unique appointment identifier.
 * @param {string|number} [appointment.patientId] - Patient identifier (if separate from appointment.id).
 * @param {string} appointment.patientName - Patient display name.
 * @param {string} appointment.doctorName - Doctor display name.
 * @param {string} appointment.date - Date string (ISO or display formatted).
 * @param {string} appointment.time - Time string.
 * @param {Function} [onPrescriptionClick] - Optional callback when prescription icon clicked.
 *        Receives (appointment, event).
 *        If not provided, defaults to navigating to addPrescription.html?id=<patientId or appointmentId>.
 * @returns {HTMLTableRowElement}
 */
export function getAppointments(appointment, onPrescriptionClick) {
  if (!appointment || typeof appointment !== "object") {
    throw new Error("getAppointments: 'appointment' must be a non-null object.");
  }

  const {
    id,
    patientId,
    patientName = "",
    doctorName = "",
    date = "",
    time = ""
  } = appointment;

  const tr = document.createElement("tr");

  // Patient Name Cell
  const tdPatient = document.createElement("td");
  tdPatient.className = "patient-id";
  tdPatient.textContent = patientName;

  // Doctor Name Cell
  const tdDoctor = document.createElement("td");
  tdDoctor.textContent = doctorName;

  // Date Cell
  const tdDate = document.createElement("td");
  tdDate.textContent = date;

  // Time Cell
  const tdTime = document.createElement("td");
  tdTime.textContent = time;

  // Action Cell (Prescription)
  const tdAction = document.createElement("td");
  const img = document.createElement("img");
  img.src = "../assets/images/edit/edit.png"; // Adjust path if needed.
  img.alt = "Add / Edit Prescription";
  img.className = "prescription-btn";
  img.dataset.id = id;
  tdAction.appendChild(img);

  tr.appendChild(tdPatient);
  tr.appendChild(tdDoctor);
  tr.appendChild(tdDate);
  tr.appendChild(tdTime);
  tr.appendChild(tdAction);

  img.addEventListener("click", (e) => {
    if (typeof onPrescriptionClick === "function") {
      onPrescriptionClick(appointment, e);
      return;
    }
    // Default navigation behavior:
    const targetId = patientId ?? id;
    if (targetId == null) {
      console.warn("Prescription click: No patientId or id available to navigate.");
      return;
    }
    // Use encodeURIComponent for safety.
    window.location.href = `addPrescription.html?id=${encodeURIComponent(targetId)}`;
  });

  return tr;
}