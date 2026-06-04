# 📖 Complete Code Explanation — Spring Boot MVC App

---

## 🗺️ Big Picture First

Before diving into files, understand this one core idea:

> **The browser never talks to the database directly. Every request goes through 4 layers:**

```
Browser (HTML Form)
    │  HTTP Request (GET / POST)
    ▼
Controller         ← Receives the request, decides what to do
    │  calls
    ▼
Service            ← Does the actual thinking / business logic
    │  calls
    ▼
Repository         ← Talks to the database (SQL)
    │
    ▼
MySQL Database     ← Stores/retrieves the data
    │
    ▼ (data comes back up the same chain)
Controller         ← Puts data into the Model
    │
    ▼
Thymeleaf View     ← Renders the HTML page with that data
    │
    ▼
Browser            ← User sees the result
```

This pattern is called **MVC — Model, View, Controller**. Your whole app is built around this.

---

## 1️⃣ Entry Point — `SpringbootmvcApplication.java`

```java
@SpringBootApplication
@EnableWebMvc
public class SpringbootmvcApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringbootmvcApplication.class, args);
    }
}
```

**What happens when you run this?**

- `SpringApplication.run(...)` boots up an **embedded Apache Tomcat server** (no external Tomcat needed)
- Spring scans your entire package tree for classes annotated with `@Controller`, `@Service`, `@Repository`, `@Configuration` etc., and registers them all as **beans** (managed objects)
- The web server starts listening on port **8080**

**Annotations explained:**
| Annotation | What it does |
|---|---|
| `@SpringBootApplication` | Combines `@Configuration` + `@EnableAutoConfiguration` + `@ComponentScan` |
| `@EnableWebMvc` | Explicitly activates Spring MVC — enables full control over the web layer |

---

## 2️⃣ Database Config — `MySqlConfig.java`

This is the most technically complex file. Normally Spring Boot auto-configures JPA, but here you've done it **manually** — which shows deeper understanding.

```java
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "in.guvi.task.springbootmvc.repository",
    entityManagerFactoryRef = "mysqlEntityManagerFactory",
    transactionManagerRef = "mysqlTransactionManager"
)
public class MySqlConfig { ... }
```

### Why manual config?
Spring Boot's auto-config looks for `spring.datasource.*` in `application.properties`. Here you used a **custom prefix** `app.datasource.mysql.*` — so you have to manually wire everything yourself.

### The 3 beans it creates:

**Bean 1 — DataSourceProperties**
```java
@ConfigurationProperties("app.datasource.mysql")
public DataSourceProperties mysqlDataSourceProperties() {
    return new DataSourceProperties();
}
```
Reads `url`, `username`, `password`, `driver-class-name` from `application.properties` and binds them to a properties object.

**Bean 2 — DataSource**
```java
public DataSource dataSource() {
    return mysqlDataSourceProperties().initializeDataSourceBuilder().build();
}
```
Uses those properties to create a **HikariCP connection pool** — a pool of pre-opened database connections that are reused across requests (much faster than opening a fresh connection every time).

**Bean 3 — EntityManagerFactory**
```java
mysqlProperties.put("hibernate.hbm2ddl.auto", "update");
return builder.dataSource(dataSource)
              .packages("in.guvi.task.springbootmvc.model")
              .persistenceUnit("mysql")
              .properties(mysqlProperties)
              .build();
```
This is the core JPA component. It:
- Scans the `model` package for `@Entity` classes
- On startup with `hbm2ddl.auto=update`, checks if the `Product` and `Feedback` tables exist in MySQL — if not, **creates them automatically**; if they exist but are outdated, **alters them**
- Manages entity state (new, managed, detached, removed)

**Bean 4 — TransactionManager**
```java
return new JpaTransactionManager(entityManagerFactory);
```
Handles `BEGIN TRANSACTION`, `COMMIT`, and `ROLLBACK` for every database operation. Without this, a crash midway through saving data would leave the database in an inconsistent state.

---

## 3️⃣ The Model Layer — `Product.java` & `Feedback.java`

These are **JPA Entities** — Java classes that map directly to database tables.

### `Product.java`
```java
@Entity
@Table(name = "Product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ProductName")
    private String productName;

    @Column(name = "ProductPrice")
    private Double price;

    @Column(name = "Category")
    private String category;
}
```

