# 🚗 Vehicle Rental & Booking System — Microservices

A backend platform for vehicle rentals, built as **1 Eureka Server + 7 independent Spring Boot microservices**. Every service owns its own data, registers with Eureka, and talks to other services through **OpenFeign** using service names, never hard-coded hosts or ports.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-Eureka%20%2B%20OpenFeign-blue)
![MySQL](https://img.shields.io/badge/MySQL-8-informational)
![Status](https://img.shields.io/badge/status-in%20development-yellow)

---

## 📌 Table of Contents

1. [Overview](#-overview)
2. [Architecture](#-architecture)
3. [Tech Stack](#-tech-stack)
4. [Services](#-services)
5. [Inter-Service Communication Matrix](#-inter-service-communication-matrix)
6. [Business Rules & Validation](#-business-rules--validation)
7. [REST API Reference](#-rest-api-reference)
8. [Getting Started](#-getting-started)
9. [Demo Walkthrough](#-demo-walkthrough)
10. [Project Structure](#-project-structure)
11. [Future Features](#-future-features)
12. [Contributing](#-contributing)
13. [Author](#-author)

---

## 🔍 Overview

A vehicle rental platform has to manage **customers, vehicles, drivers, bookings, payments, trips, and feedback**. Instead of one large monolith, this project splits the system into seven independently deployable services.

**Core design rules**

- Each microservice **owns its own business data** (separate schema per service).
- A service **never accesses another service's database**. It calls the other service's REST API through OpenFeign.
- Services **discover each other via Eureka**, so no fixed IPs or ports.
- If a downstream service is unavailable, the caller returns a clear, meaningful error instead of pretending it got data.

---

## 🏗 Architecture

```
                         ┌─────────────────┐
                         │  eureka-server  │
                         └────────┬────────┘
        ┌───────────┬───────────┬─┴─────────┬────────────┬───────────┐
        ▼           ▼           ▼           ▼            ▼           ▼
   customer-    vehicle-     driver-     booking-     payment-     trip-      feedback-
    service      service     service      service      service    service      service
```

**Service-to-service call flow (OpenFeign + Eureka)**

```
Calling Service ──(OpenFeign)──► Target Service Name ──(Eureka lookup)──► Available Instance
```

**Registered services**

| # | Application | Type |
|---|---|---|
| 1 | `eureka-server` | Infrastructure |
| 2 | `customer-service` | Business |
| 3 | `vehicle-service` | Business |
| 4 | `driver-service` | Business |
| 5 | `booking-service` | Business |
| 6 | `payment-service` | Business |
| 7 | `trip-service` | Business |
| 8 | `feedback-service` | Business |

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java |
| Framework | Spring Boot |
| Microservices tooling | Spring Cloud (Netflix Eureka Server & Client) |
| Inter-service calls | Spring Cloud OpenFeign |
| API style | REST |
| Database | MySQL (database/schema per service) |
| Persistence | Spring Data JPA / Hibernate |
| Build tool | Maven |
| API testing | Postman |

---

## 🧩 Services

### 1. Eureka Server (`eureka-server`)
Central service registry. All seven business services register here as Eureka clients. The dashboard must show all seven as available.

### 2. Customer Service (`customer-service`)
Central source of customer information and profile management.
- **Data:** `id, name, email, mobile`
- **Provides customer details** to booking, payment, and feedback services.

### 3. Vehicle Service (`vehicle-service`)
Manages the rental fleet.
- **Data:** `id, vehicleName, model, type, dailyFee`
- **Provides vehicle details** to booking and trip services.

### 4. Driver Service (`driver-service`)
Manages drivers who execute trips or are responsible for bookings.
- **Data:** `id, name, email, licenseNumber, specialization`
- **Provides driver details** to trip service.

### 5. Booking Service (`booking-service`)
Records the relationship between a customer and a vehicle.
- **Data:** `id, customerId, vehicleId, bookingDate, durationDays, status`
- **Calls:** `customer-service`, `vehicle-service`

### 6. Payment Service (`payment-service`)
Handles payments made for a customer's vehicle booking.
- **Data:** `id, customerId, bookingId, amount, paymentDate, status`
- **Calls:** `customer-service`, `booking-service`

### 7. Trip Service (`trip-service`)
Manages individual transport routes and schedules tied to vehicles and drivers.
- **Data:** `id, title, routeDetails, vehicleId, driverId, dueDate`
- **Calls:** `vehicle-service`, `driver-service`

### 8. Feedback Service (`feedback-service`)
Stores star ratings and reviews submitted after trips.
- **Data:** `id, customerId, tripId, bookingId, ratingScore, comments, feedbackDate`
- **Calls:** `customer-service`, `trip-service`, `booking-service`

---

## 🔗 Inter-Service Communication Matrix

| Calling Service | Target Service | Purpose | Technology |
|---|---|---|---|
| booking-service | customer-service | Verify / retrieve customer | OpenFeign + Eureka |
| booking-service | vehicle-service | Verify / retrieve vehicle | OpenFeign + Eureka |
| payment-service | customer-service | Verify customer identity | OpenFeign + Eureka |
| payment-service | booking-service | Verify booking records | OpenFeign + Eureka |
| trip-service | vehicle-service | Verify vehicle data | OpenFeign + Eureka |
| trip-service | driver-service | Verify driver context | OpenFeign + Eureka |
| feedback-service | customer-service | Verify customer identity | OpenFeign + Eureka |
| feedback-service | trip-service | Verify trip execution | OpenFeign + Eureka |
| feedback-service | booking-service | Verify booking context | OpenFeign + Eureka |

---

## ✅ Business Rules & Validation

- Reject a booking if the **customer does not exist**.
- Reject a booking if the **vehicle does not exist**.
- For payments, verify the referenced **customer and booking**.
- For trips, verify the **vehicle and driver** allocations.
- For feedback, verify the **customer, trip, and booking** instances.
- Return suitable **HTTP status codes** and meaningful error responses.
- **Never create records** for invalid references.
- If a target service is down, return an understandable error rather than fake data.

---

## 📡 REST API Reference

Every service exposes full CRUD. Pattern shown for `customer-service`:

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/customers` | Add customer |
| `GET` | `/customers` | Get all customers |
| `GET` | `/customers/{id}` | Get customer by ID |
| `PUT` | `/customers/{id}` | Update customer |
| `DELETE` | `/customers/{id}` | Delete customer |

The same pattern applies to `/vehicles`, `/drivers`, `/bookings`, `/payments`, `/trips`, and `/feedbacks`.

> 📝 Feedback service exposes: create, get by ID, get all, update, and delete feedback.

---

## 🚀 Getting Started

### Prerequisites

- JDK 17+
- Maven 3.8+
- MySQL 8+
- Postman (optional, for testing)

### 1. Clone the repository

```bash
git clone https://github.com/<your-username>/vehicle-rental-microservices.git
cd vehicle-rental-microservices
```

### 2. Create the databases

```sql
CREATE DATABASE customer_db;
CREATE DATABASE vehicle_db;
CREATE DATABASE driver_db;
CREATE DATABASE booking_db;
CREATE DATABASE payment_db;
CREATE DATABASE trip_db;
CREATE DATABASE feedback_db;
```

### 3. Configure credentials

Update `application.properties` (or `application.yml`) in each service with your MySQL username and password.

### 4. Start services in this order

```bash
# 1. Registry first
cd eureka-server && mvn spring-boot:run

# 2. Then the business services (any order, separate terminals)
cd customer-service && mvn spring-boot:run
cd vehicle-service  && mvn spring-boot:run
cd driver-service   && mvn spring-boot:run
cd booking-service  && mvn spring-boot:run
cd payment-service  && mvn spring-boot:run
cd trip-service     && mvn spring-boot:run
cd feedback-service && mvn spring-boot:run
```

### 5. Verify

Open the Eureka dashboard (default `http://localhost:8761`). All seven business services should show as **UP**.

---

## 🎬 Demo Walkthrough

Main end-to-end scenario: *a customer books a vehicle, pays, gets a trip with a driver, and submits feedback.*

1. Start `eureka-server`.
2. Start all seven business services.
3. Confirm all seven appear on the Eureka dashboard.
4. Insert sample customers, vehicles, and drivers.
5. **Create a booking** → `booking-service` calls `customer-service` + `vehicle-service`.
6. **Create a payment** → `payment-service` calls `customer-service` + `booking-service`.
7. **Create a trip** → `trip-service` calls `vehicle-service` + `driver-service`.
8. **Submit feedback** → `feedback-service` calls `customer-service` + `trip-service` + `booking-service`.
9. Show that communication uses **service names via Eureka**, not fixed ports.

**Sample data used:** Customer `101`, Vehicle `501`, Driver `301`, Booking `9001`, Trip `7001`.

---

## 📁 Project Structure

```
vehicle-rental-microservices/
├── eureka-server/
├── customer-service/
├── vehicle-service/
├── driver-service/
├── booking-service/
├── payment-service/
├── trip-service/
├── feedback-service/
└── README.md
```

Each business service follows the same layered layout:

```
src/main/java/com/<org>/<service>/
├── controller/
├── service/
├── repository/
├── entity/
├── dto/
├── client/        # Feign clients
├── exception/     # Custom exceptions + global handler
└── config/
```

---

## 🔮 Future Features Additions;

**Architecture & infrastructure**
- [ ] **API Gateway** (Spring Cloud Gateway) as the single entry point
- [ ] **Centralized configuration** with Spring Cloud Config Server
- [ ] **Resilience4j** circuit breakers, retries, and fallbacks for Feign calls
- [ ] **Distributed tracing** with Micrometer + Zipkin
- [ ] **Docker** image per service and a **Docker Compose** setup for one-command startup
- [ ] **Kubernetes** manifests / Helm charts for deployment
- [ ] **CI/CD** pipeline with GitHub Actions (build, test, image push)

**Security**
- [ ] **JWT authentication and authorization** with Spring Security
- [ ] **Role-based access control** (Admin, Customer, Driver)
- [ ] Secure inter-service calls with service-to-service tokens

**Messaging & data**
- [ ] **Event-driven flow** with Kafka or RabbitMQ (e.g., booking confirmed → payment → notification)
- [ ] **Saga pattern** for distributed transactions across booking, payment, and trip
- [ ] **Redis caching** for hot lookups (vehicles, customers)
- [ ] Database migrations with **Flyway**

**Business features**
- [ ] Vehicle **availability calendar** and double-booking prevention
- [ ] **Dynamic pricing** (seasonal, weekend, long-duration discounts)
- [ ] Real **payment gateway** integration (Razorpay / Stripe)
- [ ] **Notification service** (email / SMS for booking and payment confirmations)
- [ ] Live **trip tracking** with GPS
- [ ] Automatic **driver assignment** based on specialization and availability
- [ ] Invoice / receipt **PDF generation**
- [ ] **Admin dashboard** and reporting

**Quality & docs**
- [ ] **Swagger / OpenAPI** docs per service
- [ ] Unit + integration tests (JUnit 5, Mockito, Testcontainers)
- [ ] **Postman collection** committed to the repo
- [ ] Pagination, sorting, and filtering on list endpoints
- [ ] Centralized logging with the ELK stack

---

⭐ If this project helped you, consider giving it a star.
