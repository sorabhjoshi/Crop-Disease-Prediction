# SECONDARY MASTER PROMPT
## Crop Pest Predictor — Complete Rebuild Guide for Any AI

Use this prompt to rebuild the entire Crop Pest Predictor application from scratch.
Paste it into any capable AI (Claude, GPT-4, DeepSeek, Gemini, etc.).

---

## 1. PROJECT OVERVIEW

Build a **production-ready, desktop Java application** called **Crop Pest Predictor**.

It predicts agricultural pest risk (5 classes: Very Low → Very High) using a pre-trained neural network.

### Technology Stack (ALL mandatory, no substitutions)

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Backend framework | Spring Boot 3.2 |
| ORM | Hibernate 6 (JPA) |
| JDBC | Raw JDBC (alongside Hibernate) |
| Database | MySQL 8 |
| Frontend | JavaFX + WebView (HTML pages) |
| Routing | Java Servlets (MANDATORY — all routes go through servlets) |
| Architecture | MVC (strict separation) |
| ML integration | Python Flask 3.0 REST API |
| ML model | TensorFlow/Keras .h5 + scikit-learn .pkl scaler |
| Build tool | Maven |
| IDE target | Apache NetBeans 19+ |

---

## 2. ARCHITECTURE EXPLANATION (MVC)

```
User (JavaFX WebView)
       ↓  HTTP
AuthFilter (Servlet Filter — checks session)
       ↓
Servlet (LoginServlet / RegisterServlet / PredictServlet / etc.)
       ↓
Service Layer (UserService / PredictionService)
       ↓
DAO Layer (UserDaoImpl / PredictionHistoryDaoImpl)
       ↓
Hibernate SessionFactory → MySQL

PredictionService also calls:
       ↓
Python Flask API (localhost:5000/predict)
       ↓
Keras model (.h5) + StandardScaler (.pkl)
```

The JavaFX window hosts a WebView that loads `http://localhost:8080`.
Spring Boot runs embedded Tomcat.
All servlets route requests to HTML views in `/WEB-INF/views/`.
REST JSON endpoints (`/api/*`) serve data to the frontend JavaScript.

---

## 3. EXACT FOLDER STRUCTURE

```
CropPestPredictor/
├── pom.xml
├── README.md
├── sql/
│   └── schema.sql
├── python_api/
│   ├── app.py
│   ├── requirements.txt
│   └── model_files/
│       ├── pest_nn_model.h5
│       ├── scaler_nn.pkl
│       └── selected_features.pkl
└── src/
    └── main/
        ├── java/com/pestpredictor/
        │   ├── MainApp.java
        │   ├── controller/
        │   │   ├── ApiController.java
        │   │   └── SecurityConfig.java
        │   ├── dao/
        │   │   ├── UserDao.java              (interface)
        │   │   ├── UserDaoImpl.java          (Hibernate impl)
        │   │   ├── PredictionHistoryDao.java (interface)
        │   │   └── PredictionHistoryDaoImpl.java
        │   ├── model/
        │   │   ├── User.java                 (@Entity)
        │   │   ├── PredictionHistory.java    (@Entity)
        │   │   ├── PredictionRequest.java    (DTO - 14 inputs)
        │   │   └── PredictionResult.java     (DTO - ML output)
        │   ├── service/
        │   │   ├── UserService.java
        │   │   └── PredictionService.java
        │   ├── servlet/
        │   │   ├── LoginServlet.java         (@WebServlet("/login"))
        │   │   ├── RegisterServlet.java      (@WebServlet("/register"))
        │   │   ├── LogoutServlet.java        (@WebServlet("/logout"))
        │   │   ├── DashboardServlet.java     (@WebServlet("/dashboard"))
        │   │   ├── PredictServlet.java       (@WebServlet({"/predict","/predict/result"}))
        │   │   ├── HistoryServlet.java       (@WebServlet("/history"))
        │   │   ├── ErrorServlet.java         (@WebServlet("/error"))
        │   │   └── AuthFilter.java           (@WebFilter("/*"))
        │   ├── util/
        │   │   ├── HibernateUtil.java        (SessionFactory singleton)
        │   │   ├── JdbcUtil.java             (raw JDBC connection helper)
        │   │   └── SessionUtil.java          (HTTP session management)
        │   └── view/
        │       └── JavaFXApp.java            (JavaFX Application subclass)
        ├── resources/
        │   ├── application.properties
        │   └── hibernate.cfg.xml
        └── webapp/
            ├── static/
            │   └── styles.css
            └── WEB-INF/
                └── views/
                    ├── login.html
                    ├── register.html
                    ├── dashboard.html
                    ├── predict.html
                    ├── result.html
                    ├── history.html
                    └── error.html
```

