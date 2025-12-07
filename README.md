# 📌 **HealthSync – Hospital Management System**

*A full-stack hospital workflow automation system built with Spring Boot + Next.js.*

**Submitted by Team V322:**
**Navaneeth D    - IMT2023095**
**R Ricky Roger  - IMT2023098**
**Pramatha V Rao - IMT2023116**

### **[Report here](./report.md)**


---

## Overview

**HealthSync** is an enterprise-grade hospital management system designed to streamline patient care, appointments, prescriptions, billing, pharmacy inventory, bed allocations, and role-based dashboards.

It uses a **Spring Boot backend**, a **Next.js (App Router) frontend**, **MySQL**, **JWT security**, and **comprehensive tests** (JUnit, Mockito, Testcontainers).

---

## Features

### **Role-Based Interfaces**

* **Patient:** appointments, bills, prescriptions, medical history, profile
* **Doctor:** availability, appointments, prescriptions, patient list
* **Receptionist:** patient registration, appointments, beds, billing
* **Pharmacist:** prescriptions, medication inventory

### **Core Modules**

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

## **Technology Stack**

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

## Project Structure

```bash
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
    │   │   │   │   └── page.tsx
    │   │   │   └── register
    │   │   │       └── page.tsx
    │   │   ├── doctor
    │   │   │   ├── appointments
    │   │   │   │   └── page.tsx
    │   │   │   ├── availability
    │   │   │   │   └── page.tsx
    │   │   │   ├── patients
    │   │   │   │   └── page.tsx
    │   │   │   ├── prescriptions
    │   │   │   │   └── page.tsx
    │   │   │   └── profile
    │   │   │       └── page.tsx
    │   │   ├── globals.css
    │   │   ├── layout.tsx
    │   │   ├── page.tsx
    │   │   ├── patient
    │   │   │   ├── appointments
    │   │   │   │   ├── book
    │   │   │   │   │   └── page.tsx
    │   │   │   │   └── page.tsx
    │   │   │   ├── bills
    │   │   │   │   └── page.tsx
    │   │   │   ├── medical-history
    │   │   │   │   └── page.tsx
    │   │   │   ├── prescriptions
    │   │   │   │   └── page.tsx
    │   │   │   └── profile
    │   │   │       └── page.tsx
    │   │   ├── pharmacist
    │   │   │   ├── inventory
    │   │   │   │   └── page.tsx
    │   │   │   └── prescriptions
    │   │   │       └── page.tsx
    │   │   └── receptionist
    │   │       ├── appointments
    │   │       │   ├── book
    │   │       │   │   └── page.tsx
    │   │       │   └── page.tsx
    │   │       ├── beds
    │   │       │   └── page.tsx
    │   │       ├── bills
    │   │       │   └── page.tsx
    │   │       └── patients
    │   │           └── page.tsx
    │   ├── components
    │   │   ├── bill-modal.tsx
    │   │   ├── medication-modal.tsx
    │   │   ├── navbar.tsx
    │   │   ├── navbar.tsx
    │   │   ├── theme-provider.tsx
    │   │   ├── theme-provider.tsx
    │   │   └── ui
    │   │       ├── accordion.tsx
    │   │       ├── alert-dialog.tsx
    │   │       ├── alert.tsx
    │   │       ├── aspect-ratio.tsx
    │   │       ├── avatar.tsx
    │   │       ├── badge.tsx
    │   │       ├── breadcrumb.tsx
    │   │       ├── button-group.tsx
    │   │       ├── button.tsx
    │   │       ├── calendar.tsx
    │   │       ├── card.tsx
    │   │       ├── carousel.tsx
    │   │       ├── chart.tsx
    │   │       ├── checkbox.tsx
    │   │       ├── collapsible.tsx
    │   │       ├── command.tsx
    │   │       ├── context-menu.tsx
    │   │       ├── dialog.tsx
    │   │       ├── drawer.tsx
    │   │       ├── dropdown-menu.tsx
    │   │       ├── empty.tsx
    │   │       ├── field.tsx
    │   │       ├── form.tsx
    │   │       ├── hover-card.tsx
    │   │       ├── input-group.tsx
    │   │       ├── input-otp.tsx
    │   │       ├── input.tsx
    │   │       ├── item.tsx
    │   │       ├── kbd.tsx
    │   │       ├── label.tsx
    │   │       ├── menubar.tsx
    │   │       ├── navigation-menu.tsx
    │   │       ├── pagination.tsx
    │   │       ├── popover.tsx
    │   │       ├── progress.tsx
    │   │       ├── radio-group.tsx
    │   │       ├── resizable.tsx
    │   │       ├── scroll-area.tsx
    │   │       ├── select.tsx
    │   │       ├── separator.tsx
    │   │       ├── sheet.tsx
    │   │       ├── sidebar.tsx
    │   │       ├── skeleton.tsx
    │   │       ├── slider.tsx
    │   │       ├── sonner.tsx
    │   │       ├── spinner.tsx
    │   │       ├── switch.tsx
    │   │       ├── table.tsx
    │   │       ├── tabs.tsx
    │   │       ├── textarea.tsx
    │   │       ├── toaster.tsx
    │   │       ├── toast.tsx
    │   │       ├── toggle-group.tsx
    │   │       ├── toggle.tsx
    │   │       ├── tooltip.tsx
    │   │       ├── use-mobile.tsx
    │   │       └── use-toast.ts
    │   ├── components.json
    │   ├── hooks
    │   │   ├── use-mobile.ts
    │   │   └── use-toast.ts
    │   ├── lib
    │   │   ├── api.ts
    │   │   ├── auth-context.tsx
    │   │   └── utils.ts
    │   ├── next.config.mjs
    │   ├── next-env.d.ts
    │   ├── package.json
    │   ├── package-lock.json
    │   ├── pnpm-lock.yaml
    │   ├── postcss.config.mjs
    │   ├── styles
    │   │   └── globals.css
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
    │       └── application.properties
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

## Database Schema

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

---

## Running the Backend

### 0️ **Prerequisites**

Before running the backend, make sure you have:

* **WSL/Ubuntu** or regular Ubuntu
* **Docker** installed

  * If using WSL: Docker Desktop on **Windows** is enough; WSL will use it automatically
* **MySQL** installed on Linux/WSL

Verify:

```bash
docker --version
mysql --version
```

---

### 1️ **Configure application.properties**

Ideally **do NOT modify** `application.properties`.
But **if required**, only edit this file: `/src/main/resources/application.properties`

Use:

```properties
spring.application.name=healthsync
server.port=5000
spring.datasource.url=jdbc:mysql://localhost:3306/healthsync
spring.datasource.username=root
spring.datasource.password=password
```

⚠️ Do **not** edit any auto-generated files inside `src/` other than this one.

---

### 2️ **Build the Backend**

From the project root:

```bash
mvn clean install -Dskip.npm
```

This will:

* Build the backend
* Skip building the frontend (because Next.js builds separately)
* Generate the final JAR inside `/target`

---

### 3️ **Run the Backend**

Navigate to the `target` directory:

```bash
cd target
```

Run the jar:

```bash
java -jar healthsync-<version>.jar
```

The backend will now run on: `http://localhost:5000`


---

## Running the Frontend

### 1️ Navigate to frontend folder

```bash
cd src/frontend
```

### 2️ Install dependencies

```bash
npm install
```

### 3️ Start development server

```bash
npm run dev
```

Frontend will run at: `http://localhost:3000`

---

## Final Setup Summary

After following the steps:

* **Backend:** running at `http://localhost:5000`
* **Frontend:** running at `http://localhost:3000`

Both services work together without modifying backend ports.

---

## Testing

HealthSync includes complete unit tests and integration tests.

### ✔ Unit Tests

* DTO mapping tests
* Validation tests
* Repository behavior tests

### ✔ Integration Tests

* **Testcontainers** for isolated MySQL testing
* Covers:

  * Appointment scheduling & conflict rules
  * Billing calculation
  * Doctor repository
  * Medication & pharmacy workflow
  * Authentication logic

Run all tests:

```bash
mvn clean test -Dskip.npm
```