**What each annotation does:**
| Annotation | Meaning |
|---|---|
| `@Entity` | Tells Hibernate: "This class is a database table" |
| `@Table(name="Product")` | The actual table name in MySQL |
| `@Id` | This field is the Primary Key |
| `@GeneratedValue(IDENTITY)` | MySQL auto-increments this — you never set it manually |
| `@Column(name="ProductName")` | Maps the Java field to a specific column name |

**Lombok annotations** (code-generation, no runtime cost):
| Annotation | What it generates |
|---|---|
| `@Getter` | `getId()`, `getName()`, `getPrice()`, etc. |
| `@Setter` | `setId()`, `setName()`, `setPrice()`, etc. |
| `@NoArgsConstructor` | `new Product()` — required by JPA spec |
| `@AllArgsConstructor` | `new Product(id, name, price, category)` |
| `@Builder` | `Product.builder().productName("TV").price(50000.0).build()` |
| `@ToString` | `product.toString()` → readable output for logging |

`Feedback.java` follows the exact same pattern but stores `name`, `bookName`, and `feedback` text.

---

## 4️⃣ The Repository Layer — `ProductRepository.java` & `FeedbackRepository.java`

```java
@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}
```

This is the **simplest but most powerful** part of the app. You wrote **zero SQL**. Spring Data JPA generates a complete implementation of this interface at runtime. The generic parameters `<Feedback, Long>` tell it: "I manage `Feedback` entities, and their primary key type is `Long`."

**What you get for free:**

| Method | Generated SQL |
|---|---|
| `save(entity)` | `INSERT INTO Feedback (...)` or `UPDATE Feedback SET ... WHERE id=?` |
| `findById(id)` | `SELECT * FROM Feedback WHERE id=?` |
| `findAll()` | `SELECT * FROM Feedback` |
| `deleteById(id)` | `DELETE FROM Feedback WHERE id=?` |
| `count()` | `SELECT COUNT(*) FROM Feedback` |

`@Repository` also enables **exception translation** — raw JDBC exceptions like `SQLException` are wrapped into Spring's cleaner `DataAccessException` hierarchy.

---

## 5️⃣ The DTO Layer — Request & Response DTOs

**Why DTOs? Why not use the Entity directly?**

Using the entity directly in forms is dangerous:
- The form could accidentally set the `id` field (allowing someone to overwrite any record)
- The entity might have fields you don't want exposed in the view
- Validation constraints on the entity would also affect database operations

So you use separate **Request DTOs** (for form input) and **Response DTOs** (for view output).

### `FeedbackRequestDto.java`
```java
@NotNull(message = "Reader name can't be null")
@NotEmpty(message = "Reader name can't be empty")
@NotBlank(message = "Reader name cannot consist of only empty spaces")
@ValidName(message = "Reader name must contain only letters and spaces")
private String name;
```

**Why 3 separate not-null/empty/blank annotations?**
- `@NotNull` → catches `null` (field completely absent)
- `@NotEmpty` → catches `""` (empty string, but not null)
- `@NotBlank` → catches `"   "` (only spaces — passes NotEmpty but fails this)

These compose together for complete coverage.

### `ProductRequestDto` vs `ProductResponseDto`

| | RequestDto | ResponseDto |
|---|---|---|
| Has `id` field? | ❌ No | ✅ Yes |
| Used when? | Submitting a form | Displaying records |
| Why no ID in request? | ID comes from URL path, not form body | ID needed to build edit/delete links |

---

## 6️⃣ Custom Validation — `@ValidName`

This is your own custom constraint annotation — the most advanced piece of the validation system.

### How it works (3-part system):

**Part 1 — The Annotation** (`ValidName.java`)
```java
@Constraint(validatedBy = ValidNameValidator.class)
public @interface ValidName {
    String message() default "Name must contain only letters and spaces...";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```
This is just a *marker* — it declares what validator to use and what error message to show.

**Part 2 — The Validator** (`ValidNameValidator.java`)
```java
public class ValidNameValidator implements ConstraintValidator<ValidName, String> {
    private static final String NAME_REGEX = "^[a-zA-Z]+(\\s[a-zA-Z]+)*$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;  // null is handled by @NotNull
        return value.matches(NAME_REGEX);
    }
}
```

**The Regex explained character by character:**
```
^               → start of string (can't have leading chars)
[a-zA-Z]+       → one or more letters (upper or lower case)
(               → start of optional group
  \s            → exactly one whitespace/space character
  [a-zA-Z]+    → followed by one or more letters
)*              → this group can repeat zero or more times
$               → end of string (can't have trailing chars)
```

