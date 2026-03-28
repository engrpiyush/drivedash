# DriveMond

A full-featured ride-sharing platform backend built with **Spring Boot 3 / Java 17**, structured as a Maven multi-module project.

## Overview

DriveMond provides the server-side backbone for a ride-sharing and parcel delivery service. It covers everything from authentication and zone-based fare configuration to real-time trip tracking, in-app messaging, payment gateway integrations, and an admin dashboard.

**Version:** 1.3.0-SNAPSHOT

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2.5 |
| Build | Maven (multi-module) |
| Database | MySQL 8+ |
| Schema migrations | Flyway 10 |
| Spatial queries | Hibernate Spatial / PostGIS |
| Security | Spring Security + JWT (jjwt 0.12.5) |
| Real-time | Spring WebSocket + STOMP + Pusher |
| Push notifications | Firebase Admin SDK 9.3 |
| Payments | Stripe, Razorpay, Paystack, PayPal |
| SMS / OTP | Twilio |
| Mapping | Google Maps API |
| API docs | SpringDoc OpenAPI (Swagger UI) |
| PDF export | iText 8 |
| Excel export | Apache POI 5 |
| Geo-IP | MaxMind GeoIP2 |
| Code generation | Lombok + MapStruct |

## Modules

| Module | Responsibility |
|---|---|
| `drivemond-core` | Shared entities, base classes, config, utilities |
| `drivemond-auth` | Authentication, JWT issuance/refresh, Spring Security, OTP flows |
| `drivemond-user-management` | Users, drivers, employees, roles, levels, withdrawal requests |
| `drivemond-business-management` | Business settings, push notification config, cancellation reasons |
| `drivemond-zone-management` | Geographic zones with PostGIS spatial queries |
| `drivemond-vehicle-management` | Vehicles, brands, models, categories |
| `drivemond-fare-management` | Trip and parcel fare/pricing configuration |
| `drivemond-trip-management` | Trip requests, bidding, routing, real-time status, WebSocket events |
| `drivemond-parcel-management` | Parcel shipping, categories, weight tiers |
| `drivemond-transaction-management` | Financial transaction ledger and reporting |
| `drivemond-promotion-management` | Coupons, banners, discount logic |
| `drivemond-review` | User and driver reviews/ratings |
| `drivemond-chatting` | In-app real-time messaging (WebSocket + STOMP) |
| `drivemond-gateways` | Payment gateway integrations (Stripe, Razorpay, Paystack, …) |
| `drivemond-admin-module` | Activity logging (AOP), admin notifications, dashboard analytics |

## Prerequisites

- Java 17+
- Maven 3.9+
- MySQL 8.0+
- (Optional) A Pusher account for real-time events
- (Optional) Firebase service-account JSON for push notifications
- (Optional) Google Maps API key for routing

## Installation

### 1. Clone the repository

```bash
git clone <repository-url>
cd drivemond
```

### 2. Create the database

```sql
CREATE DATABASE drivemond CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Configure environment variables

The application is configured via environment variables (with sensible defaults for local development). Copy and adjust as needed:

```bash
# Database
export DB_HOST=localhost
export DB_PORT=3306
export DB_DATABASE=drivemond
export DB_USERNAME=root
export DB_PASSWORD=your_password

# Server
export SERVER_PORT=8080

# JWT
export JWT_SECRET=your_strong_secret_key
export JWT_EXPIRATION_MS=86400000       # 24 hours
export JWT_REFRESH_MS=604800000         # 7 days

# Mail
export MAIL_HOST=smtp.mailtrap.io
export MAIL_PORT=587
export MAIL_USERNAME=your_mail_user
export MAIL_PASSWORD=your_mail_password

# Firebase (push notifications)
export FIREBASE_CREDENTIALS_FILE=/path/to/firebase-service-account.json

# Google Maps
export GOOGLE_MAP_API_KEY=your_google_maps_key

# Pusher (real-time)
export PUSHER_APP_ID=your_app_id
export PUSHER_APP_KEY=your_app_key
export PUSHER_APP_SECRET=your_app_secret
export PUSHER_APP_CLUSTER=mt1

# File storage
export STORAGE_BASE_DIR=uploads
```

### 4. Build

```bash
# Build all modules
mvn clean install

# Skip tests for a faster build
mvn clean install -DskipTests
```

### 5. Run

```bash
# From the project root — run the admin module (entry point)
mvn spring-boot:run -pl drivemond-admin-module

# Or run the packaged JAR
java -jar drivemond-admin-module/target/drivemond-admin-module-*.jar
```

The server starts on `http://localhost:8080` by default.

### Production profile

```bash
java -jar drivemond-admin-module/target/drivemond-admin-module-*.jar --spring.profiles.active=prod
```

The `prod` profile enables Thymeleaf caching and reduces log verbosity.

## API Documentation

Swagger UI is available at:

```
http://localhost:8080/swagger-ui.html
```

Raw OpenAPI spec:

```
http://localhost:8080/api/docs
```

## Database Migrations

Flyway manages all schema changes. Migrations run automatically on startup from `classpath:db/migration`. Hibernate is set to `validate` mode — it does not modify the schema.

## Project Structure

```
drivemond/
├── pom.xml                          # Parent POM (dependency management)
├── drivemond-core/                  # Shared library (no main class)
├── drivemond-auth/
├── drivemond-business-management/
├── drivemond-zone-management/
├── drivemond-vehicle-management/
├── drivemond-user-management/
├── drivemond-fare-management/
├── drivemond-trip-management/
├── drivemond-parcel-management/
├── drivemond-transaction-management/
├── drivemond-promotion-management/
├── drivemond-review/
├── drivemond-chatting/
├── drivemond-gateways/
└── drivemond-admin-module/          # Application entry point
```

## License

MIT