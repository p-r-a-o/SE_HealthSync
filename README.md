# 📌 **HealthSync – Hospital Management System**

*A full-stack hospital workflow automation system built with Spring Boot + Next.js.*

---

## 🚀 Overview

**HealthSync** is an enterprise-grade hospital management system designed to streamline patient care, appointments, prescriptions, billing, pharmacy inventory, bed allocations, and role-based dashboards.

It uses a **Spring Boot backend**, a **Next.js (App Router) frontend**, **MySQL**, **JWT security**, and **comprehensive tests** (JUnit, Mockito, Testcontainers).

---

## ✨ Features

### 🧑‍⚕️ **Role-Based Interfaces**

* **Patient:** appointments, bills, prescriptions, medical history, profile
* **Doctor:** availability, appointments, prescriptions, patient list
* **Receptionist:** patient registration, appointments, beds, billing
* **Pharmacist:** prescriptions, medication inventory

### 🏥 **Core Modules**

* Patient Management
* Doctor Management & Availability
* Appointment Scheduling with conflict detection
* Prescription Management
* Billing & Invoice Generation
* Pharmacy Inventory & Fulfillment
* Bed Allocation & Status Tracking
* JWT-based Authentication
* DTO mapping + layered architecture
* Extensive automated testing

---

## 🧱 **Technology Stack**

### **Backend**

* Java 21
* Spring Boot 3.3
* Spring Security + JWT
* JPA/Hibernate
* MySQL
* Testcontainers
* Maven

### **Frontend**

* Next.js (App Router)
* React 18
* Tailwind CSS
* ShadCN UI
* TypeScript

---

## 📂 Project Structure