---

## 4. STEP-BY-STEP IMPLEMENTATION PLAN

### Phase 1: Project Setup
1. Create Maven project with groupId `com.pestpredictor`, artifactId `CropPestPredictor`
2. Add all dependencies to pom.xml (see Section 8)
3. Create MySQL database with schema (see Section 7)
4. Configure application.properties and hibernate.cfg.xml

### Phase 2: Model Layer
1. Create `User` @Entity with fields: id, username, email, passwordHash, fullName, role, active, createdAt, lastLogin
2. Create `PredictionHistory` @Entity with all 14 input fields + predicted_class, predicted_label, confidence_score, probabilities, created_at
3. Create `PredictionRequest` DTO (14 doubles matching notebook inputs)
4. Create `PredictionResult` DTO (predictedClass, predictedLabel, confidenceScore, probabilities list)
5. Create `HibernateUtil` singleton that loads hibernate.cfg.xml
6. Create `JdbcUtil` with getConnection() using DriverManager
7. Create `SessionUtil` with static methods: setLoggedInUser, getLoggedInUser, isLoggedIn, invalidateSession, setFlashMessage, consumeFlashMessage

### Phase 3: DAO Layer
1. `UserDao` interface: save, findById, findByUsername, findByEmail, existsByUsername, existsByEmail, updateLastLogin
2. `UserDaoImpl`: implement all using Hibernate Session (open session → begin tx → operate → commit → close)
3. `PredictionHistoryDao` interface: save, findByUserId, findByUserId(limit), countByUserId
4. `PredictionHistoryDaoImpl`: implement all using Hibernate Session

### Phase 4: Service Layer
1. `UserService`: inject UserDao + BCryptPasswordEncoder. Methods: register (validate, hash, save), authenticate (lookup, BCrypt verify, update lastLogin)
2. `PredictionService`: inject PredictionHistoryDao + Java HttpClient. Methods: predict (POST to Flask API, parse JSON), savePrediction (map DTO to entity, persist), getHistoryByUser, countByUser

### Phase 5: Servlet Layer
1. `AuthFilter` — @WebFilter("/*") — allow public paths (/login, /register, /error, /static), redirect unauthenticated to /login
2. `LoginServlet` — GET: show login.html; POST: call UserService.authenticate, set session, redirect to /dashboard
3. `RegisterServlet` — GET: show register.html; POST: validate, call UserService.register, redirect to /login
4. `LogoutServlet` — GET/POST: invalidate session, redirect to /login
5. `DashboardServlet` — GET (auth required): load recent 5 predictions, forward to dashboard.html
6. `PredictServlet` — GET: show predict.html; POST: extract 14 params, validate ranges, call PredictionService.predict, save, store result in session, redirect to /predict/result
7. `HistoryServlet` — GET: load full history, forward to history.html
8. `ErrorServlet` — GET/POST: read error attributes, forward to error.html

### Phase 6: REST API Controller
Create `ApiController` (@RestController, @RequestMapping("/api")):
- GET /api/dashboard — returns JSON: username, totalPredictions, highRiskCount, lowRiskCount, lastPredictionDate, recentPredictions[]
- GET /api/last-result — returns JSON from session attribute "lastResult"
- GET /api/history — returns full history JSON array
- GET /api/health — returns {status: UP}

### Phase 7: JavaFX View
Create `JavaFXApp extends Application`:
- `start(Stage)`: build nav bar (Back, Forward, Refresh, Home buttons) + WebView center
- Use a background thread to poll http://localhost:8080/health until server is ready
- Then load http://localhost:8080/login into the WebView
- Window: 1280×800, resizable, dark theme

### Phase 8: Main Entry Point
`MainApp` (@SpringBootApplication, @ServletComponentScan):
- main(): SpringApplication.run(...) then Application.launch(JavaFXApp.class, args)

