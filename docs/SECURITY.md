# Security Implementation Guide

This document explains how authentication and authorization work in the E-Commerce application: how JWT tokens are issued, how users and admins are created, how data is stored and retrieved, and how every piece connects to make security work end to end.

The stack is a **Spring Boot** backend (Spring Security OAuth2 Resource Server + JWT) with a **MySQL** database, and a **React (Vite)** frontend that stores the token and attaches it to every request.

---

## 1. High-Level Overview

```
                         ┌──────────────────────────────────────────────┐
   React frontend        │                Spring Boot API               │
 ┌───────────────┐       │                                              │
 │ Login form    │──POST /api/auth/login──▶ AuthController ──▶ AuthService│
 │               │◀─────── JWT token ──────                             │
 │ localStorage  │       │                                              │
 │ 'incredible-  │       │  Every later request carries:                │
 │  tech.auth'   │──────▶│  Authorization: Bearer <token>               │
 └───────────────┘       │        │                                     │
                         │        ▼                                     │
                         │  SecurityFilterChain (JWT decoder/validator) │
                         │        │                                     │
                         │        ▼                                     │
                         │  Controller + @PreAuthorize role checks      │
                         │        │                                     │
                         │        ▼                                     │
                         │  JPA Repositories ──▶ MySQL (users, roles)   │
                         └──────────────────────────────────────────────┘
```

The API is **stateless**: there are no server-side sessions. The JWT itself carries the user's identity (email) and role, and is verified on every request using a shared secret.

---

## 2. Core Security Components (Backend)

| Concern | Class / File | Responsibility |
|---|---|---|
| Filter chain & route rules | `config/SecurityConfig.java` | Which endpoints are public vs. protected, role rules, stateless sessions, CORS/CSRF |
| JWT encode/decode keys | `config/JwtConfig.java` | Builds the `JwtEncoder` and `JwtDecoder` from a shared secret |
| Password hashing | `config/PasswordEncoderConfig.java` | Provides the `BCryptPasswordEncoder` bean |
| Login + token issue | `controller/AuthController.java` + `service/impl/AuthServiceImpl.java` | Validates credentials and issues the JWT |
| Register / profile / admin actions | `controller/UserManagementController.java` + `service/impl/UserManagementServiceImpl.java` | Creates customers, reads/updates the logged-in user, admin activate/deactivate |
| Identity model | `domain/User.java`, `Customer.java`, `Administrator.java`, `AccountProfile.java`, `UserRole.java` | The stored user records and their role |
| Persistence | `repository/UserManagementRepository.java` | Saves and looks up users in MySQL |

---

## 3. How the JWT Token Works

### 3.1 The signing key (`JwtConfig.java`)

The application uses **symmetric HMAC signing (HS256)** — the same secret both signs and verifies the token.

- The secret is read from the property `security.jwt.secret`, which in `application.properties` comes from the environment variable **`ECOMMERCE_JWT_SECRET`**.
- `createSecretKey(...)` Base64-decodes it and enforces **at least 32 bytes** (256 bits). If the secret is missing, not valid Base64, or too short, the app fails to start. This prevents weak keys.
- Two beans are produced:
  - `JwtEncoder` (`NimbusJwtEncoder` with an `ImmutableSecret`) — used to **create/sign** tokens at login.
  - `JwtDecoder` (`NimbusJwtDecoder` with `MacAlgorithm.HS256`) — used to **verify** tokens on every request. It also validates the **issuer** (`security.jwt.issuer` = `ecommerce-api`) and standard claims like expiry.

### 3.2 Issuing a token at login (`AuthServiceImpl.generateToken`)

When login succeeds, a JWT is built with these claims:

| Claim | Value | Purpose |
|---|---|---|
| `iss` (issuer) | `ecommerce-api` | Verified by the decoder so foreign tokens are rejected |
| `iat` (issued at) | now | Token age |
| `exp` (expires at) | now + `expiration-seconds` (3600 = **1 hour**) | Token lifetime |
| `sub` (subject) | user's email | Identifies the principal; later read as `authentication.getName()` |
| `roles` | e.g. `["CUSTOMER"]` or `["ADMINISTRATOR"]` | Drives authorization |
| `userId` | e.g. `USR-AB9107E7EC4A` | Convenience identifier |

The token is signed with the secret and returned as a string.

### 3.3 Verifying a token on later requests

`SecurityConfig` registers the API as an **OAuth2 Resource Server**:

```java
.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt ->
        jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));
```

For each incoming request that has `Authorization: Bearer <token>`:
1. The `JwtDecoder` verifies the signature, the issuer, and the expiry.
2. The `JwtAuthenticationConverter` reads the **`roles`** claim and turns each value into a Spring authority with the **`ROLE_`** prefix (so `ADMINISTRATOR` becomes `ROLE_ADMINISTRATOR`). This is what makes `hasRole('ADMINISTRATOR')` work.
3. The email (`sub`) becomes the authenticated principal name, available in controllers as the `Authentication` parameter.