```
SE_HealthSync
├── application.properties
├── mvnw
├── mvnw.cmd
├── node
│   ├── node
│   ├── npm
│   ├── npm.cmd
│   ├── npx
│   └── npx.cmd
├── pom.xml
├── README.md
└── src
    ├── frontend
    │   ├── app
    │   │   ├── auth
    │   │   │   ├── login
    │   │   │   │   ├── page.tsx
    │   │   │   │   └── page.tsxZone.Identifier
    │   │   │   └── register
    │   │   │       ├── page.tsx
    │   │   │       └── page.tsxZone.Identifier
    │   │   ├── doctor
    │   │   │   ├── appointments
    │   │   │   │   ├── page.tsx
    │   │   │   │   └── page.tsxZone.Identifier
    │   │   │   ├── availability
    │   │   │   │   ├── page.tsx
    │   │   │   │   └── page.tsxZone.Identifier
    │   │   │   ├── patients
    │   │   │   │   ├── page.tsx
    │   │   │   │   └── page.tsxZone.Identifier
    │   │   │   ├── prescriptions
    │   │   │   │   ├── page.tsx
    │   │   │   │   └── page.tsxZone.Identifier
    │   │   │   └── profile
    │   │   │       └── page.tsx
    │   │   ├── globals.css
    │   │   ├── globals.cssZone.Identifier
    │   │   ├── layout.tsx
    │   │   ├── layout.tsxZone.Identifier
    │   │   ├── page.tsx
    │   │   ├── page.tsxZone.Identifier
    │   │   ├── patient
    │   │   │   ├── appointments
    │   │   │   │   ├── book
    │   │   │   │   │   ├── page.tsx
    │   │   │   │   │   └── page.tsxZone.Identifier
    │   │   │   │   ├── page.tsx
    │   │   │   │   └── page.tsxZone.Identifier
    │   │   │   ├── bills
    │   │   │   │   ├── page.tsx
    │   │   │   │   └── page.tsxZone.Identifier
    │   │   │   ├── medical-history
    │   │   │   │   ├── page.tsx
    │   │   │   │   └── page.tsxZone.Identifier
    │   │   │   ├── prescriptions
    │   │   │   │   ├── page.tsx
    │   │   │   │   └── page.tsxZone.Identifier
    │   │   │   └── profile
    │   │   │       ├── page.tsx
    │   │   │       └── page.tsxZone.Identifier
    │   │   ├── pharmacist
    │   │   │   ├── inventory
    │   │   │   │   ├── page.tsx
    │   │   │   │   └── page.tsxZone.Identifier
    │   │   │   └── prescriptions
    │   │   │       ├── page.tsx
    │   │   │       └── page.tsxZone.Identifier
    │   │   └── receptionist
    │   │       ├── appointments
    │   │       │   ├── book
    │   │       │   │   ├── page.tsx
    │   │       │   │   └── page.tsxZone.Identifier
    │   │       │   ├── page.tsx
    │   │       │   └── page.tsxZone.Identifier
    │   │       ├── beds
    │   │       │   ├── page.tsx
    │   │       │   └── page.tsxZone.Identifier
    │   │       ├── bills
    │   │       │   ├── page.tsx
    │   │       │   └── page.tsxZone.Identifier
    │   │       └── patients
    │   │           ├── page.tsx
    │   │           └── page.tsxZone.Identifier
    │   ├── components
    │   │   ├── bill-modal.tsx
    │   │   ├── medication-modal.tsx
    │   │   ├── navbar.tsx
    │   │   ├── navbar.tsxZone.Identifier
    │   │   ├── theme-provider.tsx
    │   │   ├── theme-provider.tsxZone.Identifier
    │   │   └── ui
    │   │       ├── accordion.tsx
    │   │       ├── accordion.tsxZone.Identifier
    │   │       ├── alert-dialog.tsx
    │   │       ├── alert-dialog.tsxZone.Identifier
    │   │       ├── alert.tsx
    │   │       ├── alert.tsxZone.Identifier
    │   │       ├── aspect-ratio.tsx
    │   │       ├── aspect-ratio.tsxZone.Identifier
    │   │       ├── avatar.tsx
    │   │       ├── avatar.tsxZone.Identifier
    │   │       ├── badge.tsx
    │   │       ├── badge.tsxZone.Identifier
    │   │       ├── breadcrumb.tsx
    │   │       ├── breadcrumb.tsxZone.Identifier
    │   │       ├── button-group.tsx
    │   │       ├── button-group.tsxZone.Identifier
    │   │       ├── button.tsx
    │   │       ├── button.tsxZone.Identifier
    │   │       ├── calendar.tsx
    │   │       ├── calendar.tsxZone.Identifier
    │   │       ├── card.tsx
    │   │       ├── card.tsxZone.Identifier
    │   │       ├── carousel.tsx
    │   │       ├── carousel.tsxZone.Identifier
    │   │       ├── chart.tsx
    │   │       ├── chart.tsxZone.Identifier
    │   │       ├── checkbox.tsx
    │   │       ├── checkbox.tsxZone.Identifier
    │   │       ├── collapsible.tsx
    │   │       ├── collapsible.tsxZone.Identifier
    │   │       ├── command.tsx
    │   │       ├── command.tsxZone.Identifier
    │   │       ├── context-menu.tsx
    │   │       ├── context-menu.tsxZone.Identifier
    │   │       ├── dialog.tsx
    │   │       ├── dialog.tsxZone.Identifier
    │   │       ├── drawer.tsx
    │   │       ├── drawer.tsxZone.Identifier
    │   │       ├── dropdown-menu.tsx
    │   │       ├── dropdown-menu.tsxZone.Identifier
    │   │       ├── empty.tsx
    │   │       ├── empty.tsxZone.Identifier
    │   │       ├── field.tsx
    │   │       ├── field.tsxZone.Identifier
    │   │       ├── form.tsx
    │   │       ├── form.tsxZone.Identifier
    │   │       ├── hover-card.tsx
    │   │       ├── hover-card.tsxZone.Identifier
    │   │       ├── input-group.tsx
    │   │       ├── input-group.tsxZone.Identifier
    │   │       ├── input-otp.tsx
    │   │       ├── input-otp.tsxZone.Identifier
    │   │       ├── input.tsx
    │   │       ├── input.tsxZone.Identifier
    │   │       ├── item.tsx
    │   │       ├── item.tsxZone.Identifier
    │   │       ├── kbd.tsx
    │   │       ├── kbd.tsxZone.Identifier
    │   │       ├── label.tsx
    │   │       ├── label.tsxZone.Identifier
    │   │       ├── menubar.tsx
    │   │       ├── menubar.tsxZone.Identifier
    │   │       ├── navigation-menu.tsx
    │   │       ├── navigation-menu.tsxZone.Identifier
    │   │       ├── pagination.tsx
    │   │       ├── pagination.tsxZone.Identifier
    │   │       ├── popover.tsx
    │   │       ├── popover.tsxZone.Identifier
    │   │       ├── progress.tsx
    │   │       ├── progress.tsxZone.Identifier
    │   │       ├── radio-group.tsx
    │   │       ├── radio-group.tsxZone.Identifier
    │   │       ├── resizable.tsx
    │   │       ├── resizable.tsxZone.Identifier
    │   │       ├── scroll-area.tsx
    │   │       ├── scroll-area.tsxZone.Identifier
    │   │       ├── select.tsx
    │   │       ├── select.tsxZone.Identifier
    │   │       ├── separator.tsx
    │   │       ├── separator.tsxZone.Identifier
    │   │       ├── sheet.tsx
    │   │       ├── sheet.tsxZone.Identifier
    │   │       ├── sidebar.tsx
    │   │       ├── sidebar.tsxZone.Identifier
    │   │       ├── skeleton.tsx
    │   │       ├── skeleton.tsxZone.Identifier
    │   │       ├── slider.tsx
    │   │       ├── slider.tsxZone.Identifier
    │   │       ├── sonner.tsx
    │   │       ├── sonner.tsxZone.Identifier
    │   │       ├── spinner.tsx
    │   │       ├── spinner.tsxZone.Identifier
    │   │       ├── switch.tsx
    │   │       ├── switch.tsxZone.Identifier
    │   │       ├── table.tsx
    │   │       ├── table.tsxZone.Identifier
    │   │       ├── tabs.tsx
    │   │       ├── tabs.tsxZone.Identifier
    │   │       ├── textarea.tsx
    │   │       ├── textarea.tsxZone.Identifier
    │   │       ├── toaster.tsx
    │   │       ├── toaster.tsxZone.Identifier
    │   │       ├── toast.tsx
    │   │       ├── toast.tsxZone.Identifier
    │   │       ├── toggle-group.tsx
    │   │       ├── toggle-group.tsxZone.Identifier
    │   │       ├── toggle.tsx
    │   │       ├── toggle.tsxZone.Identifier
    │   │       ├── tooltip.tsx
    │   │       ├── tooltip.tsxZone.Identifier
    │   │       ├── use-mobile.tsx
    │   │       ├── use-mobile.tsxZone.Identifier
    │   │       ├── use-toast.ts
    │   │       └── use-toast.tsZone.Identifier
    │   ├── components.json
    │   ├── components.jsonZone.Identifier
    │   ├── hooks
    │   │   ├── use-mobile.ts
    │   │   ├── use-mobile.tsZone.Identifier
    │   │   ├── use-toast.ts
    │   │   └── use-toast.tsZone.Identifier
    │   ├── lib
    │   │   ├── api.ts
    │   │   ├── api.tsZone.Identifier
    │   │   ├── auth-context.tsx
    │   │   ├── auth-context.tsxZone.Identifier
    │   │   ├── utils.ts
    │   │   └── utils.tsZone.Identifier
    │   ├── next.config.mjs
    │   ├── next.config.mjsZone.Identifier
    │   ├── next-env.d.ts
    │   ├── package.json
    │   ├── package.jsonZone.Identifier
    │   ├── package-lock.json
    │   ├── pnpm-lock.yaml
    │   ├── pnpm-lock.yamlZone.Identifier
    │   ├── postcss.config.mjs
    │   ├── postcss.config.mjsZone.Identifier
    │   ├── styles
    │   │   ├── globals.css
    │   │   └── globals.cssZone.Identifier
    │   └── tsconfig.json
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── v322
    │   │           └── healthsync
    │   │               ├── config
    │   │               │   └── DataInitializer.java
    │   │               ├── controller
    │   │               │   ├── AppointmentController.java
    │   │               │   ├── AuthController.java
    │   │               │   ├── BedController.java
    │   │               │   ├── BillingController.java
    │   │               │   ├── DepartmentController.java
    │   │               │   ├── DoctorController.java
    │   │               │   ├── HTMLController.java
    │   │               │   ├── IndexController.java
    │   │               │   ├── MedicationController.java
    │   │               │   ├── PatientController.java
    │   │               │   ├── PharmacistController.java
    │   │               │   ├── PharmacyController.java
    │   │               │   ├── PrescriptionController.java
    │   │               │   ├── ReceptionistController.java
    │   │               │   └── TestController.java
    │   │               ├── dto
    │   │               │   ├── AppointmentDTO.java
    │   │               │   ├── BedDTO.java
    │   │               │   ├── BillDTO.java
    │   │               │   ├── BillItemDTO.java
    │   │               │   ├── DepartmentDTO.java
    │   │               │   ├── DoctorAvailabilityDTO.java
    │   │               │   ├── DoctorDTO.java
    │   │               │   ├── DTOMapper.java
    │   │               │   ├── EntityMapper.java
    │   │               │   ├── MedicationDTO.java
    │   │               │   ├── PatientDTO.java
    │   │               │   ├── PatientRegisterDTO.java
    │   │               │   ├── PharmacistDTO.java
    │   │               │   ├── PharmacyDTO.java
    │   │               │   ├── PrescriptionDTO.java
    │   │               │   ├── PrescriptionItemDTO.java
    │   │               │   └── ReceptionistDTO.java
    │   │               ├── entity
    │   │               │   ├── Appointment.java
    │   │               │   ├── Bed.java
    │   │               │   ├── BillItem.java
    │   │               │   ├── Bill.java
    │   │               │   ├── Department.java
    │   │               │   ├── DoctorAvailability.java
    │   │               │   ├── Doctor.java
    │   │               │   ├── Error.java
    │   │               │   ├── Medication.java
    │   │               │   ├── Patient.java
    │   │               │   ├── Pharmacist.java
    │   │               │   ├── Pharmacy.java
    │   │               │   ├── PrescriptionItem.java
    │   │               │   ├── Prescription.java
    │   │               │   ├── Receptionist.java
    │   │               │   └── User.java
    │   │               ├── HospitalApplication.java
    │   │               ├── repository
    │   │               │   ├── AppointmentRepository.java
    │   │               │   ├── BedRepository.java
    │   │               │   ├── BillItemRepository.java
    │   │               │   ├── BillRepository.java
    │   │               │   ├── DepartmentRepository.java
    │   │               │   ├── DoctorAvailabilityRepository.java
    │   │               │   ├── DoctorRepository.java
    │   │               │   ├── MedicationRepository.java
    │   │               │   ├── PatientRepository.java
    │   │               │   ├── PharmacistRepository.java
    │   │               │   ├── PharmacyRepository.java
    │   │               │   ├── PrescriptionItemRepository.java
    │   │               │   ├── PrescriptionRepository.java
    │   │               │   ├── ReceptionistRepository.java
    │   │               │   └── UserRepository.java
    │   │               ├── security
    │   │               │   ├── JwtAuthenticationFilter.java
    │   │               │   ├── SecurityConfig.java
    │   │               │   └── WebConfiguration.java
    │   │               └── service
    │   │                   ├── AppointmentService.java
    │   │                   ├── AuthService.java
    │   │                   ├── BedService.java
    │   │                   ├── BillingService.java
    │   │                   ├── DepartmentService.java
    │   │                   ├── DoctorService.java
    │   │                   ├── JwtService.java
    │   │                   ├── MedicationService.java
    │   │                   ├── PatientService.java
    │   │                   ├── PharmacistService.java
    │   │                   ├── PharmacyService.java
    │   │                   ├── PrescriptionService.java
    │   │                   ├── ReceptionistService.java
    │   │                   └── UserService.java
    │   └── resources
    │       ├── application.properties
    │       └── application.propertiesZone.Identifier
    └── test
        └── java
            └── com
                └── v322
                    └── healthsync
                        ├── AppointmentServiceTest.java
                        ├── AuthServiceTest.java
                        ├── BaseIntegrationTest.java
                        ├── BedServiceTest.java
                        ├── BillingServiceTest.java
                        ├── DepartmentServiceTest.java
                        ├── DoctorRepositoryTest.java
                        ├── DoctorServiceTest.java
                        ├── MedicationServiceTest.java
                        ├── PatientServiceTest.java
                        ├── PharmacistService.java
                        ├── PrescriptionServiceTest.java
                        ├── ReceptionistServiceTest.java
                        └── UserServiceTest.java


```

