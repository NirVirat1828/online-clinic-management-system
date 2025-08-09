/**
 * Doctor Services Module
 *
 * Provides API helper functions for doctor-related operations:
 *  - getDoctors()
 *  - deleteDoctor(id, token)
 *  - saveDoctor(doctorData, token)
 *  - filterDoctors({ name, time, specialization })
 *
 * Adjust endpoint path segments if your backend differs. The code assumes
 * path-style parameters for token and filters, based on your specification.
 *
 * Expected config:
 *   export const API_BASE_URL = "http://localhost:8080/api";  // example
 *
 * If your actual config file is in a different location, update the import path below.
 */
import { API_BASE_URL } from "../config.js"; // Adjust path if needed

// Base endpoint for doctor-related actions
export const DOCTOR_API = `${API_BASE_URL}/doctors`;

/**
 * Internal helper to safely parse JSON (in case of empty bodies).
 * @param {Response} response
 * @returns {Promise<any>}
 */
async function safeJson(response) {
  try {
    // Some backends return 204 No Content
    if (response.status === 204) return null;
    return await response.json();
  } catch (err) {
    console.warn("Failed to parse JSON response:", err);
    return null;
  }
}

/**
 * Internal helper to build a descriptive error.
 * @param {Response} resp
 * @param {any} body
 * @returns {Error}
 */
function buildHttpError(resp, body) {
  const msg =
    (body && (body.message || body.error)) ||
    `HTTP ${resp.status} ${resp.statusText}`;
  const error = new Error(msg);
  error.status = resp.status;
  error.body = body;
  return error;
}

/**
 * Fetch all doctors.
 * @returns {Promise<{ doctors: Array }|{ doctors: [] }>}
 */
export async function getDoctors() {
  try {
    const resp = await fetch(DOCTOR_API, {
      method: "GET",
      headers: { Accept: "application/json" }
    });

    const data = await safeJson(resp);

    if (!resp.ok) {
      console.error("getDoctors failed:", data);
      return { doctors: [] };
    }

    // Backend may return { doctors: [...] } or direct array.
    if (Array.isArray(data)) return { doctors: data };
    if (data && Array.isArray(data.doctors)) return { doctors: data.doctors };
    return { doctors: [] };
  } catch (err) {
    console.error("getDoctors error:", err);
    return { doctors: [] };
  }
}

/**
 * Delete a doctor by id (admin-only).
 * Endpoint style assumed: DELETE /doctors/{doctorId}/{token}
 *
 * @param {string|number} doctorId
 * @param {string} token - Admin auth token
 * @returns {Promise<{ success: boolean, message: string }>}
 */
export async function deleteDoctor(doctorId, token) {
  if (!doctorId) return { success: false, message: "Doctor ID is required." };
  if (!token) return { success: false, message: "Token is required." };

  const url = `${DOCTOR_API}/${encodeURIComponent(
    doctorId
  )}/${encodeURIComponent(token)}`;

  try {
    const resp = await fetch(url, {
      method: "DELETE",
      headers: { Accept: "application/json" }
    });
    const data = await safeJson(resp);

    if (!resp.ok) {
      const message =
        (data && (data.message || data.error)) ||
        `Failed to delete doctor (status ${resp.status})`;
      return { success: false, message };
    }

    return {
      success: true,
      message:
        (data && (data.message || data.status)) ||
        "Doctor deleted successfully."
    };
  } catch (err) {
    console.error("deleteDoctor error:", err);
    return { success: false, message: "Network or server error." };
  }
}

/**
 * Save (create) a new doctor.
 * Endpoint style assumed: POST /doctors/{token}
 *
 * @param {Object} doctorData - Doctor payload (name, specialization, email, etc.)
 * @param {string} token - Admin auth token
 * @returns {Promise<{ success: boolean, message: string, doctor?: any }>}
 */
export async function saveDoctor(doctorData, token) {
  if (!token) return { success: false, message: "Token is required." };
  if (!doctorData || typeof doctorData !== "object") {
    return { success: false, message: "Invalid doctor data." };
  }

  const url = `${DOCTOR_API}/${encodeURIComponent(token)}`;

  try {
    const resp = await fetch(url, {
      method: "POST",
      headers: {
        Accept: "application/json",
        "Content-Type": "application/json"
      },
      body: JSON.stringify(doctorData)
    });

    const data = await safeJson(resp);

    if (!resp.ok) {
      console.error("saveDoctor failed:", data);
      return {
        success: false,
        message:
          (data && (data.message || data.error)) ||
          `Failed to save doctor (status ${resp.status})`
      };
    }

    return {
      success: true,
      message:
        (data && (data.message || data.status)) ||
        "Doctor saved successfully.",
      doctor: data && data.doctor ? data.doctor : data
    };
  } catch (err) {
    console.error("saveDoctor error:", err);
    return { success: false, message: "Network or server error." };
  }
}

/**
 * Filter doctors by criteria.
 * Endpoint style assumed: GET /doctors/filter/{name}/{time}/{specialization}
 *
 * Empty criteria become 'all' in the path.
 *
 * @param {Object} criteria
 * @param {string} [criteria.name]
 * @param {string} [criteria.time]
 * @param {string} [criteria.specialization]
 * @returns {Promise<{ doctors: Array }>}
 */
export async function filterDoctors({
  name = "",
  time = "",
  specialization = ""
} = {}) {
  const safe = (v) =>
    encodeURIComponent(v && v.trim() ? v.trim() : "all");

  const url = `${DOCTOR_API}/filter/${safe(name)}/${safe(time)}/${safe(
    specialization
  )}`;

  try {
    const resp = await fetch(url, {
      method: "GET",
      headers: { Accept: "application/json" }
    });

    const data = await safeJson(resp);

    if (!resp.ok) {
      console.error("filterDoctors failed:", data);
      return { doctors: [] };
    }

    if (Array.isArray(data)) return { doctors: data };
    if (data && Array.isArray(data.doctors)) return { doctors: data.doctors };
    return { doctors: [] };
  } catch (err) {
    console.error("filterDoctors error:", err);
    alert("Failed to filter doctors. Please try again later.");
    return { doctors: [] };
  }
}

/**
 * OPTIONAL: Centralized export object (if you prefer named default style).
 */
const DoctorService = {
  getDoctors,
  deleteDoctor,
  saveDoctor,
  filterDoctors
};

export default DoctorService;