| Input | Matches regex? | Result |
|---|---|---|
| `"John"` | ✅ | Valid |
| `"John Doe"` | ✅ | Valid |
| `"Mary Jane Lee"` | ✅ | Valid |
| `"John123"` | ❌ | Invalid — digit |
| `"John@Doe"` | ❌ | Invalid — special char |
| `"John  Doe"` | ❌ | Invalid — double space |
| `" John"` | ❌ | Invalid — leading space |

**Part 3 — Applied on DTO fields:**
```java
@ValidName(message = "Reader name must contain only letters and spaces")
private String name;
```
When `@Valid` is used in the controller, Jakarta Validation finds this annotation, instantiates `ValidNameValidator`, and calls `isValid()` automatically.

---

## 7️⃣ The Service Layer

The service layer is the **brain** of the application. Controllers just route, repositories just store — services do the actual work.

### `FeedbackService.java` — method by method:

**`saveFeedback(dto)`**
```java
Feedback feedback = Feedback.builder()
    .name(dto.getName())
    .bookName(dto.getBookName())
    .feedback(dto.getFeedback())
    .build();
return feedbackRepository.save(feedback);
```
DTO → Entity mapping. Notice: no `id` is set. The database generates it. `save()` issues `INSERT INTO Feedback ...`.

**`displayAllFeedbacks()`**
```java
return feedbackRepository.findAll()
    .stream()
    .map(f -> FeedbackResponseDto.builder()
        .id(f.getId())
        .name(f.getName())
        // ...
        .build())
    .collect(Collectors.toList());
```
Entity → DTO mapping using Java Streams. `findAll()` fetches all rows; `.stream()` processes them one by one; `.map()` converts each `Feedback` entity to a `FeedbackResponseDto`; `.collect(toList())` gathers results back into a list.

**`getFeedbackByIdForUpdate(id)`**
```java
Feedback f = feedbackRepository.findById(id)
    .orElseThrow(() -> new RuntimeException("Not found"));
return FeedbackRequestDto.builder()...build();
```
`findById()` returns `Optional<Feedback>` — not the entity directly. `.orElseThrow()` safely unwraps it: if a record exists, return it; if not, throw an exception. Returns a **RequestDto** (not ResponseDto) because the edit form needs the same structure it would submit.

**`updateFeedback(id, dto)`**
```java
Feedback existing = feedbackRepository.findById(id).orElseThrow(...);
existing.setName(dto.getName());
existing.setBookName(dto.getBookName());
feedbackRepository.save(existing);
```
Fetches the existing record, applies new values, saves. Hibernate detects the entity already has an ID → issues `UPDATE` instead of `INSERT`.

**`deleteFeedback(id)`**
```java
feedbackRepository.deleteById(id);
```
Issues `DELETE FROM Feedback WHERE id = ?`. Silent no-op if ID doesn't exist.

---

## 8️⃣ The Controller Layer

Controllers are the **traffic directors** of the app. They receive HTTP requests and return view names.

### `HomeController.java`

```java
@Controller
@GetMapping("/")
public String homePage() {
    return "homePage";
}
```
The simplest possible controller. When someone visits `http://localhost:8080/`, this returns `"homePage"`, which Thymeleaf resolves to `/templates/homePage.html`.

---

### `FeedbackController.java` — the full CRUD flow:

#### Step 1: Show the empty form
```java
@GetMapping   // GET /feedback
public String formPage(Model model) {
    model.addAttribute("feedback", new FeedbackRequestDto());
    return "feedback/feedbackFormPage";
}
```
An **empty DTO** is added to the model. Thymeleaf uses this as the object that form fields bind to (`th:object="${feedback}"`). Without this, Thymeleaf would throw an error trying to bind `*{name}`.

#### Step 2: Process the form submission
```java
@PostMapping("/saveFeedback")
public String saveFeedback(
    @Valid @ModelAttribute("feedback") FeedbackRequestDto requestDto,
    BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {
        return "feedback/feedbackFormPage";  // Re-show form WITH errors
    }
    feedbackService.saveFeedback(requestDto);
    return "feedback/successPage";
}
```

What happens step by step:
1. Browser submits the form as an HTTP POST with form-encoded body
2. Spring MVC reads the body and populates `requestDto` fields automatically (`@ModelAttribute`)
3. `@Valid` triggers all Bean Validation constraints (`@NotBlank`, `@ValidName`, etc.)
4. `BindingResult` captures any violations — **must come immediately after the DTO parameter**
5. If errors exist → return the same form view (Thymeleaf will show the error messages inline)
6. If clean → save and show success page