---

## 🔐 Authentication & Security

* **JWT Authentication**
* Stateless sessions
* BCrypt password hashing
* Role-based route protection
* Secure Spring Security configuration

---

## 🗄 Database Schema (Summary)

### Key tables

* `appointment`
* `bed`
* `bill`
* `bill_item`
* `department`
* `doctor_availability`  
* `medication`
* `pharmacy`
* `prescription`
* `prescription_item`
* `users`

Relationships include:

* Patient → Appointments (1:N)
* Doctor → Appointments (1:N)
* Patient → Prescriptions (1:N)
* Prescription → Items (1:N)
* Patient → Bills (1:N)
* Bed → Patient (1:1)
* Doctor → Department (N:1)

---

## 🧪 Testing

### ✔ Unit Tests

* Service logic using **Mockito**
* DTO mapping tests
* Validation tests
* Repository layer tests

### ✔ Integration Tests

* Uses **Testcontainers** for MySQL
* Tests for:

  * Appointment booking + conflict rules
  * Billing calculation
  * Doctor repository
  * Medication & pharmacy logic
  * Authentication logic

Run all tests:

```
./mvnw test
```

---

## ▶️ Running the Backend

### 1️⃣ **Configure MySQL**

Create database:

```sql
CREATE DATABASE healthsync;
```

