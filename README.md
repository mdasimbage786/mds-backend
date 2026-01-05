# 🏥 Medicine Donation System - Backend

A robust Spring Boot REST API that powers the Medicine Donation System, enabling secure medicine donations, applications, and NGO verification processes.

[![Java](https://img.shields.io/badge/Java-17-orange)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/license-MIT-green)](LICENSE)

## 📋 Table of Contents

- [Overview](#overview)
- [Key Features](#key-features)
- [Tech Stack](#tech-stack)
- [System Architecture](#system-architecture)
- [Getting Started](#getting-started)
- [Database Schema](#database-schema)
- [API Documentation](#api-documentation)
- [Security](#security)
- [Deployment](#deployment)
- [Testing](#testing)
- [Contributing](#contributing)

## 🎯 Overview

The Medicine Donation System Backend is a RESTful API service built with Spring Boot that handles all business logic, data management, and security for the medicine donation platform. It implements a unique verification-code system to ensure secure and transparent medicine collection and distribution.


**Frontend Repository:** [mds-frontend](https://github.com/mdasimbage786/mds-frontend)

## ✨ Key Features

### Core Functionality
- 🔐 **JWT-based Authentication** - Secure user sessions with role-based access
- 🎫 **Verification Code System** - Unique codes for every donation/application
- 📦 **Inventory Management** - Real-time stock tracking and updates
- ✅ **Approval Workflow** - Code-verified collection and distribution
- 🔄 **Status Tracking** - Real-time updates for all transactions

### Security Features
- 🛡️ Spring Security integration
- 🔑 JWT token generation and validation
- 👤 Role-based authorization (USER, NGO, ADMIN)
- 🔒 Password encryption with BCrypt
- 🚫 Protection against common vulnerabilities

### Data Management
- 💾 JPA/Hibernate ORM
- 🗃️ MySQL (Development) / PostgreSQL (Production)
- ⚡ Optimized queries and relationships
- 🔄 Automatic inventory updates
- 📊 Transaction logging

## 🛠️ Tech Stack

### Core Framework
- **Java** 17
- **Spring Boot** 3.x
- **Spring Data JPA**
- **Spring Security**
- **Spring Web**

### Database
- **MySQL** (Development)
- **PostgreSQL** (Production)
- **H2** (Testing)

### Build & Dependencies
- **Maven** 3.8+
- **Lombok** - Reduce boilerplate code
- **JWT** (io.jsonwebtoken)
- **Validation API**

### Tools
- **Postman** - API Testing
- **IntelliJ IDEA** - Development
- **Git** - Version Control
- **Render** - Cloud Deployment

## 🏗️ System Architecture

```
┌─────────────┐
│   Frontend  │
│  (React)    │
└──────┬──────┘
       │ HTTP/REST
       ▼
┌─────────────────────────┐
│   Spring Boot Backend   │
├─────────────────────────┤
│  • REST Controllers     │
│  • Service Layer        │
│  • Security (JWT)       │
│  • Validation           │
└──────┬──────────────────┘
       │ JPA/Hibernate
       ▼
┌─────────────────┐
│   Database      │
│ MySQL/PostgreSQL│
└─────────────────┘
```

## 🚀 Getting Started

### Prerequisites

- Java JDK 17 or higher
- Maven 3.8+
- MySQL 8.0+ (for local development)
- Git
- Postman (optional, for API testing)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/mdasimbage786/mds-backend.git
   cd mds-backend
   ```

2. **Configure database**
   
   Create a MySQL database:
   ```sql
   CREATE DATABASE medicine_donation_db;
   ```

3. **Update application properties**
   
   Edit `src/main/resources/application.properties`:
   ```properties
   # Database Configuration
   spring.datasource.url=jdbc:mysql://localhost:3306/medicine_donation_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   
   # JPA Configuration
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
   
   # JWT Configuration
   jwt.secret=your-secret-key-min-256-bits
   jwt.expiration=86400000
   
   # Server Configuration
   server.port=8080
   ```

4. **Build the project**
   ```bash
   mvn clean install
   ```

5. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

6. **Verify the server**
   
   Navigate to `http://localhost:8080/api/health` (if health endpoint exists)

## 🗄️ Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Medicines Table
```sql
CREATE TABLE medicines (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    quantity INT NOT NULL,
    expiry_date DATE,
    status VARCHAR(20) DEFAULT 'AVAILABLE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Donations Table
```sql
CREATE TABLE donations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    medicine_name VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    expiry_date DATE,
    verification_code VARCHAR(10) UNIQUE NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### Applications Table
```sql
CREATE TABLE applications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    medicine_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    verification_code VARCHAR(10) UNIQUE NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (medicine_id) REFERENCES medicines(id)
);
```

## 📚 API Documentation

### Authentication Endpoints

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123",
  "role": "USER"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "johndoe",
  "password": "password123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "johndoe",
  "role": "USER"
}
```

### Medicine Donation Endpoints

#### Donate Medicine
```http
POST /api/medicines/donate
Authorization: Bearer {token}
Content-Type: application/json

{
  "medicineName": "Paracetamol",
  "quantity": 100,
  "expiryDate": "2025-12-31"
}

Response:
{
  "id": 1,
  "medicineName": "Paracetamol",
  "quantity": 100,
  "verificationCode": "ABC123XYZ",
  "status": "PENDING"
}
```

#### Get User Donations
```http
GET /api/medicines/donations
Authorization: Bearer {token}
```

### Medicine Application Endpoints

#### Apply for Medicine
```http
POST /api/medicines/apply
Authorization: Bearer {token}
Content-Type: application/json

{
  "medicineId": 1,
  "quantity": 10
}

Response:
{
  "id": 1,
  "medicineName": "Paracetamol",
  "quantity": 10,
  "verificationCode": "XYZ789ABC",
  "status": "PENDING"
}
```

#### Get Available Medicines
```http
GET /api/medicines/available
Authorization: Bearer {token}
```

### NGO Endpoints

#### Get Pending Donations
```http
GET /api/ngo/pending-donations
Authorization: Bearer {token}
```

#### Verify and Approve Donation
```http
POST /api/ngo/verify-donation
Authorization: Bearer {token}
Content-Type: application/json

{
  "donationId": 1,
  "verificationCode": "ABC123XYZ"
}

Response:
{
  "message": "Donation verified and approved successfully",
  "status": "APPROVED"
}
```

#### Get Pending Applications
```http
GET /api/ngo/pending-applications
Authorization: Bearer {token}
```

#### Verify and Approve Application
```http
POST /api/ngo/verify-application
Authorization: Bearer {token}
Content-Type: application/json

{
  "applicationId": 1,
  "verificationCode": "XYZ789ABC"
}
```

### Admin Endpoints

#### Get All Users
```http
GET /api/admin/users
Authorization: Bearer {token}
```

#### Get All NGOs
```http
GET /api/admin/ngos
Authorization: Bearer {token}
```

## 🔒 Security

### JWT Authentication Flow

1. User sends credentials to `/api/auth/login`
2. Server validates and generates JWT token
3. Client stores token (localStorage/sessionStorage)
4. Client sends token in Authorization header for protected routes
5. Server validates token on each request

### Password Security
- Passwords hashed using BCrypt (strength: 12)
- No plain-text password storage
- Secure password validation

### Role-Based Access Control

| Endpoint | USER | NGO | ADMIN |
|----------|------|-----|-------|
| `/api/medicines/donate` | ✅ | ❌ | ✅ |
| `/api/medicines/apply` | ✅ | ❌ | ✅ |
| `/api/ngo/verify-*` | ❌ | ✅ | ✅ |
| `/api/admin/*` | ❌ | ❌ | ✅ |

## 🚀 Deployment

### Environment Configuration

Create `application-prod.properties` for production:

```properties
# Database (PostgreSQL for Production)
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

# JWT
jwt.secret=${JWT_SECRET}
jwt.expiration=86400000

# Server
server.port=${PORT:8080}

# CORS
cors.allowed-origins=${FRONTEND_URL}
```

### Deploy to Render

1. Push code to GitHub
2. Create new Web Service on Render
3. Connect GitHub repository
4. Set environment variables:
   - `DATABASE_URL`
   - `JWT_SECRET`
   - `FRONTEND_URL`
5. Deploy

### Build Command
```bash
mvn clean package -DskipTests
```

### Start Command
```bash
java -jar target/mds-backend-0.0.1-SNAPSHOT.jar
```

## 🧪 Testing

### Run Tests
```bash
mvn test
```

### Test with Postman
1. Import the Postman collection (if provided)
2. Set environment variables (base URL, token)
3. Run test sequences

### Manual Testing Checklist
- [ ] User registration and login
- [ ] JWT token generation and validation
- [ ] Medicine donation with code generation
- [ ] Medicine application with quantity validation
- [ ] NGO verification workflow
- [ ] Inventory updates after approval
- [ ] Role-based access control

## 📂 Project Structure

```
mds-backend/
├── src/
│   ├── main/
│   │   ├── java/com/mds/
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   └── CorsConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── MedicineController.java
│   │   │   │   ├── NGOController.java
│   │   │   │   └── AdminController.java
│   │   │   ├── model/
│   │   │   │   ├── User.java
│   │   │   │   ├── Medicine.java
│   │   │   │   ├── Donation.java
│   │   │   │   └── Application.java
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── MedicineRepository.java
│   │   │   │   ├── DonationRepository.java
│   │   │   │   └── ApplicationRepository.java
│   │   │   ├── service/
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── MedicineService.java
│   │   │   │   ├── NGOService.java
│   │   │   │   └── AdminService.java
│   │   │   ├── security/
│   │   │   │   ├── JwtUtil.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   └── UserDetailsServiceImpl.java
│   │   │   ├── dto/
│   │   │   │   ├── LoginRequest.java
│   │   │   │   ├── RegisterRequest.java
│   │   │   │   └── DonationRequest.java
│   │   │   └── exception/
│   │   │       ├── GlobalExceptionHandler.java
│   │   │       └── CustomExceptions.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-prod.properties
│   └── test/
├── pom.xml
└── README.md
```

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/NewFeature`)
3. Commit your changes (`git commit -m 'Add NewFeature'`)
4. Push to the branch (`git push origin feature/NewFeature`)
5. Open a Pull Request

### Code Style Guidelines
- Follow Java naming conventions
- Write meaningful commit messages
- Add JavaDoc comments for public methods
- Write unit tests for new features

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Mohd Asim Bage**

- GitHub: [@mdasimbage786](https://github.com/mdasimbage786)
- LinkedIn: [LinkedIn Profile](https://www.linkedin.com/in/mohammedasim-bage-4290b22a9)
- Email: mdasimbage786@gmail.com

## 🙏 Acknowledgments

- Spring Boot community for excellent documentation
- All contributors who have helped improve this project
- Inspired by the mission to reduce medicine wastage

## 🔗 Related Links

- [Frontend Repository](https://github.com/mdasimbage786/mds-frontend)
- [Live Demo](https://your-frontend-url.netlify.app)
- [API Documentation](https://your-backend-url.com/swagger-ui.html)

## 📞 Support

If you have any questions or need help, please:
- Open an issue on GitHub
- Contact via email
- Check the documentation

---

⭐ If you found this project useful, please consider giving it a star!
