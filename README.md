# Crop Pest Predictor
- Created by Sourabh Joshi, Bhavishya Aggarwal, Kapilan Srinivasan
A full-stack Java desktop application that predicts crop pest risk using a pre-trained neural network model.

## Tech Stack

- **Frontend**: JavaFX (WebView rendering HTML pages)
- **Backend**: Spring Boot 3.2 + Servlet API
- **ORM**: Hibernate 6 (JPA) + raw JDBC
- **Database**: MySQL 8
- **ML Server**: Python Flask + TensorFlow/Keras
- **IDE**: NetBeans (Maven project)
- **Architecture**: MVC — strict Servlet routing

---

## Quick Start (5 Steps)

### Step 1 — Prerequisites

Install the following before proceeding:

| Tool | Version | Download |
|------|---------|----------|
| JDK | 17+ | https://adoptium.net |
| NetBeans | 19+ | https://netbeans.apache.org |
| MySQL | 8.0+ | https://dev.mysql.com/downloads |
| Python | 3.10+ | https://python.org |
| Maven | 3.9+ | bundled in NetBeans |

---

### Step 2 — Database Setup

Open MySQL Workbench or terminal and run:

```bash
mysql -u root -p < sql/schema.sql
```

This creates the `pest_predictor` database with the `users` and `prediction_history` tables.

Update credentials in `src/main/resources/application.properties` and `src/main/resources/hibernate.cfg.xml` if your MySQL password is not `root`.

---

### Step 3 — Copy ML Model Files

Copy the three model files into `python_api/model_files/`:

```
python_api/
  model_files/
    pest_nn_model.h5        <-- Keras neural network
    scaler_nn.pkl           <-- StandardScaler
    selected_features.pkl   <-- Feature order list
```

---

### Step 4 — Start Python Flask API

```bash
cd python_api
pip install -r requirements.txt
python app.py
```

The Flask server will start on **http://localhost:5000**.
Test it: `curl http://localhost:5000/health`

---

### Step 5 — Run the Java Application

**In NetBeans:**
1. File > Open Project > select `CropPestPredictor/`
2. Right-click project > Build (first build downloads Maven dependencies)
3. Right-click project > Run
4. JavaFX window opens automatically and loads the login page

**Via command line:**
```bash
mvn clean package
java --module-path /path/to/javafx-sdk/lib \
     --add-modules javafx.controls,javafx.fxml,javafx.web \
     -jar target/CropPestPredictor-1.0.0.jar
```

---

## Credentials

- **Demo account**: `demo` / `demo1234` (if inserted via schema.sql)
- Register a new account at `/register`

---

## Application URLs (inside WebView)

| Page | URL |
|------|-----|
| Login | http://localhost:8080/login |
| Register | http://localhost:8080/register |
| Dashboard | http://localhost:8080/dashboard |
| Predict | http://localhost:8080/predict |
| History | http://localhost:8080/history |

---

## Project Structure

```
CropPestPredictor/
├── pom.xml
├── README.md
├── sql/
│   └── schema.sql
├── python_api/
│   ├── app.py               Flask ML server
│   ├── requirements.txt
│   └── model_files/         Place .h5 and .pkl files here
└── src/main/
    ├── java/com/pestpredictor/
    │   ├── MainApp.java              Entry point
    │   ├── controller/
    │   │   ├── ApiController.java    REST JSON API
    │   │   └── SecurityConfig.java
    │   ├── dao/
    │   │   ├── UserDao.java
    │   │   ├── UserDaoImpl.java
    │   │   ├── PredictionHistoryDao.java
    │   │   └── PredictionHistoryDaoImpl.java
    │   ├── model/
    │   │   ├── User.java
    │   │   ├── PredictionHistory.java
    │   │   ├── PredictionRequest.java
    │   │   └── PredictionResult.java
    │   ├── service/
    │   │   ├── UserService.java
    │   │   └── PredictionService.java
    │   ├── servlet/
    │   │   ├── LoginServlet.java
    │   │   ├── RegisterServlet.java
    │   │   ├── LogoutServlet.java
    │   │   ├── DashboardServlet.java
    │   │   ├── PredictServlet.java
    │   │   ├── HistoryServlet.java
    │   │   ├── ErrorServlet.java
    │   │   └── AuthFilter.java
    │   ├── util/
    │   │   ├── HibernateUtil.java
    │   │   ├── JdbcUtil.java
    │   │   └── SessionUtil.java
    │   └── view/
    │       └── JavaFXApp.java
    ├── resources/
    │   ├── application.properties
    │   └── hibernate.cfg.xml
    └── webapp/
        ├── static/
        │   └── styles.css
        └── WEB-INF/views/
            ├── login.html
            ├── register.html
            ├── dashboard.html
            ├── predict.html
            ├── result.html
            ├── history.html
            └── error.html
```

---

## ML Model Pipeline

The Python Flask API replicates the exact feature engineering from `run_file.ipynb`:

**Input** (14 raw values): `temperature, humidity, wind_speed, N, P, K, organic_matter, soil_moisture, rainfall, irrigation_frequency, water_usage_efficiency, sunlight_exposure, co2_concentration, fertilizer_usage`

**Engineered features** (10 derived):
- `moist_temp_proxy` = soil_moisture × temperature
- `wind_speed` (passthrough)
- `water_usage_efficiency` (passthrough)
- `sfi` = organic_matter × (N + P + K)
- `overwater_index` = irrigation_frequency × rainfall
- `temperature` (passthrough)
- `nbr` = N / (P + K + ε)
- `pp` = (sunlight_exposure × co2_concentration) / (temperature + ε)
- `sunlight_exposure` (passthrough)
- `fert_heat_index` = fertilizer_usage × temperature

**Output classes**:
- 0 = Very Low Pest Risk (0–25%)
- 1 = Low Pest Risk (25–50%)
- 2 = Moderate Pest Risk (50–65%)
- 3 = High Pest Risk (65–80%)
- 4 = Very High Pest Risk (80–100%)

---

## Common Errors & Fixes

| Error | Fix |
|-------|-----|
| `HibernateException: Unable to create SessionFactory` | Check MySQL is running. Verify credentials in hibernate.cfg.xml |
| `Connection refused: localhost:5000` | Start the Python Flask API first |
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | Run `mvn clean install` to re-download dependencies |
| JavaFX window doesn't open | Ensure JDK 17+ with JavaFX support or add `--module-path` VM arg |
| `InconsistentVersionWarning` in Python | Acceptable warning — model still works. Or match sklearn version |
| `No module named tensorflow` | Run `pip install tensorflow` in the python_api folder |
| Port 8080 already in use | Change `server.port` in application.properties |
| Port 5000 already in use | Edit `app.py` and change the port, update `python.api.url` accordingly |

---

## NetBeans VM Options (if needed)

Right-click project > Properties > Run > VM Options:
```
--module-path /path/to/javafx-sdk-21/lib --add-modules javafx.controls,javafx.fxml,javafx.web,javafx.swing
```