Update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/healthsync
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### 2️⃣ **Run Spring Boot**

```
./mvnw spring-boot:run
```

Backend runs on:

```
http://localhost:5000
```

---

## ▶️ Running the Frontend

### 1️⃣ Go to the frontend directory:

```
cd src/main/frontend
```

### 2️⃣ Install dependencies:

```
npm install
```

### 3️⃣ Start dev server:

```
npm run dev
```

Frontend runs on:

```
http://localhost:3000
```

---

## 🧩 API Endpoints (Summary)

### Auth

```
POST /api/auth/login
POST /api/auth/register
GET  /api/auth/me
```

### Patients

```
GET    /api/patients
POST   /api/patients
PUT    /api/patients/{id}
GET    /api/patients/{id}
```

### Doctors

```
GET    /api/doctors
POST   /api/doctors
GET    /api/doctors/{id}
```

### Appointments

```
POST   /api/appointments
GET    /api/appointments/patient/{id}
GET    /api/appointments/doctor/{id}
```

### Prescriptions

```
POST   /api/prescriptions
GET    /api/prescriptions/patient/{id}
```

### Billing

```
POST  /api/bills
PUT   /api/bills/{id}/payment
```

---

## 🧱 Architecture

```
Next.js Frontend → REST API → Spring Boot → JPA/Hibernate → MySQL
                         ↑
                       JWT
```

* **Frontend:** UI + API calls + auth context
* **Backend:** Controllers → Services → Repositories
* **Database:** Normalized, optimized schema
* **Security:** JWT filters + role-based access

---

## 🧭 Future Enhancements

* Email/SMS appointment reminders
* AI-driven scheduling optimization
* Lab module & radiology integration
* Analytics dashboard
* Mobile app

---

## 🏁 Conclusion

HealthSync delivers a fully functional hospital automation system built with modern full-stack architecture. It is modular, scalable, secure, and supported by comprehensive testing and industry-standard patterns.

---

If you want, I can also generate:
✅ A **diagram-based README**
✅ A **badge-enhanced GitHub README**
✅ A **separate CONTRIBUTING.md**
✅ A **Docker + docker-compose setup**

Just tell me — I can generate those instantly.
