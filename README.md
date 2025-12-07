
# HealthSync – Full-Stack Medical Management System

This project uses **React + Next.js** for the frontend and **Spring Boot (Java)** for the backend.  
Integration tests run using **Docker + Testcontainers**.

---

## 🚀 Frontend Setup (React + Next.js)

### 1. Go to the frontend folder

```bash
cd frontend
````

### 2. Install dependencies

```bash
npm install
```

### 3. Start development server

```bash
npm run dev
```

---

## ⚙️ Backend Setup (Spring Boot + Java)

### 1. Go to the backend folder

```bash
cd backend
```

### 2. Configure database (MySQL)

Set your database details in `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/healthsync
spring.datasource.username=<yourusername>
spring.datasource.password=<yourpassword>
spring.jpa.hibernate.ddl-auto=update
```

### 3. Run the backend

```bash
mvn clean install -Dskip.npm
```

---

## 🧪 Running Tests (Testcontainers + Docker)

```bash
mvn clean test -Dskip.npm
```