---

## 4. Authorization Rules

### 4.1 URL-level rules (`SecurityConfig.securityFilterChain`)

The chain is stateless (`SessionCreationPolicy.STATELESS`), CSRF is disabled (safe here because there are no cookies/sessions — the token is sent explicitly), and CORS is enabled via a `CorsConfigurationSource`.

Public (no token required):
- `OPTIONS /**` (CORS preflight)
- `/error`
- `/api/auth/**` (login)
- `POST /api/users/register` (customer sign-up)
- `GET /api/productcatalog/**` (browse products)
- `GET /api/feedback/product/**` (read product reviews)

Restricted:
- Any non-GET `/api/productcatalog/**` (create/update/delete products) → **`ADMINISTRATOR`** only
- **Everything else** → any authenticated user

### 4.2 Method-level rules (`@EnableMethodSecurity` + `@PreAuthorize`)

`@EnableMethodSecurity` on `SecurityConfig` turns on annotation checks. Controllers then guard admin-only actions directly, for example:

```java
@PreAuthorize("hasRole('ADMINISTRATOR')")
@GetMapping   // list all users
```

This pattern appears across `UserManagementController`, `OrderController`, `PaymentController`, `DeliveryController`, and `FeedbackController` (update/delete). Regular authenticated endpoints (like `GET /api/users/me`) rely on the injected `Authentication` to know *who* is calling and act only on that user's own data.

---

## 5. How Users Are Created

### 5.1 Customers (self-service registration)

Flow: `POST /api/users/register` → `UserManagementController.registerCustomer` → `UserManagementServiceImpl.registerCustomer`:

1. Normalizes email and phone (`ValidationPatterns`).
2. Confirms `password` matches `confirmPassword`.
3. Rejects duplicate email or phone (`existsByEmailIgnoreCase`, `existsByPhoneNumber`).
4. **Hashes the password** with `passwordEncoder.encode(...)` (BCrypt) — the raw password is never stored.
5. `UserManagementFactory.createCustomer(...)` builds a `Customer` with a generated `userId` (`USR-...`) and `customerNumber` (`CUST-...`).
6. `userRepository.save(customer)` persists it.

The result is always a `Customer` (role `CUSTOMER`). **There is no endpoint that creates an administrator** — this is intentional so the public cannot self-promote.

### 5.2 Administrators (created manually)

The role is not a column you flip; it is determined by **which entity subclass** the record is. `User` uses JPA **JOINED inheritance**:

- `Customer` → `getRole()` returns `CUSTOMER`, stored in the `customer` table.
- `Administrator` → `getRole()` returns `ADMINISTRATOR`, stored in the `administrator` table.

Because there is no admin-registration code or SQL seed file in the project, an administrator must be inserted directly into the database: a row in the base `app_user` table plus a matching row in the `administrator` table (with `employee_number` and `department`). The password stored there must be a **BCrypt hash** so `passwordEncoder.matches(...)` works at login. Once that record exists, logging in with its email returns a token whose `roles` claim is `["ADMINISTRATOR"]`.

> Note: `spring.jpa.hibernate.ddl-auto=update` means Hibernate creates/updates these tables automatically on startup — there is no `data.sql` seeding an admin.

---

## 6. How Login Works (end to end)

`POST /api/auth/login` → `AuthController.login` → `AuthServiceImpl.login`:

1. Look up the user by email (case-insensitive) via `findByEmailIgnoreCase`. Not found → generic "Invalid email or password".
2. Reject if the account is inactive (`!user.isActive()`).
3. Compare the submitted password against the stored BCrypt hash with `passwordEncoder.matches(...)`. Mismatch → same generic error (so attackers can't tell which part failed).
4. Build and sign the JWT (`generateToken`).
5. Return an `AuthResponse`:

```json
{
  "token": "<jwt>",
  "type": "Bearer",
  "userId": "USR-AB9107E7EC4A",
  "email": "user@example.com",
  "role": "CUSTOMER"
}
```

---

## 7. How Data Is Stored and Retrieved

### 7.1 Storage model

- `User` (`@Entity`, abstract, `@Table("app_user")`, `@Inheritance(JOINED)`) holds shared fields: `userId` (PK), embedded `AccountProfile` (first/last name, email, phone), `passwordHash`, `active`, `createdAt`.
- `passwordHash` is annotated `@JsonIgnore`, so it is **never serialized** into any API response.
- `AccountProfile` is an `@Embeddable`, so its columns live inside the user tables (not a separate table).
- Passwords are stored only as **BCrypt hashes** (BCrypt includes a per-password salt automatically).

### 7.2 Retrieval

`UserManagementRepository extends JpaRepository<User, String>` with JPQL queries:
- `findByEmailIgnoreCase` — used by both login and profile lookups.
- `findCustomerByEmailIgnoreCase`, `existsByEmailIgnoreCase`, `existsByPhoneNumber` — for registration checks.

For "who am I" endpoints (`/api/users/me`), the controller reads the email from the verified JWT (`authentication.getName()`), then loads that user — a user can only read/update **their own** record this way.

### 7.3 Database configuration (`application.properties`)

- MySQL at `jdbc:mysql://localhost:3306/ecommerce`, with credentials supplied through `ECOMMERCE_DB_USERNAME` (default `root`) and the required `ECOMMERCE_DB_PASSWORD`; server port `8081`.
- `security.jwt.secret=${ECOMMERCE_JWT_SECRET}`, issuer `ecommerce-api`, expiry `3600s`.

---

## 8. Frontend Side of Security

| File | Role |
|---|---|
| `services/api/apiClient.js` | Central fetch wrapper. Reads the saved session from `localStorage` key **`incredibletech.auth`**, adds `Authorization: Bearer <token>` to requests, and on a `401` dispatches an `auth:expired` event. |
| `services/auth/authService.js` | Thin wrappers: `login` → `/auth/login`, `register` → `/users/register`, `profile` → `/users/me`, etc. |
| `state/auth/AuthProvider.jsx` | Holds the session in React context. `login()` saves the token to `localStorage`; `isAdmin` is computed as `session?.role === 'ADMINISTRATOR'`. Listens for `auth:expired` and logs out. |
| `hooks/auth/useAuth.js` | Hook to read the auth context anywhere. |
| `components/protected-route/ProtectedRoute.jsx` | Route guard: no session → redirect to `/login`; `admin` route without admin role → redirect to `/`. |

Note: the frontend's `isAdmin` guard is only for **UX** (hiding buttons/routes). Real enforcement always happens on the backend via the JWT `roles` claim and `@PreAuthorize`.

---

## 9. IntelliJ HTTP Client Collection

The request collection is organized under [`Backend/http/`](../Backend/http/).

- `Backend/http/auth.http` registers a sample customer and captures separate customer and administrator login tokens from `response.body.token`.
- `Backend/http/cart.http` calls the authenticated customer's current cart.
- `Backend/http/catalog/catalog.http` contains controller-verified catalog examples and uses an administrator token for product creation.
- `Backend/http/catalog/seed/*.http` contains the category seed payloads.

Select the `local` environment from `Backend/http/http-client.env.json`, copy `Backend/http/http-client.private.env.example.json` to the ignored `Backend/http/http-client.private.env.json`, and add local credentials there. Run `Backend/http/auth.http` before protected requests; no JWTs are committed.

---

## 10. How It All Connects (request lifecycle)

1. **Register** — `POST /api/users/register` creates a `Customer` with a BCrypt-hashed password in MySQL.
2. **Login** — `POST /api/auth/login` verifies the hash and returns a signed JWT containing the email and role.
3. **Store** — the React app saves the token in `localStorage` (`incredibletech.auth`).
4. **Call** — every subsequent request attaches `Authorization: Bearer <token>`.
5. **Verify** — the `SecurityFilterChain` decodes the token (signature + issuer + expiry) and converts `roles` into `ROLE_*` authorities.
6. **Authorize** — URL rules and `@PreAuthorize` decide if the caller may proceed; the email principal scopes "me" endpoints to the caller's own data.
7. **Persist/Read** — JPA repositories read and write the user/domain records in MySQL.
8. **Expire** — after 1 hour the token is rejected; a `401` triggers `auth:expired`, which logs the user out on the frontend.

---

## 11. Security Notes & Recommendations

These are observations about the current setup, not bugs to fix unless you want to:

- **DB credentials are environment-backed** in `application.properties`: `ECOMMERCE_DB_USERNAME` may default to `root`, while `ECOMMERCE_DB_PASSWORD` is required and has no committed default.
- **Token stored in `localStorage`** is readable by JavaScript, so it is exposed to XSS. It is a common tradeoff for SPA + stateless JWT; mitigate with strong output escaping / CSP, or consider httpOnly cookies if you later add CSRF protection.
- **No refresh-token flow** — when the 1-hour token expires the user must log in again.
- **Roles are trusted from the token** and not re-checked against the DB per request. That is standard for stateless JWT, but it means a role change only takes effect after the user gets a new token (re-login).
- **Admin creation is manual** (direct DB insert). If you want a safer path, add a one-time seeding routine or an admin-only "promote user" endpoint rather than editing the database by hand.

## Legacy collaborator seed collection

The repository also retains the collaborator-provided `Backend/http/seeding/` request collection for historical and broader end-to-end examples. New IntelliJ HTTP Client work should use the organized `Backend/http/` collection, private environment credentials, and port `8081`. Never commit generated JWTs or local database credentials. For secret generation, administrator setup, execution order, and troubleshooting, use [`SECURITY_SETUP_AND_SEEDING.md`](SECURITY_SETUP_AND_SEEDING.md).
