## MySQL Database Design

This document outlines the database schema for a clinic management system. The design focuses on operational data—patients, doctors, appointments, and admins—and introduces additional tables to support clinic locations, payments, and prescriptions. Comments are provided to justify design choices.

---

### Table: patients
- id: INT, PRIMARY KEY, AUTO_INCREMENT
- first_name: VARCHAR(50), NOT NULL
- last_name: VARCHAR(50), NOT NULL
- date_of_birth: DATE, NOT NULL
- gender: ENUM('Male', 'Female', 'Other'), NOT NULL
- phone: VARCHAR(20), NOT NULL, UNIQUE
- email: VARCHAR(100), UNIQUE
- address: VARCHAR(255)
- created_at: DATETIME, NOT NULL, DEFAULT CURRENT_TIMESTAMP

> **Comments:**  
> - Phone and email are unique to avoid duplicate registrations.  
> - Email/phone format validation should be handled in application code.  
> - Patient records should not be deleted if appointments/history must be retained (consider "soft delete" with a status field).

---

### Table: doctors
- id: INT, PRIMARY KEY, AUTO_INCREMENT
- first_name: VARCHAR(50), NOT NULL
- last_name: VARCHAR(50), NOT NULL
- specialty: VARCHAR(100), NOT NULL
- phone: VARCHAR(20), NOT NULL, UNIQUE
- email: VARCHAR(100), NOT NULL, UNIQUE
- clinic_location_id: INT, FOREIGN KEY → clinic_locations(id)
- active: BOOLEAN, NOT NULL, DEFAULT TRUE

> **Comments:**  
> - Each doctor is assigned to a clinic location.  
> - Uniqueness constraints on contact info.  
> - "active" determines if doctor is currently available for appointments.

---

### Table: clinic_locations
- id: INT, PRIMARY KEY, AUTO_INCREMENT
- name: VARCHAR(100), NOT NULL
- address: VARCHAR(255), NOT NULL
- phone: VARCHAR(20)
- email: VARCHAR(100)

> **Comments:**  
> - Allows multi-location clinics.  
> - Each doctor and appointment can reference a location.

---

### Table: appointments
- id: INT, PRIMARY KEY, AUTO_INCREMENT
- doctor_id: INT, FOREIGN KEY → doctors(id), NOT NULL
- patient_id: INT, FOREIGN KEY → patients(id), NOT NULL
- appointment_time: DATETIME, NOT NULL
- status: ENUM('Scheduled', 'Completed', 'Cancelled'), NOT NULL, DEFAULT 'Scheduled'
- clinic_location_id: INT, FOREIGN KEY → clinic_locations(id), NOT NULL
- created_at: DATETIME, NOT NULL, DEFAULT CURRENT_TIMESTAMP

> **Comments:**  
> - If a patient is deleted, consider restricting deletion if appointments exist (or use cascading delete carefully).  
> - Appointments for a doctor should not overlap; enforce via application logic or add a UNIQUE constraint on (doctor_id, appointment_time).  
> - Each appointment is tied to a clinic location.

---

### Table: admins
- id: INT, PRIMARY KEY, AUTO_INCREMENT
- username: VARCHAR(50), NOT NULL, UNIQUE
- password_hash: VARCHAR(255), NOT NULL
- email: VARCHAR(100), NOT NULL, UNIQUE
- role: ENUM('Admin', 'Staff'), NOT NULL, DEFAULT 'Staff'
- created_at: DATETIME, NOT NULL, DEFAULT CURRENT_TIMESTAMP

> **Comments:**  
> - Stores admin and staff credentials.  
> - Passwords should be hashed before storing.  
> - Role-based access for different admin types.

---

### Table: payments
- id: INT, PRIMARY KEY, AUTO_INCREMENT
- appointment_id: INT, FOREIGN KEY → appointments(id), NOT NULL
- patient_id: INT, FOREIGN KEY → patients(id), NOT NULL
- amount: DECIMAL(10,2), NOT NULL
- payment_date: DATETIME, NOT NULL, DEFAULT CURRENT_TIMESTAMP
- payment_method: ENUM('Cash', 'Card', 'Online'), NOT NULL
- status: ENUM('Pending', 'Completed', 'Failed'), NOT NULL

> **Comments:**  
> - Payments are tied to appointments and patients.  
> - Payment status tracks transaction state.

---

### Table: prescriptions
- id: INT, PRIMARY KEY, AUTO_INCREMENT
- appointment_id: INT, FOREIGN KEY → appointments(id), NOT NULL
- doctor_id: INT, FOREIGN KEY → doctors(id), NOT NULL
- patient_id: INT, FOREIGN KEY → patients(id), NOT NULL
- prescribed_at: DATETIME, NOT NULL, DEFAULT CURRENT_TIMESTAMP
- notes: TEXT

> **Comments:**  
> - Prescriptions are linked to appointments for traceability.  
> - Keeping prescription history is important for medical records.

---

### Additional Design Notes

- **On Deletion:**  
  - Deleting a patient: Consider "soft delete" (mark as inactive) to preserve appointment and prescription history.
  - Deleting a doctor: Prevent if appointments/prescriptions exist, or handle via "inactive" status.
  - Deleting appointments: Allow only if not completed, or archive instead.
- **Appointment Overlaps:**  
  - Enforce via application logic, or create a UNIQUE constraint on (doctor_id, appointment_time).
- **Time Slots:**  
  - Optionally, add a `doctor_availability` table for managing time slots.
- **History Retention:**  
  - Medical/legal reasons may require retaining appointment/prescription records indefinitely.

---

You can further extend this schema with audit logs, insurance, or medical records as required.