### Phase 9: Python Flask API
`python_api/app.py`:
- Load artifacts on first request (lazy loading): load_model(.h5), joblib.load(scaler), joblib.load(features)
- POST /predict: parse JSON body → run create_features() → select features in order → scaler.transform → model.predict → return JSON
- Replicate create_features() EXACTLY from the notebook (see Section 6)

### Phase 10: HTML Views
All 7 pages use a shared dark design system (CSS variables: --bg-deep #0f172a, --bg-card #1e293b, --green #22c55e).
Dashboard/Predict/History/Result pages call /api/* endpoints via JavaScript fetch().

---

## 5. CODE GENERATION INSTRUCTIONS

### For each Servlet, follow this pattern:
```java
@WebServlet("/path")
public class XServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // 1. Check auth: if (!SessionUtil.isLoggedIn(req)) { res.sendRedirect("/login"); return; }
        // 2. Load data from service
        // 3. Set request attributes
        // 4. Forward to view: req.getRequestDispatcher("/WEB-INF/views/page.html").forward(req, res);
    }
    @Override
    protected void doPost(...) { /* validate → service → redirect */ }
}
```

### For each DAO method, follow this pattern:
```java
public User save(User user) {
    Transaction tx = null;
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
        tx = session.beginTransaction();
        session.persist(user);
        tx.commit();
        return user;
    } catch (Exception e) {
        if (tx != null) tx.rollback();
        throw new RuntimeException("Failed to save user", e);
    }
}
```

### For PredictionService.predict(), call Flask:
```java
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("http://localhost:5000/predict"))
    .header("Content-Type", "application/json")
    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
    .timeout(Duration.ofSeconds(30))
    .build();
HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
// parse response JSON with Jackson ObjectMapper
```

---

## 6. ML INTEGRATION — EXACT PREPROCESSING

The Python Flask API MUST replicate this feature engineering exactly (from run_file.ipynb):

```python
def create_features(data):
    eps = 1e-6
    return {
        'moist_temp_proxy':       data['soil_moisture'] * data['temperature'],
        'wind_speed':             data['wind_speed'],
        'water_usage_efficiency': data['water_usage_efficiency'],
        'sfi':                    data['organic_matter'] * (data['N'] + data['P'] + data['K']),
        'overwater_index':        data['irrigation_frequency'] * data['rainfall'],
        'temperature':            data['temperature'],
        'nbr':                    data['N'] / (data['P'] + data['K'] + eps),
        'pp':                     (data['sunlight_exposure'] * data['co2_concentration']) / (data['temperature'] + eps),
        'sunlight_exposure':      data['sunlight_exposure'],
        'fert_heat_index':        data['fertilizer_usage'] * data['temperature'],
    }
```

Input mapping from Java JSON body → Python dict keys:
- temperature → temperature
- humidity → humidity (not used in features, but required as input)
- wind_speed → wind_speed
- N → N
- P → P
- K → K
- organic_matter → organic_matter
- soil_moisture → soil_moisture
- rainfall → rainfall
- irrigation_frequency → irrigation_frequency
- water_usage_efficiency → water_usage_efficiency
- sunlight_exposure → sunlight_exposure
- co2_concentration → co2_concentration
- fertilizer_usage → fertilizer_usage

Feature selection order: use `selected_features.pkl` (loaded via joblib) to pick and order the 10 engineered features.

Output classes:
- 0 = Very Low Pest Risk (0–25%)
- 1 = Low Pest Risk (25–50%)
- 2 = Moderate Pest Risk (50–65%)
- 3 = High Pest Risk (65–80%)
- 4 = Very High Pest Risk (80–100%)

Flask JSON response format:
```json
{
  "predicted_class": 0,
  "predicted_label": "Very Low Pest Risk (0-25%)",
  "confidence": 0.6298,
  "probabilities": [0.6298, 0.1977, 0.0887, 0.0093, 0.0745]
}
```

---

## 7. DATABASE SCHEMA (SQL)

```sql
CREATE DATABASE IF NOT EXISTS pest_predictor CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE pest_predictor;