#### Step 3: Show edit form pre-filled
```java
@GetMapping("/edit/{id}")
public String editFeedbackPage(@PathVariable Long id, Model model) {
    model.addAttribute("feedback", feedbackService.getFeedbackByIdForUpdate(id));
    model.addAttribute("feedbackId", id);
    return "feedback/updateFeedbackPage";
}
```
`@PathVariable` extracts `id` from the URL (e.g., `/feedback/edit/3` → `id = 3`). The service fetches the record and maps it to a RequestDto (pre-filled). The `feedbackId` is separately added because the update form's POST action URL needs it: `th:action="@{/feedback/edit/{id}(id=${feedbackId})}"`.

#### Step 4: Process the update
```java
@PostMapping("/edit/{id}")
public String updateFeedback(@PathVariable Long id,
    @Valid @ModelAttribute("feedback") FeedbackRequestDto requestDto,
    BindingResult bindingResult, Model model) {

    if (bindingResult.hasErrors()) {
        model.addAttribute("feedbackId", id);  // Re-add ID for form action URL
        return "feedback/updateFeedbackPage";
    }
    feedbackService.updateFeedback(id, requestDto);
    return "redirect:/feedback/display";  // PRG Pattern
}
```

**PRG Pattern** (Post/Redirect/Get): After a successful update, instead of returning a view name directly, you `redirect:` to the GET endpoint. This means:
- If the user presses F5 (refresh), they re-run the GET, not the POST
- Without PRG: refresh → browser says "Resend form data?" → duplicate submit

#### Step 5: Delete
```java
@GetMapping("/delete/{id}")
public String deleteFeedback(@PathVariable Long id) {
    feedbackService.deleteFeedback(id);
    return "redirect:/feedback/display";
}
```
Uses a GET for delete because HTML `<a>` links can only make GET requests. After deletion, redirects to the list.

---

## 9️⃣ The View Layer — Thymeleaf Templates

Thymeleaf is a **server-side** template engine. The HTML is processed on the server before being sent to the browser. All `th:*` attributes are processed and stripped — the browser receives plain HTML.

### Key Thymeleaf attributes used:

| Attribute | Example | What it does |
|---|---|---|
| `xmlns:th` | `<html xmlns:th="...">` | Declares the Thymeleaf namespace |
| `th:object` | `th:object="${feedback}"` | Binds the form to a model attribute |
| `th:field` | `th:field="*{name}"` | Binds an input to a field of `th:object` — sets `name`, `id`, and `value` |
| `th:text` | `th:text="${prod.price}"` | Sets the text content of an element |
| `th:href` | `th:href="@{/feedback/edit/{id}(id=${f.id})}"` | Generates a dynamic URL |
| `th:if` | `th:if="${#fields.hasErrors('name')}"` | Conditional rendering |
| `th:errors` | `th:errors="*{name}"` | Shows validation error message for a field |
| `th:each` | `th:each="f : ${feedbacks}"` | Loops over a list |
| `@{...}` | `@{/product/addProduct}` | URL expression — handles context path automatically |
| `${...}` | `${prod.price}` | Variable expression — reads from model |
| `*{...}` | `*{name}` | Selection expression — reads from `th:object` bound object |

### `displayProductsPage.html` — the loop explained:
```html
<tr th:each="prod : ${products}" class="hover:bg-gray-50">
    <td th:text="${prod.id}"></td>
    <td th:text="${prod.productName}"></td>
    <td th:text="${prod.category}"></td>
    <td th:text="'₹' + ${prod.price}"></td>
    <td>
        <a th:href="@{/product/edit/{id}(id=${prod.id})}">Edit</a>
        <a th:href="@{/product/delete/{id}(id=${prod.id})}"
           onclick="return confirm('Are you sure?')">Delete</a>
    </td>
</tr>
```
- `th:each="prod : ${products}"` → for each `ProductResponseDto` in the `products` list, render this `<tr>`
- `'₹' + ${prod.price}` → string concatenation in Thymeleaf — prefixes ₹ to the price value
- `@{/product/edit/{id}(id=${prod.id})}` → generates `/product/edit/3` (the `{id}` placeholder is filled by the `(id=...)` parameter)
- `onclick="return confirm(...)"` → plain JavaScript confirmation dialog before delete

