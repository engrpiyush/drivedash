# Drivedash Migration Gap Analysis (Static Code Inspection)

## Scope
- Legacy reference: `admin-panel` (Laravel + module-based PHP)
- Target reference: `drivedash` (multi-module Java/Spring Boot)
- Method: static inspection of routes/controllers/templates only (legacy app is not runnable)

## Stack & Architecture Snapshot

### Drivemond (`admin-panel`)
- Laravel monolith with module folders under `Modules/*`
- Feature exposure is route-driven (`Routes/web.php`, `Routes/api.php` per module)
- Hybrid admin UI + mobile APIs (customer/driver/user prefixed endpoints)

### Drivedash (`drivedash`)
- Maven multi-module Spring Boot monorepo (`drivedash-*` modules)
- Feature exposure is annotation-driven (`@RequestMapping`, `@GetMapping`, etc.)
- Admin and API controllers are present, but parity with legacy routes is incomplete in several modules

## High-Impact Missing/Broken Features

### 1) Payment gateways are effectively missing
- Legacy has dedicated payment callback/flow endpoints for many providers (`sslcommerz`, `stripe`, `razor-pay`, `paypal`, `bkash`, `paystack`, etc.).
- Drivedash `drivedash-gateways` module contains only `pom.xml` and no source/controllers/resources.
- Impact: digital payment execution/callback pipeline is incomplete, blocking ride/parcel payment flows.
- Evidence:
  - `admin-panel/Modules/Gateways/Routes/web.php`
  - `admin-panel/Modules/Gateways/Routes/api.php`
  - `drivedash/drivedash-gateways/pom.xml`

### 2) Trip mobile API lifecycle is largely missing
- Legacy exposes rich customer/driver ride lifecycle APIs (create ride, bidding, status updates, OTP, tracking, fare/payment, overview, parcel-related trip states).
- Drivedash currently has only admin-side trip web controller; no trip API controller equivalent was found.
- Impact: core ride lifecycle for apps is not at parity.
- Evidence:
  - `admin-panel/Modules/TripManagement/Routes/api.php`
  - `drivedash/drivedash-trip-management/src/main/java/com/drivedash/trip/controller/web/TripWebController.java`

### 3) Auth API parity gap (registration/OTP/password/reset/social)
- Legacy API includes customer+driver registration, social login, OTP login/verification, forget/reset password, user delete/change-password.
- Drivedash exposes only `/api/v1/auth/login`, `/refresh-token`, `/logout`.
- Impact: onboarding and account recovery flows are incomplete.
- Evidence:
  - `admin-panel/Modules/AuthManagement/Routes/api.php`
  - `drivedash/drivedash-auth/src/main/java/com/drivedash/auth/controller/AuthApiController.java`

### 4) Parcel operation API parity gap
- Legacy includes parcel order lifecycle endpoints: create, details/list, track-driver, suggested vehicle category, driver status updates.
- Drivedash parcel API currently exposes only category and weight listing.
- Impact: parcel booking and operational tracking APIs are incomplete.
- Evidence:
  - `admin-panel/Modules/ParcelManagement/Routes/api.php`
  - `drivedash/drivedash-parcel-management/src/main/java/com/drivedash/parcel/controller/api/ParcelApiController.java`

### 5) User-management API parity gap
- Legacy user APIs include address CRUD, profile update/info, notifications, loyalty points (customer+driver), driver activity/time tracking, withdraw request creation, live location endpoints.
- Drivedash user API module currently exposes only level and withdraw-method lookup endpoints.
- Impact: multiple customer/driver self-service and operational APIs are missing.
- Evidence:
  - `admin-panel/Modules/UserManagement/Routes/api.php`
  - `drivedash/drivedash-user-management/src/main/java/com/drivedash/usermanagement/controller/api/UserLevelApiController.java`

## Significant Admin Panel Gaps

### 6) Business management configuration coverage is partial
- Legacy includes additional admin flows not mapped in Drivedash controllers: driver/customer settings pages, SMS gateway config, language management, environment setup, clean-database utility, and full landing-page section management.
- Drivedash has business info, notifications, third-party config, pages media, trip fare setup, but no equivalents for the above sets.
- Impact: operational setup/configuration parity incomplete for admins.
- Evidence:
  - `admin-panel/Modules/BusinessManagement/Routes/web.php`
  - `drivedash/drivedash-business-management/src/main/java/com/drivedash/business/controller/web/*.java`

### 7) User-management admin tools are partial
- Legacy admin routes include customer/driver transaction exports, customer wallet tooling, driver level access controls, cash collect workflow, extensive statistics/log/export/trash flows.
- Drivedash covers base CRUD for customers/drivers/employees/roles/levels/withdraw, but lacks several above workflows.
- Impact: admin operations/reporting and role-level governance are reduced.
- Evidence:
  - `admin-panel/Modules/UserManagement/Routes/web.php`
  - `drivedash/drivedash-user-management/src/main/java/com/drivedash/usermanagement/controller/web/*.java`

### 8) Transaction admin export endpoint missing
- Legacy has `admin/transaction/export`.
- Drivedash transaction web controller currently has only list page endpoint.
- Impact: finance/report export parity gap.
- Evidence:
  - `admin-panel/Modules/TransactionManagement/Routes/web.php`
  - `drivedash/drivedash-transaction-management/src/main/java/com/drivedash/transaction/controller/web/TransactionWebController.java`

### 9) Dashboard endpoint mismatch
- Legacy dashboard includes `zone-wise-statistics` endpoint.
- Drivedash dashboard controller currently exposes dashboard, recent trips, leaderboards, and earnings; no `zone-wise-statistics` endpoint found.
- Impact: dashboard analytics feature gap.
- Evidence:
  - `admin-panel/Modules/AdminModule/Routes/web.php`
  - `drivedash/drivedash-admin-module/src/main/java/com/drivedash/admin/controller/DashboardController.java`

### 10) Auth web route path mismatch likely breaks existing admin entry URLs
- Legacy login is under `/admin/auth/login`.
- Drivedash web auth controller maps to `/auth/login`.
- Impact: existing admin bookmark/redirect expectations may fail without route aliasing/redirect.
- Evidence:
  - `admin-panel/Modules/AuthManagement/Routes/web.php`
  - `drivedash/drivedash-auth/src/main/java/com/drivedash/auth/controller/AuthWebController.java`

## Coverage Signal (Quantitative)
- Legacy module controller files found: **141**
- Drivedash controller files found: **49**
- Largest deltas by route/controller surface include: `UserManagement`, `BusinessManagement`, `TripManagement`, and `Gateways`.

## Notes
- This report is intentionally static and conservative: items listed are based on route/controller/template parity, not runtime behavior.
- Some legacy routes are commented or partially deprecated; however, where active route declarations exist and no Drivedash equivalent is present, they are treated as migration gaps.