CREATE TABLE IF NOT EXISTS users (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    username      VARCHAR(50)  NOT NULL,
    email         VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(100),
    role          VARCHAR(20)  NOT NULL DEFAULT 'USER',
    active        TINYINT(1)   NOT NULL DEFAULT 1,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login    DATETIME,
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS prediction_history (
    id                     BIGINT  NOT NULL AUTO_INCREMENT,
    user_id                BIGINT  NOT NULL,
    temperature            DOUBLE,
    humidity               DOUBLE,
    wind_speed             DOUBLE,
    nitrogen               DOUBLE,
    phosphorus             DOUBLE,
    potassium              DOUBLE,
    organic_matter         DOUBLE,
    soil_moisture          DOUBLE,
    rainfall               DOUBLE,
    irrigation_frequency   DOUBLE,
    water_usage_efficiency DOUBLE,
    sunlight_exposure      DOUBLE,
    co2_concentration      DOUBLE,
    fertilizer_usage       DOUBLE,
    predicted_class        INT,
    predicted_label        VARCHAR(100),
    confidence_score       DOUBLE,
    probabilities          VARCHAR(500),
    notes                  VARCHAR(500),
    created_at             DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_ph_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_ph_user (user_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

## 8. MAVEN DEPENDENCIES (pom.xml)

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.0</version>
</parent>

<properties>
    <java.version>17</java.version>
    <javafx.version>21.0.1</javafx.version>
</properties>

<dependencies>
    <!-- Spring Boot -->
    <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-web</artifactId></dependency>
    <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-data-jpa</artifactId></dependency>
    <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-security</artifactId></dependency>
    <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-validation</artifactId></dependency>

    <!-- MySQL -->
    <dependency><groupId>com.mysql</groupId><artifactId>mysql-connector-j</artifactId><scope>runtime</scope></dependency>

    <!-- Hibernate -->
    <dependency><groupId>org.hibernate.orm</groupId><artifactId>hibernate-core</artifactId><version>6.4.1.Final</version></dependency>

    <!-- JavaFX -->
    <dependency><groupId>org.openjfx</groupId><artifactId>javafx-controls</artifactId><version>21.0.1</version></dependency>
    <dependency><groupId>org.openjfx</groupId><artifactId>javafx-fxml</artifactId><version>21.0.1</version></dependency>
    <dependency><groupId>org.openjfx</groupId><artifactId>javafx-web</artifactId><version>21.0.1</version></dependency>
    <dependency><groupId>org.openjfx</groupId><artifactId>javafx-swing</artifactId><version>21.0.1</version></dependency>

    <!-- Jackson -->
    <dependency><groupId>com.fasterxml.jackson.core</groupId><artifactId>jackson-databind</artifactId></dependency>

    <!-- BCrypt -->
    <dependency><groupId>org.springframework.security</groupId><artifactId>spring-security-crypto</artifactId></dependency>

    <!-- Jakarta Servlet -->
    <dependency><groupId>jakarta.servlet</groupId><artifactId>jakarta.servlet-api</artifactId><version>6.0.0</version><scope>provided</scope></dependency>

    <!-- HTTP Client (Java 11+, built-in, no extra dep needed) -->
    <!-- Lombok (optional) -->
    <dependency><groupId>org.projectlombok</groupId><artifactId>lombok</artifactId><optional>true</optional></dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <mainClass>com.pestpredictor.MainApp</mainClass>
            </configuration>
        </plugin>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.11.0</version>
            <configuration><source>17</source><target>17</target></configuration>
        </plugin>
    </plugins>
</build>
```

---

## 9. NETBEANS SETUP GUIDE

1. File > Open Project > navigate to CropPestPredictor folder > Open
2. Wait for Maven to download dependencies (bottom progress bar)
3. Right-click project > Properties > Run:
   - Main Class: `com.pestpredictor.MainApp`
   - VM Options: `--module-path /path/to/javafx-sdk-21/lib --add-modules javafx.controls,javafx.fxml,javafx.web,javafx.swing`
4. Right-click project > Clean and Build
5. Right-click project > Run

If JavaFX is bundled via Maven (OpenJFX), no extra module-path is needed — Maven resolves it automatically.

---

## 10. HOW TO RUN THE COMPLETE APPLICATION

Order of startup matters:

```
Step 1: Start MySQL (should be running as a service)

Step 2: (First time only) Run SQL schema:
  mysql -u root -p < sql/schema.sql

Step 3: Start Python Flask API:
  cd python_api
  pip install -r requirements.txt    # first time only
  python app.py
  # Wait for: "Starting Flask API on port 5000..."

Step 4: Run Java application:
  # In NetBeans: Right-click > Run
  # Or: mvn spring-boot:run
  # JavaFX window opens and loads login page
```

---

## 11. TESTING STEPS

### Unit test: Database
```sql
USE pest_predictor;
SELECT * FROM users;
SELECT * FROM prediction_history;
```

### Unit test: Python API
```bash
curl -X POST http://localhost:5000/predict \
  -H "Content-Type: application/json" \
  -d '{"temperature":26,"humidity":60,"wind_speed":12,"N":55,"P":35,"K":40,"organic_matter":3.5,"soil_moisture":16,"rainfall":80,"irrigation_frequency":2,"water_usage_efficiency":2.8,"sunlight_exposure":6,"co2_concentration":390,"fertilizer_usage":90}'
# Expected: predicted_class: 0 (Very Low Risk)

curl -X POST http://localhost:5000/predict \
  -H "Content-Type: application/json" \
  -d '{"temperature":30,"humidity":70,"wind_speed":5,"N":60,"P":40,"K":50,"organic_matter":6,"soil_moisture":25,"rainfall":150,"irrigation_frequency":4,"water_usage_efficiency":2.5,"sunlight_exposure":9,"co2_concentration":410,"fertilizer_usage":150}'
# Expected: predicted_class: 4 (Very High Risk)
```

### Integration test: Registration
1. Open app → click "Create one" → fill form → submit
2. Should redirect to login with success message
3. Log in with new credentials → should reach dashboard

### Integration test: Prediction
1. Log in → click "New Prediction" → click "Load Example Values" → click "Run Prediction"
2. Should show result page with "Very Low Pest Risk" and confidence ~63%
3. Navigate to History → should see saved record

---

## 12. COMMON ERRORS & FIXES

| Error | Cause | Fix |
|-------|-------|-----|
| `ExceptionInInitializerError: HibernateUtil` | MySQL not running or wrong password | Start MySQL, check hibernate.cfg.xml credentials |
| `java.net.ConnectException: Connection refused (localhost:5000)` | Flask API not started | Run `python app.py` first |
| `WebView blank page` | Spring Boot not ready yet | Wait 3–5 seconds, click Refresh in nav bar |
| `InconsistentVersionWarning` (Python) | sklearn version mismatch | Upgrade sklearn: `pip install --upgrade scikit-learn` |
| `No module named flask` | Flask not installed | `pip install flask` |
| `UserWarning: X does not have valid feature names` | Known sklearn warning, harmless | Suppress with `import warnings; warnings.filterwarnings('ignore')` |
| `Duplicate entry for username` | Trying to register same username | Use different username |
| `404 on /static/styles.css` | Static resource not configured | Ensure `spring.web.resources.static-locations=classpath:/static/,file:src/main/webapp/static/` in application.properties |
| JavaFX black window | Java module issues | Add `--add-opens` VM flags or update to JDK 21 |
| `HibernateException: Unrecognized dialect` | Wrong dialect class name | Use `org.hibernate.dialect.MySQLDialect` |
| Port 8080 busy | Another app running on 8080 | Change `server.port=8081` and update JavaFXApp.java URL |

---

## 13. ADDITIONAL NOTES FOR AI

- The `@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})` annotation is essential — otherwise Spring Security intercepts all requests before our Servlets run.
- `@ServletComponentScan("com.pestpredictor.servlet")` is required on MainApp so Spring Boot registers the `@WebServlet` and `@WebFilter` annotations.
- `SecurityConfig` must disable CSRF, form login, and http basic, and permit all requests — our own AuthFilter handles authentication.
- `HibernateUtil` and `JdbcUtil` are plain utility classes (NOT Spring beans) because they're used inside Servlet classes which are not managed by Spring.
- The `PredictionService` uses Java's built-in `java.net.http.HttpClient` (Java 11+) — no Apache HttpClient needed.
- All session attributes use string constants defined in `SessionUtil` to avoid typos.
- HTML views in `/WEB-INF/views/` use plain JavaScript `fetch()` to call the `/api/*` REST endpoints — no server-side template engine (JSTL/Thymeleaf) is needed.
- The `predict.html` page includes a "Load Example Values" button that fills the form with known-good values from the notebook (expected output: class 0).

