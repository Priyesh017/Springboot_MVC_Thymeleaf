# 📦 Spring Boot MVC — Product & Feedback Management App

> **Assignment Submission** | GUVI Task | Spring Boot 4.x · Thymeleaf · MySQL · JPA

---

## 📋 Overview

This is a **full-stack web application** built as a GUVI assignment to demonstrate the Spring Boot MVC architecture. The application provides two independent CRUD modules — a **Product Catalog** and a **Book Feedback System** — served through server-side rendered HTML pages using the **Thymeleaf** templating engine backed by a **MySQL** database.

---

## 🧰 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0.6 |
| Web Layer | Spring MVC (`@Controller`, Thymeleaf) |
| Persistence | Spring Data JPA + Hibernate |
| Database | MySQL 8+ |
| Validation | Jakarta Bean Validation (`@Valid`, `@NotBlank`, custom `@NoSpaces`) |
| Boilerplate Reduction | Lombok (`@Builder`, `@Data`, `@RequiredArgsConstructor`) |
| Build Tool | Apache Maven |
| Dev Tools | Spring Boot DevTools (hot reload) |

---

## 🏗️ Project Architecture

The project follows a classic **Layered MVC Architecture**:

```
Browser / HTML Form
       │
       ▼
  [ Controller ]       ← Handles HTTP requests, delegates to Service
       │
       ▼
  [  Service  ]        ← Business logic, DTO ↔ Entity mapping
       │
       ▼
  [ Repository ]       ← Spring Data JPA — auto-generated CRUD
       │
       ▼
  [  Database  ]       ← MySQL (tables: Product, Feedback)
```

---

## 📁 Project Structure

```
src/main/java/in/guvi/task/springbootmvc/
│
├── SpringbootmvcApplication.java          # Application entry point (@SpringBootApplication)
│
├── config/
│   └── MySqlConfig.java                   # Manual JPA stack wiring (DataSource, EntityManagerFactory, TxManager)
│
├── controller/
│   ├── HomeController.java                # GET "/" → renders the dashboard home page
│   ├── ProductController.java             # Full CRUD endpoints for /product/**
│   └── FeedbackController.java            # Full CRUD endpoints for /feedback/**
│
├── service/
│   ├── ProductService.java                # Product business logic + DTO/entity mapping
│   └── FeedbackService.java               # Feedback business logic + DTO/entity mapping
│
├── repository/
│   ├── ProductRepository.java             # JpaRepository<Product, Long>
│   └── FeedbackRepository.java            # JpaRepository<Feedback, Long>
│
├── model/
│   ├── Product.java                       # @Entity mapped to "Product" table
│   └── Feedback.java                      # @Entity mapped to "Feedback" table
│
├── dto/
│   ├── ProductRequestDto.java             # Incoming form data for Product (no ID)
│   ├── ProductResponseDto.java            # Outgoing product data for views (includes ID)
│   ├── FeedbackRequestDto.java            # Incoming form data for Feedback (no ID)
│   ├── FeedbackResponseDto.java           # Outgoing feedback data for views (includes ID)
│   └── ErrorResponseDto.java             # Standardized error payload for exception responses
│
├── exception/
│   ├── GlobalExceptionHandler.java        # @RestControllerAdvice — centralized exception handling
│   └── ResourceNotFoundException.java     # Custom RuntimeException for 404 scenarios
│
└── validations/
    ├── NoSpacesValidator.java             # ConstraintValidator logic for @NoSpaces
    └── annotations/
        └── NoSpaces.java                  # Custom constraint annotation definition

src/main/resources/
├── application.properties                 # MySQL connection properties (custom prefix)
├── application.yaml                       # Spring/Thymeleaf/JPA configuration
└── templates/
    ├── homePage.html                      # Dashboard page with links to both modules
    ├── product/
    │   ├── addProductPage.html            # Form to add a new product
    │   ├── displayProductsPage.html       # Table listing all products with edit/delete actions
    │   └── updateProductPage.html         # Form to edit an existing product
    └── feedback/
        ├── feedbackFormPage.html          # Form to submit new feedback
        ├── displayFeedbacksPage.html      # Table listing all feedbacks with edit/delete actions
        ├── updateFeedbackPage.html        # Form to edit an existing feedback
        └── successPage.html              # Confirmation page shown after successful submission
```

---

## 🔁 Application Workflow

### Home Page (`GET /`)
The user lands on a dashboard that presents two navigation cards — one for the **Product Catalog** and one for the **Feedback System**.

---

### 🛍️ Product Module (`/product/**`)

| HTTP Method | URL | Description |
|---|---|---|
| `GET` | `/product/displayProduct` | Lists all products in a table |
| `GET` | `/product/addProduct` | Renders the blank "Add Product" form |
| `POST` | `/product/addProduct` | Validates and saves the new product |
| `GET` | `/product/edit/{id}` | Renders the edit form pre-filled with existing data |
| `POST` | `/product/edit/{id}` | Validates and updates the product |
| `GET` | `/product/delete/{id}` | Deletes the product and redirects to the list |

