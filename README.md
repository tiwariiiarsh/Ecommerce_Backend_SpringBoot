# 🛒 Ecommerce Backend — Spring Boot

A full-featured, production-ready **RESTful e-commerce backend** built with **Java 17** and **Spring Boot 3.5.5**. It handles user authentication (JWT + Spring Security), product management, cart, orders, and payments via the **Stripe** payment gateway — all backed by **MySQL** and containerized with **Docker**.

---

## 🚀 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.5.5 |
| Database | MySQL 8 |
| ORM | Spring Data JPA / Hibernate |
| Security | Spring Security + JWT (jjwt 0.12.5) |
| Payment | Stripe Java SDK 30.0.0 |
| Mapping | ModelMapper 3.2.4 |
| Boilerplate Reduction | Lombok 1.18.38 |
| Validation | Spring Boot Starter Validation |
| Build | Maven (Maven Wrapper included) |
| Containerization | Docker + Docker Compose |

---

## ✨ Features

- **JWT Authentication** — Secure stateless auth with login/register endpoints; tokens are validated on every protected request.
- **Role-Based Access Control** — User and Admin roles with protected routes.
- **Product Management** — CRUD operations for products with category support.
- **Shopping Cart** — Add, update, remove items per authenticated user.
- **Order Management** — Place and track orders with order history.
- **Stripe Payment Integration** — Create payment intents and handle checkout securely.
- **Bean Validation** — Request body validation with meaningful error responses.
- **Docker-Ready** — Multi-stage Dockerfile + Docker Compose for one-command startup.
- **Persistent Storage** — MySQL with Docker volume for data durability.

---

## 📁 Project Structure

```
Ecommerce_Backend_SpringBoot/
├── src/
│   └── main/
│       ├── java/com/example/Ecommerce/
│       │   ├── controller/       # REST API controllers
│       │   ├── model/            # JPA entity classes
│       │   ├── repository/       # Spring Data JPA repositories
│       │   ├── service/          # Business logic layer
│       │   ├── dto/              # Data Transfer Objects
│       │   ├── security/         # JWT filter, config, UserDetailsService
│       │   └── EcommerceApplication.java
│       └── resources/
│           └── application.properties
├── images/                       # Project screenshots / diagrams
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── .gitignore
```

---

## ⚙️ Prerequisites

- **Java 17+**
- **Maven 3.9+** *(or use the included `./mvnw` wrapper)*
- **MySQL 8** *(skip if using Docker — it's included)*
- **Docker & Docker Compose** *(optional, for containerized run)*
- A **Stripe account** and secret API key

---

## 🏃 Running the Application

### Option 1 — Docker Compose (Recommended)

The easiest way to get everything running. Docker Compose starts both the MySQL database and the Spring Boot app automatically.

**1. Clone the repository**

```bash
git clone https://github.com/tiwariiiarsh/Ecommerce_Backend_SpringBoot.git
cd Ecommerce_Backend_SpringBoot
```

**2. Create a `.env` file** in the project root with the following variables:

```env
SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/ecommerce
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=root
JWT_SECRET=your_jwt_secret_key_here
STRIPE_SECRET_KEY=sk_test_your_stripe_key_here
```

**3. Start the services**

```bash
docker-compose up --build
```

The app will be available at **http://localhost:8080** once the MySQL healthcheck passes and the Spring Boot container starts.

To stop:

```bash
docker-compose down
```

To stop and remove volumes (clears DB data):

```bash
docker-compose down -v
```

---

### Option 2 — Run Locally (without Docker)

**1. Set up MySQL**

Create a database named `ecommerce` in your local MySQL instance:

```sql
CREATE DATABASE ecommerce;
```

**2. Configure `application.properties`**

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce
spring.datasource.username=root
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

jwt.secret=your_jwt_secret_key
stripe.secret.key=sk_test_your_stripe_key
```

**3. Build and run**

```bash
./mvnw clean spring-boot:run
```

Or build a JAR and run it:

```bash
./mvnw clean package -DskipTests
java -jar target/Ecommerce-0.0.1-SNAPSHOT.jar
```

The server starts on **http://localhost:8080**.

---

## 🔐 Authentication

This project uses **JWT (JSON Web Tokens)** for stateless authentication.

| Endpoint | Method | Description | Auth Required |
|---|---|---|---|
| `/api/auth/register` | POST | Register a new user | ❌ |
| `/api/auth/login` | POST | Login and receive JWT | ❌ |

Include the token in subsequent requests:

```
Authorization: Bearer <your_jwt_token>
```

---

## 📦 Key API Endpoints

> All endpoints below require a valid JWT unless otherwise noted.

### Products
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/products` | Get all products |
| GET | `/api/products/{id}` | Get a product by ID |
| POST | `/api/products` | Create product *(Admin)* |
| PUT | `/api/products/{id}` | Update product *(Admin)* |
| DELETE | `/api/products/{id}` | Delete product *(Admin)* |

### Cart
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/cart` | View current user's cart |
| POST | `/api/cart` | Add item to cart |
| PUT | `/api/cart/{itemId}` | Update cart item quantity |
| DELETE | `/api/cart/{itemId}` | Remove item from cart |

### Orders
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/orders` | Place an order |
| GET | `/api/orders` | Get current user's orders |
| GET | `/api/orders/{id}` | Get order details |

### Payments (Stripe)
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/payments/create-intent` | Create a Stripe PaymentIntent |

---

## 🐳 Docker Details

### Dockerfile

Uses a **multi-stage build** to keep the final image small:

1. **Build stage** — Uses `maven:3.9-eclipse-temurin-17` to compile and package the JAR.
2. **Runtime stage** — Uses `eclipse-temurin:17-jre` (lightweight) to run the JAR.

### docker-compose.yml

Spins up two services:

- **`db`** — MySQL 8, exposed on port `3308` (mapped from internal `3306`), with a healthcheck to ensure the DB is ready before the app starts.
- **`app`** — Spring Boot app, exposed on port `8080`, depends on the `db` service being healthy, and reads environment variables from `.env`.

---

## 🛠️ Environment Variables

| Variable | Description |
|---|---|
| `SPRING_DATASOURCE_URL` | JDBC URL for MySQL |
| `SPRING_DATASOURCE_USERNAME` | MySQL username |
| `SPRING_DATASOURCE_PASSWORD` | MySQL password |
| `JWT_SECRET` | Secret key used to sign JWT tokens |
| `STRIPE_SECRET_KEY` | Stripe secret API key for payment processing |

---

## 🧪 Running Tests

```bash
./mvnw test
```

---

## 📌 Notes

- The project was built with **Spring Boot 3.5.5** and requires **Java 17** as the minimum runtime.
- PostgreSQL and H2 dependencies are present in `pom.xml` but currently commented out — MySQL is the active database.
- The `.env` file is not committed to the repository. Make sure to create it locally before running with Docker Compose.
- Stripe integration uses the official [stripe-java](https://github.com/stripe/stripe-java) SDK (v30).

---

## 👤 Author

**Arsh Tiwari**  
GitHub: [@tiwariiiarsh](https://github.com/tiwariiiarsh)
