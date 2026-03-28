# Drivedash Migration Gap Analysis

**Scope:** Features present in the legacy admin-panel (Laravel/PHP) that are missing or incomplete in Drivedash (Spring Boot/Java).

This analysis is based on static code inspection of both codebases. The legacy system cannot be run, so findings are derived purely from route definitions, controller methods, service implementations, and entity models.

---

## 1. User Management

### Customer Management

| Feature | Admin-Panel | Drivedash |
|---|---|---|
| Full CRUD | ✅ | ✅ |
| Status toggle | ✅ | ✅ |
| Soft delete / trash / restore / permanent delete | ✅ | ✅ |
| Statistics (total, active, inactive counts) | ✅ | ❌ Missing |
| Activity logging per customer | ✅ | ❌ Missing |
| Export to Excel | ✅ | ❌ Missing |
| Per-customer transaction history export | ✅ | ❌ Missing |
| AJAX data endpoint (getAllAjax) | ✅ | ❌ Missing |

### Driver Management

| Feature | Admin-Panel | Drivedash |
|---|---|---|
| Full CRUD | ✅ | ✅ |
| Status toggle | ✅ | ✅ |
| Soft delete / trash / restore / permanent delete | ✅ | ✅ |
| Profile update request list & approval/rejection workflow | ✅ | ❌ Missing |
| Cash collection tracking | ✅ | ❌ Missing |
| Vehicle association AJAX endpoint | ✅ | ❌ Missing |
| Statistics | ✅ | ❌ Missing |
| Activity logging per driver | ✅ | ❌ Missing |
| Export to Excel | ✅ | ❌ Missing |
| Per-driver transaction history export | ✅ | ❌ Missing |

### Employee Management

| Feature | Admin-Panel | Drivedash |
|---|---|---|
| Full CRUD | ✅ | ✅ |
| Status toggle | ✅ | ✅ |
| Soft delete / trash / restore / permanent delete | ✅ | ✅ |
| Activity logging per employee | ✅ | ❌ Missing |
| Export to Excel | ✅ | ❌ Missing |

### User Level Management

| Feature | Admin-Panel | Drivedash |
|---|---|---|
| Level CRUD (customer & driver levels) | ✅ | ⚠️ Incomplete (API list only) |
| Level access control configuration | ✅ | ❌ Missing |
| Statistics per level | ✅ | ❌ Missing |

### Withdrawal Management

| Feature | Admin-Panel | Drivedash |
|---|---|---|
| Withdraw method CRUD | ✅ | ✅ |
| Withdraw request list & approval/rejection | ✅ | ✅ |
| Default/active status update | ✅ | ✅ |

---

## 2. Trip Management

| Feature | Admin-Panel | Drivedash |
|---|---|---|
| Trip listing (all, parcel, regular types) | ✅ | ✅ |
| Trip detail view | ✅ | ✅ |
| Soft delete / trash / restore / permanent delete | ✅ | ✅ |
| Invoice generation (PDF) | ✅ | ❌ Missing |
| Activity logging per trip | ✅ | ❌ Missing |
| Export to Excel | ✅ | ❌ Missing |
| Trip API controller (for mobile app) | ✅ | ❌ Missing |

---

## 3. Business Management

### Business Setup

| Feature | Admin-Panel | Drivedash |
|---|---|---|
| Business info management | ✅ | ✅ |
| Driver-specific settings | ✅ | ✅ |
| Customer-specific settings | ✅ | ✅ |
| Cancellation reasons CRUD with type/user-type | ✅ | ✅ |

### Third-Party Integrations Configuration

| Feature | Admin-Panel | Drivedash |
|---|---|---|
| SMS gateway configuration UI | ✅ | ❌ Missing |
| Email configuration UI | ✅ | ❌ Missing |
| reCAPTCHA configuration UI | ✅ | ❌ Missing |
| Google Maps API configuration UI | ✅ | ❌ Missing |
| Payment gateway configuration UI | ✅ | ⚠️ Incomplete (API exists, no full UI) |

### Pages & Media

| Feature | Admin-Panel | Drivedash |
|---|---|---|
| Social links management | ✅ | ✅ |
| Landing page section builder (intro, solutions, statistics, platform, testimonials, CTA) | ✅ | ❌ Missing |
| Business pages content management | ✅ | ⚠️ Incomplete |

### System Settings

| Feature | Admin-Panel | Drivedash |
|---|---|---|
| Environment variable setup | ✅ | ❌ Missing |
| Database cleanup utility | ✅ | ❌ Missing |
| Multi-language / translation management | ✅ | ❌ Missing |

---

## 4. Vehicle Management

| Feature | Admin-Panel | Drivedash |
|---|---|---|
| Vehicle CRUD (with brand/model/category) | ✅ | ✅ |
| Status toggle for all vehicle entities | ✅ | ✅ |
| Soft delete / trash / restore / permanent delete | ✅ | ✅ |
| AJAX brand, category, model endpoints | ✅ | ❌ Missing |
| Activity logging | ✅ | ❌ Missing |
| Export to Excel | ✅ | ❌ Missing |
| Statistics | ✅ | ❌ Missing |

---

## 5. Zone Management

| Feature | Admin-Panel | Drivedash |
|---|---|---|
| Zone CRUD with polygon coordinates | ✅ | ✅ |
| Status toggle | ✅ | ✅ |
| Soft delete / trash / restore / permanent delete | ✅ | ✅ |
| AJAX getZones endpoint | ✅ | ❌ Missing |
| Activity logging | ✅ | ❌ Missing |
| Export to Excel | ✅ | ❌ Missing |

---

## 6. Fare Management