**Product Fields:** `productName` (String), `price` (Double ≥ 0), `category` (String)

---

### 💬 Feedback Module (`/feedback/**`)

| HTTP Method | URL | Description |
|---|---|---|
| `GET` | `/feedback` | Renders the blank feedback submission form |
| `POST` | `/feedback/saveFeedback` | Validates and saves new feedback |
| `GET` | `/feedback/display` | Lists all feedback entries in a table |
| `GET` | `/feedback/edit/{id}` | Renders the edit form pre-filled with existing feedback |
| `POST` | `/feedback/edit/{id}` | Validates and updates the feedback |
| `GET` | `/feedback/delete/{id}` | Deletes the feedback and redirects to the list |

**Feedback Fields:** `name` (Reader Name, String), `bookName` (String), `feedback` (Review text, String)

---

## ✅ Validation Strategy

Validation is applied at multiple levels:

- **Standard constraints** via Jakarta Bean Validation on DTO fields:
  - `@NotNull` — rejects null values
  - `@NotEmpty` — rejects empty strings
  - `@NotBlank` — rejects whitespace-only strings
  - `@Min(0)` — ensures price is non-negative
- **Custom annotation** `@NoSpaces` — a bespoke constraint that rejects strings containing space characters, implemented via `NoSpacesValidator`.
- **Controller-level enforcement** — `@Valid` on `@ModelAttribute` parameters triggers all constraints. Errors are captured by `BindingResult` and displayed inline on the form without a redirect.

---

## ⚠️ Error Handling

The `GlobalExceptionHandler` class (annotated `@RestControllerAdvice`) intercepts exceptions application-wide and returns structured JSON error responses:

```json
{
  "timestamp": "2026-06-04T10:30:00",
  "status": 404,
  "error": "Resource Not Found",
  "message": "Product not found with id: 99"
}
```

| Exception | HTTP Status | Trigger |
|---|---|---|
| `ResourceNotFoundException` | 404 | Resource not found in DB |
| `DataIntegrityViolationException` | 409 | Duplicate/missing FK in DB |
| `MethodArgumentNotValidException` | 400 | Bean Validation failure |
| `MethodArgumentTypeMismatchException` | 400 | Wrong type in URL path variable |
| `NoHandlerFoundException` | 404 | Unknown URL (e.g., /favicon.ico) |
| `Exception` (catch-all) | 500 | Unexpected server errors |

---

## 🗄️ Database Configuration

The application uses a **manually configured** JPA stack (instead of Spring Boot's auto-configuration) to explicitly demonstrate the wiring of DataSource → EntityManagerFactory → TransactionManager.

**`application.properties`** (Custom prefix `app.datasource.mysql`):
```properties
app.datasource.mysql.url=jdbc:mysql://localhost:3306/guvi_task
app.datasource.mysql.username=root
app.datasource.mysql.password=
app.datasource.mysql.driver-class-name=com.mysql.cj.jdbc.Driver
```

**`application.yaml`**:
```yaml
spring:
  thymeleaf:
    cache: false
  jpa:
    database-platform: org.hibernate.dialect.MySQLDialect
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

Hibernate is configured with `hbm2ddl.auto=update`, which automatically creates or updates the database tables based on the entity class definitions on startup.

---

## ⚙️ How to Run

### Prerequisites
- Java 21+
- Maven 3.8+
- MySQL 8+ running locally
- A database named `guvi_task` created in MySQL

### Steps

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd springbootmvc
   ```

2. **Configure MySQL credentials** in `src/main/resources/application.properties`:
   ```properties
   app.datasource.mysql.url=jdbc:mysql://localhost:3306/guvi_task
   app.datasource.mysql.username=root
   app.datasource.mysql.password=your_password
   ```

3. **Create the database** (if it doesn't exist):
   ```sql
   CREATE DATABASE guvi_task;
   ```

4. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```
   Or on Windows:
   ```cmd
   mvnw.cmd spring-boot:run
   ```

5. **Open in browser**: [http://localhost:8080](http://localhost:8080)

> Tables (`Product`, `Feedback`) are auto-created by Hibernate on first run.

---

## 🔑 Key Design Patterns Used

| Pattern | Where Applied |
|---|---|
| **MVC (Model-View-Controller)** | Entire application architecture |
| **DTO Pattern** | Separate Request/Response DTOs isolate the API from the domain model |
| **Builder Pattern** | Lombok `@Builder` used for clean entity and DTO construction |
| **Repository Pattern** | Spring Data JPA repositories abstract all database access |
| **PRG (Post/Redirect/Get)** | After successful POST, a redirect prevents form re-submission on refresh |
| **DRY Principle** | `buildErrorResponse()` helper in `GlobalExceptionHandler` centralizes error construction |

---

## 👤 Author

**Priyesh** | GUVI Assignment — Spring Boot MVC with Thymeleaf
