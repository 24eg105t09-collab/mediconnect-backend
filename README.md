# 🏥 MediConnect AI — Backend (Spring Boot + MySQL)

The backend service for **MediConnect AI** (Hospital Management & Doctor Availability Assistant).

## 🛠️ Tech Stack
- **Language:** Java 21 (LTS)
- **Framework:** Spring Boot 3.3.4
- **ORM / Persistence:** Spring Data JPA + Hibernate
- **Database:** MySQL 8.0
- **Build Tool:** Maven (Maven Wrapper included)

## ⚙️ Configuration
Configure MySQL credentials in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mediconnect_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=jaidev
server.port=8080
```

## 🚀 Running the Backend
Run using the Maven Wrapper:

**Windows (PowerShell / Command Prompt):**
```bash
.\mvnw.cmd spring-boot:run
```

**Linux / macOS:**
```bash
./mvnw spring-boot:run
```

The API will be available at: **http://localhost:8080**

## 📡 Key REST API Endpoints
- `POST /api/auth/login` - Authenticate users
- `POST /api/auth/register` - Register patient/doctor
- `GET /api/doctors` - All certified active doctors
- `GET /api/doctors/search?query=` - Search doctors
- `GET /api/departments` - List medical departments
- `GET /api/appointments/slots?doctorId={id}&date={YYYY-MM-DD}` - Real-time slot availability
- `POST /api/appointments/book` - Book appointment (with queue & wait time calculation)
- `PUT /api/appointments/{id}/cancel` - Cancel appointment and release slot
- `POST /api/chatbot/message` - AI Doctor availability assistant & symptom analysis
- `GET /api/admin/stats` - Hospital metrics & analytics