### Empty state handling:
```html
<div th:if="${#lists.isEmpty(products)}">
    No products found in the database.
</div>
```
`#lists.isEmpty()` is a Thymeleaf utility method. If no products exist, this div is shown instead of an empty table.

---

## 🔟 Exception Handling — `GlobalExceptionHandler.java`

```java
@RestControllerAdvice
public class GlobalExceptionHandler { ... }
```

`@RestControllerAdvice` is an **AOP (Aspect-Oriented Programming)** mechanism. It intercepts exceptions thrown from any controller without you having to write try-catch anywhere. All error responses follow the same structure using `ErrorResponseDto`:

```json
{
  "timestamp": "2026-06-04T10:30:00",
  "status": 404,
  "error": "Resource Not Found",
  "message": "Product not found with id: 99"
}
```

### The handler priority chain:

```
Exception thrown
    │
    ├── Is it ResourceNotFoundException?      → 404 Not Found
    ├── Is it DataIntegrityViolationException? → 409 Conflict
    ├── Is it MethodArgumentNotValidException? → 400 Bad Request
    ├── Is it MethodArgumentTypeMismatchException? → 400 Bad Request
    ├── Is it NoHandlerFoundException?         → 404 (plain string)
    └── Is it Exception (anything else)?       → 500 Internal Server Error
```

**Security point:** The catch-all `Exception` handler logs the full stack trace internally but returns only a generic message to the client. This prevents leaking implementation details.

**The DRY helper:**
```java
private ResponseEntity<ErrorResponseDto> buildErrorResponse(
        Exception ex, HttpStatus status, String customErrorTitle) {
    // Builds the ErrorResponseDto in one place
}
```
Without this, each handler would repeat the same builder code 5 times.

---

## 🔁 Complete Request Flow — Example: "User adds a product"

```
1. User visits GET /product/addProduct
   → ProductController.addProductPage()
   → Adds empty ProductRequestDto to model
   → Returns "product/addProductPage" view
   → Thymeleaf renders addProductPage.html with the empty form

2. User fills in "Television", ₹45000, "Electronics" and clicks "Save Product"
   → Browser sends POST /product/addProduct
   → ProductController.addProduct() receives request

3. @Valid triggers validation:
   - @NotBlank on productName → "Television" ✅
   - @ValidName on productName → "Television" matches regex ✅
   - @NotNull + @Min(0) on price → 45000.0 ✅
   - @NotBlank on category → "Electronics" ✅
   → BindingResult.hasErrors() == false → no errors

4. productService.saveProduct(requestDto) is called
   → Maps DTO → Product entity (no ID set)
   → productRepository.save(entity)
   → Hibernate generates: INSERT INTO Product (ProductName, ProductPrice, Category) VALUES (?, ?, ?)
   → MySQL inserts the row, assigns id=1 (auto-increment)
   → Saved Product entity returned (now has id=1)

5. Controller returns "redirect:/product/displayProduct"
   → Browser receives HTTP 302 redirect
   → Browser automatically sends GET /product/displayProduct

6. ProductController.displayAllProducts() runs
   → productService.displayProducts()
   → productRepository.findAll()
   → SELECT * FROM Product → returns [Product(id=1, "Television", 45000.0, "Electronics")]
   → Mapped to [ProductResponseDto(id=1, ...)]
   → Added to model as "products"
   → Returns "product/displayProductsPage"
   → Thymeleaf loops over products list, renders the table row
   → User sees "Television | Electronics | ₹45000.0" in the table
```

---

## 🎓 Key Concepts Summary

| Concept | Where Used | What It Solves |
|---|---|---|
| **MVC Pattern** | Entire app | Separation of concerns |
| **DTO Pattern** | Request/Response DTOs | Isolates API from database model |
| **Builder Pattern** | Lombok `@Builder` | Clean object construction without giant constructors |
| **Repository Pattern** | Spring Data JPA | Zero-SQL database access |
| **PRG Pattern** | Controller redirects | Prevents duplicate form submission on refresh |
| **Bean Validation** | `@Valid`, `@NotBlank` etc. | Input validation before it reaches the database |
| **Custom Validation** | `@ValidName` | Reusable, declarative constraint annotations |
| **Global Exception Handling** | `@RestControllerAdvice` | Centralized, consistent error responses |
| **DRY Principle** | `buildErrorResponse()` helper | Avoids code duplication |
| **Optional pattern** | `findById().orElseThrow()` | Safe null-handling without NullPointerException |
| **Java Streams** | Service layer mapping | Declarative, functional collection transformation |
