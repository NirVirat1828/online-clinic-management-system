# Online Clinic Management System – User Stories

---

## Admin User Stories

### 1. Admin Login

**Title:**  
_As an admin, I want to log into the portal with my username and password, so that I can manage the platform securely._

**Acceptance Criteria:**  
1. Admin can enter valid credentials to access the portal.
2. Invalid login attempts are rejected with appropriate messages.
3. Admin receives a session token (e.g., JWT) upon successful login.

**Priority:** High  
**Story Points:** 3  
**Notes:**  
- Secure authentication required.
- Audit unsuccessful login attempts.

---

### 2. Admin Logout

**Title:**  
_As an admin, I want to log out of the portal, so that I can protect system access._

**Acceptance Criteria:**  
1. Admin can securely log out from any device.
2. Session is invalidated after logout.
3. Admin is redirected to the login page.

**Priority:** High  
**Story Points:** 2  
**Notes:**  
- All tokens/sessions are properly cleared.

---

### 3. Add Doctor

**Title:**  
_As an admin, I want to add doctors to the portal, so that I can expand the clinic's medical staff._

**Acceptance Criteria:**  
1. Admin can fill out a form with doctor’s information.
2. Doctor receives login credentials after being added.
3. Added doctors appear in the list of active doctors.

**Priority:** High  
**Story Points:** 5  
**Notes:**  
- Duplicate checks for emails/usernames.

---

### 4. Delete Doctor

**Title:**  
_As an admin, I want to delete a doctor's profile from the portal, so that I can manage staff changes and maintain accurate records._

**Acceptance Criteria:**  
1. Admin can select and delete a doctor’s profile.
2. System warns if the doctor has upcoming appointments.
3. Associated data is handled as per data retention policy.

**Priority:** High  
**Story Points:** 5  
**Notes:**  
- Handle orphaned appointments gracefully.

---

### 5. Run Usage Statistics

**Title:**  
_As an admin, I want to run a stored procedure in MySQL CLI to get the number of appointments per month, so that I can track usage statistics._

**Acceptance Criteria:**  
1. Stored procedure exists and returns monthly appointment counts.
2. Admin can trigger this procedure and view results.
3. Results are easy to interpret and export.

**Priority:** Medium  
**Story Points:** 3  
**Notes:**  
- Procedure should be optimized for performance.

---

## Patient User Stories

### 1. View Doctors Without Login

**Title:**  
_As a patient, I want to view a list of doctors without logging in, so that I can explore options before registering._

**Acceptance Criteria:**  
1. Patients can access doctor list as guests.
2. Doctor profiles display name, specialization, and contact info.
3. No sensitive data is shown.

**Priority:** High  
**Story Points:** 3  
**Notes:**  
- Search and filter options available.

---

### 2. Patient Sign Up

**Title:**  
_As a patient, I want to sign up using my email and password, so that I can book appointments._

**Acceptance Criteria:**  
1. Patients can create an account using valid email and strong password.
2. Duplicate emails are not allowed.
3. Confirmation is sent upon successful signup.

**Priority:** High  
**Story Points:** 3  
**Notes:**  
- Email verification optional.

---

### 3. Patient Login

**Title:**  
_As a patient, I want to log into the portal, so that I can manage my bookings._

**Acceptance Criteria:**  
1. Patients can log in with valid credentials.
2. Invalid credentials are rejected with messages.
3. JWT/session token is issued on success.

**Priority:** High  
**Story Points:** 2  
**Notes:**  
- Secure password handling.

---

### 4. Patient Logout

**Title:**  
_As a patient, I want to log out of the portal, so that I can secure my account._

**Acceptance Criteria:**  
1. Patient can log out at any time.
2. Session/token is invalidated.
3. Redirect to login or home page.

**Priority:** High  
**Story Points:** 2  
**Notes:**  
- Support for multiple devices/sessions.

---

### 5. Book Appointment

**Title:**  
_As a patient, I want to log in and book an hour-long appointment to consult with a doctor, so that I can receive medical advice._

**Acceptance Criteria:**  
1. Patients can select a doctor and time slot for booking.
2. Appointment duration is fixed at one hour.
3. Booking confirmation is provided.

**Priority:** High  
**Story Points:** 5  
**Notes:**  
- Prevent double-booking via conflict checks.

---

### 6. View Upcoming Appointments

**Title:**  
_As a patient, I want to view my upcoming appointments, so that I can prepare accordingly._

**Acceptance Criteria:**  
1. Patients can see a list of all future appointments.
2. Details include doctor, date, time, and location.
3. Users can cancel or reschedule from this view.

**Priority:** High  
**Story Points:** 3  
**Notes:**  
- Past appointments are shown separately or archived.

---

## Doctor User Stories

### 1. Doctor Login

**Title:**  
_As a doctor, I want to log into the portal, so that I can manage my appointments._

**Acceptance Criteria:**  
1. Doctor can log in using assigned credentials.
2. Invalid logins are rejected with clear messages.
3. JWT/session token is provided on success.

**Priority:** High  
**Story Points:** 2  
**Notes:**  
- Account must be activated by admin.

---

### 2. Doctor Logout

**Title:**  
_As a doctor, I want to log out of the portal, so that I can protect my data._

**Acceptance Criteria:**  
1. Doctor can log out securely.
2. Session/token is invalidated.
3. Redirect to login or home page.

**Priority:** High  
**Story Points:** 2  
**Notes:**  
- Logout works on all devices.

---

### 3. View Appointment Calendar

**Title:**  
_As a doctor, I want to view my appointment calendar, so that I can stay organized._

**Acceptance Criteria:**  
1. Doctor sees all upcoming appointments in calendar view.
2. Details include patient, date, and time.
3. Option to filter by date or status.

**Priority:** High  
**Story Points:** 5  
**Notes:**  
- Calendar is easily navigable.

---

### 4. Mark Unavailability

**Title:**  
_As a doctor, I want to mark my unavailability, so that patients can only book available slots._

**Acceptance Criteria:**  
1. Doctor can set unavailable dates/times.
2. These slots are blocked from patient booking.
3. Doctor can update or remove unavailability.

**Priority:** High  
**Story Points:** 5  
**Notes:**  
- System prevents bookings during unavailable periods.

---

### 5. Update Profile

**Title:**  
_As a doctor, I want to update my profile with specialization and contact information, so that patients have up-to-date information._

**Acceptance Criteria:**  
1. Doctor can edit specialization, contact, and other profile details.
2. Changes are reflected immediately in public profiles.
3. Admin can review changes if needed.

**Priority:** Medium  
**Story Points:** 3  
**Notes:**  
- Profile picture upload optional.

---

### 6. View Patient Details

**Title:**  
_As a doctor, I want to view the patient details for upcoming appointments, so that I can be prepared._

**Acceptance Criteria:**  
1. Doctor can access patient information for scheduled appointments.
2. Details include medical history and contact info (if permitted).
3. Data is only accessible for confirmed appointments.

**Priority:** High  
**Story Points:** 3  
**Notes:**  
- Sensitive data is protected and access is logged.

---