| Feature | Admin-Panel | Drivedash |
|---|---|---|
| Trip fare setup per zone | ✅ | ✅ |
| Parcel fare setup per zone | ✅ | ✅ |
| Zone-wise default trip fare | ✅ | ✅ |
| Fare bidding configuration | ✅ | ✅ |

---

## 7. Parcel Management

| Feature | Admin-Panel | Drivedash |
|---|---|---|
| Parcel category CRUD | ✅ | ✅ |
| Parcel weight tier CRUD | ✅ | ✅ |
| Status toggle | ✅ | ✅ |
| Soft delete / trash / restore / permanent delete | ✅ | ✅ |
| Export / download | ✅ | ❌ Missing |
| Activity logging | ✅ | ❌ Missing |
| Parcel booking management (admin view of parcel orders) | ✅ | ❌ Missing |

---

## 8. Transaction Management

| Feature | Admin-Panel | Drivedash |
|---|---|---|
| Transaction listing and filtering | ✅ | ✅ |
| Customer wallet view and fund addition | ✅ | ✅ |
| Export to Excel | ✅ | ❌ Missing |
| Activity logging | ✅ | ❌ Missing |
| Statistics / analytics | ✅ | ❌ Missing |

---

## 9. Promotion Management

| Feature | Admin-Panel | Drivedash |
|---|---|---|
| Banner CRUD | ✅ | ✅ |
| Coupon CRUD | ✅ | ✅ |
| Status toggle (banners & coupons) | ✅ | ✅ |
| Soft delete / trash / restore / permanent delete | ✅ | ✅ |
| Export to Excel | ✅ | ❌ Missing |
| Activity logging | ✅ | ❌ Missing |

---

## 10. Review Module

| Feature | Admin-Panel | Drivedash |
|---|---|---|
| Driver review listing | ✅ | ✅ |
| Customer review listing | ✅ | ✅ |
| Driver review export | ✅ | ❌ Missing |
| Customer review export | ✅ | ❌ Missing |
| Review status management | ✅ | ❌ Missing |

---

## 11. Chatting Module

| Feature | Admin-Panel | Drivedash |
|---|---|---|
| Admin chat view | ✅ | ✅ |
| WebSocket real-time messaging | N/A (no routes in admin-panel) | ✅ |

---

## 12. Payment Gateways Module

This is the most critical gap. The legacy system supports 13+ payment gateways; the Drivedash `drivedash-gateways` module is entirely empty — no controllers, services, or implementations.

| Gateway | Admin-Panel | Drivedash |
|---|---|---|
| SSLCommerz | ✅ | ❌ Missing |
| Stripe | ✅ | ❌ Missing |
| Razor Pay | ✅ | ❌ Missing |
| PayPal | ✅ | ❌ Missing |
| SenangPay | ✅ | ❌ Missing |
| Paytm | ✅ | ❌ Missing |
| Flutterwave | ✅ | ❌ Missing |
| Paystack | ✅ | ❌ Missing |
| bKash | ✅ | ❌ Missing |
| LiqPay | ✅ | ❌ Missing |
| MercadoPago | ✅ | ❌ Missing |
| Paymob | ✅ | ❌ Missing |
| PayTabs | ✅ | ❌ Missing |

---

## 13. Dashboard & Admin Module

| Feature | Admin-Panel | Drivedash |
|---|---|---|
| Dashboard with earnings statistics | ✅ | ✅ |
| Recent trip activity | ✅ | ✅ |
| Driver leaderboard | ✅ | ✅ |
| Customer leaderboard | ✅ | ✅ |
| Zone-wise statistics | ✅ | ❌ Missing |
| Activity log viewer | ✅ | ✅ |
| Admin settings / profile management | ✅ | ✅ |
| Notification management | ✅ | ✅ |
| Language selection | ✅ | ✅ |

---

## Summary: Priority Gaps

### P0 — Critical (blocks cutover)

| # | Gap | Affected Modules |
|---|---|---|
| 1 | **Payment gateway implementations** — all 13 gateways missing | `drivedash-gateways` |
| 2 | **Trip invoice generation** — admin needs to generate PDF invoices | `drivedash-trip-management` |
| 3 | **Driver profile update request workflow** — drivers submit updates, admin approves/rejects | `drivedash-user-management` |
| 4 | **Export to Excel** — missing across all modules (customers, drivers, employees, trips, transactions, vehicles, zones, parcels, promotions, reviews) | All modules |
| 5 | **Parcel booking management** — admin view of active/completed parcel orders | `drivedash-parcel-management` |

### P1 — High (significant feature loss)

| # | Gap | Affected Modules |
|---|---|---|
| 6 | **Activity logging per record** — missing across most modules | All modules |
| 7 | **Statistics/analytics per module** — customer stats, driver stats, vehicle stats, zone stats | User, Vehicle, Zone, Transaction |
| 8 | **Third-party config UIs** — SMS, email, reCAPTCHA, Google Maps configuration pages | `drivedash-business-management` |
| 9 | **Cash collection tracking for drivers** | `drivedash-user-management` |
| 10 | **Zone-wise dashboard statistics** | `drivedash-admin-module` |

### P2 — Medium (admin/operational features)

| # | Gap | Affected Modules |
|---|---|---|
| 11 | **Multi-language / translation management** | `drivedash-business-management` |
| 12 | **Environment variable setup UI** | `drivedash-admin-module` |
| 13 | **Landing page section builder** (intro, solutions, testimonials, CTA sections) | `drivedash-business-management` |
| 14 | **AJAX helper endpoints** (all-brands, all-categories, all-zones, ajax-models) needed for dependent form dropdowns | Vehicle, Zone modules |
| 15 | **User level access control configuration** | `drivedash-user-management` |
| 16 | **Review status management and export** | `drivedash-review` |
| 17 | **Database cleanup utility** | `drivedash-admin-module` |
