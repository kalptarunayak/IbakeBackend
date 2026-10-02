# IBake — Cake & Bakery E-Commerce REST API

Enterprise backend REST API for **IBake**, a multi-vendor, city-scoped cake, chocolate, and floral gifting e-commerce platform tailored for the Indian market. Designed for deployment on **Railway** with **PostgreSQL via Supabase**.

---

## 🛠 Tech Stack
- **Framework**: Spring Boot 3.2.4 (Java 17+)
- **Build Tool**: Apache Maven 3.9+
- **Security**: Spring Security 6 with JJWT 0.12.5 (Stateless Bearer Tokens)
- **Database**: PostgreSQL (Supabase with connection pooling optimization)
- **Persistence**: Spring Data JPA / Hibernate 6
- **Validation**: Jakarta Bean Validation (`@Valid`, `@NotBlank`, `@Size`, etc.)
- **Deployment**: Docker containerization on Railway (`railway.json` / `railway.toml`)

---

## 🏛 Domain Architecture & Key Rules

1. **City-Scoped Availability**:
   - A single `Product` (e.g. *Dutch Chocolate Truffle Cake*) can be fulfilled by multiple `Vendors`.
   - Each `Vendor` operates in specific `Cities` with individual prices, stock counts, and preparation lead times via the `VendorProductCity` join entity.
   - Products are only visible in a city if at least one active vendor offers it there with `available = true`.

2. **Role-Based Access Control (RBAC)**:
   - `CUSTOMER`: Cart operations, placing orders, viewing personal order history.
   - `ADMIN`: Manage products, assign vendor availability and prices, manage banners, view all orders.
   - `SUPER_ADMIN`: All ADMIN capabilities + enable/disable cities globally and create/manage admin accounts.

3. **Banners**:
   - Scoped to `city_id` + `occasion_id` + `startDate` / `endDate` range.

---

## 🚀 Environment Variables

All sensitive values are configured through environment variables:

| Variable | Description | Default / Example |
| :--- | :--- | :--- |
| `PORT` | HTTP port dynamically injected by Railway | `8080` |
| `SPRING_DATASOURCE_URL` or `DATABASE_URL` | Supabase / PostgreSQL JDBC connection string | `jdbc:postgresql://aws-0-ap-south-1.pooler.supabase.com:6543/postgres?sslmode=require` |
| `SPRING_DATASOURCE_USERNAME` or `DB_USERNAME` | Database username | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` or `DB_PASSWORD` | Database password | `your_supabase_password` |
| `JWT_SECRET` | 256-bit+ HMAC SHA key for JWT signing | `404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970` |
| `CORS_ALLOWED_ORIGINS` | Allowed frontend domains (comma separated) | `http://localhost:3000,http://localhost:5173,https://yourusername.github.io` |
| `HIBERNATE_DDL_AUTO` | Schema generation strategy | `update` |

---

## 📦 Build & Local Run

### Option 1: Terminal / Command Line
```bash
# Clone the repository and navigate to backend
cd backend

# Build project with Maven
mvn clean package -DskipTests

# Run the Spring Boot application (uses embedded Tomcat on port 8080)
java -jar target/ibake-backend-1.0.0.jar
```
Or directly with Maven:
```bash
mvn spring-boot:run
```

### Option 2: IntelliJ IDEA (Fixing "Server is not specified")
Spring Boot applications have an **embedded Tomcat web server** built-in and do **NOT** use external application servers (Tomcat/TomEE/GlassFish). If you see `Server is not specified`:

1. **Delete any accidental "Tomcat/Server" configuration**:
   - Go to **Run** → **Edit Configurations...**
   - If there is a configuration under *Tomcat Server*, *TomEE*, or *Application Server* showing *"Server is not specified"*, select it and click **Delete (-)**.
2. **Run via `IBakeApplication.java`**:
   - In IntelliJ Project tree, navigate to:  
     `backend/src/main/java/com/ibake/IBakeApplication.java`
   - Right-click `IBakeApplication.java` (or click the green **▶** play icon next to `public class IBakeApplication`)
   - Click **Run 'IBakeApplication'**
3. **Or run via Maven Tool Window**:
   - Open the **Maven** sidebar (right side of IntelliJ).
   - If `ibake-backend` is not visible, click **+** and select `backend/pom.xml`.
   - Expand `ibake-backend` → `Plugins` → `spring-boot` → double-click `spring-boot:run`.

---

## 🚂 Railway One-Click Deployment

1. Connect your GitHub repository to [Railway.app](https://railway.app).
2. Set the Root Directory to `backend` (or deploy from root Dockerfile).
3. In the Railway dashboard under **Variables**, set:
   - `DATABASE_URL` (from your Supabase Project Settings -> Database -> Connection string -> JDBC / Session mode)
   - `DB_USERNAME` and `DB_PASSWORD`
   - `JWT_SECRET`
   - `CORS_ALLOWED_ORIGINS` (pointing to your deployed React frontend URL)
4. Railway will automatically build the Dockerfile and start the service with healthy `/actuator/health` checks!

---

## ☁️ Render Deployment (render.com)

You can deploy IBake to Render in two ways:

### Option A: Using the Render Blueprint (`render.yaml`)
1. Push this repository to GitHub/GitLab.
2. In [Render Dashboard](https://dashboard.render.com), click **New +** → **Blueprint**.
3. Connect your repository. Render automatically reads `render.yaml` with the configured Dockerfile, health check path (`/actuator/health`), port `10000`, and environment parameters.
4. Fill in your Supabase JDBC connection credentials (`SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`).
5. Click **Apply** to deploy!

### Option B: Deploy as a Render Web Service manually
1. In Render Dashboard, click **New +** → **Web Service**.
2. Select your repository.
3. Configure the service settings:
   - **Environment**: `Docker`
   - **Region**: `Singapore` (recommended for low latency in India) or closest region
   - **Dockerfile Path**: `./Dockerfile` (or `./backend/Dockerfile.render`)
   - **Docker Context**: `.` (root directory)
   - **Health Check Path**: `/actuator/health`
4. Add the following **Environment Variables**:
   - `PORT`: `10000`
   - `SPRING_DATASOURCE_URL`: `jdbc:postgresql://<SUPABASE_HOST>:6543/postgres?sslmode=require`
   - `SPRING_DATASOURCE_USERNAME`: `<DB_USERNAME>`
   - `SPRING_DATASOURCE_PASSWORD`: `<DB_PASSWORD>`
   - `JWT_SECRET`: Secure 256-bit+ HMAC SHA key
   - `CORS_ALLOWED_ORIGINS`: `https://your-frontend.onrender.com,https://your-username.github.io`
5. Click **Create Web Service**. Render builds the multi-stage Docker image and deploys with container-optimized memory limits!

