# Security Setup & Seeding Guide

This guide is the **hands-on companion** to [`SECURITY.md`](./SECURITY.md). Where `SECURITY.md`
explains *how* authentication and authorization work, this file shows you *how to run it*:

1. Generate a strong random JWT secret and wire it into `security.jwt.secret=${ECOMMERCE_JWT_SECRET}`.
2. Understand how that secret is used at runtime.
3. Get **Bearer tokens** for a customer and an administrator.
4. Seed data end to end — **customer, admin, products, delivery, payment** — using the
   IntelliJ HTTP Client (`.http`) JSON format.

> Backend runs on **`http://localhost:8081`** (`server.port=8081`). All examples use that base URL.
> Set the required `ECOMMERCE_DB_PASSWORD`; optionally set `ECOMMERCE_DB_USERNAME` when the `root` default is unsuitable. Keep both values outside source control.

---

## 1. Why a JWT Secret Is Needed

The API signs every login token with **HMAC-SHA256 (HS256)** — a *symmetric* algorithm, so the
**same secret both signs and verifies** the token. In `application.properties`:

```properties
security.jwt.secret=${ECOMMERCE_JWT_SECRET}
security.jwt.issuer=ecommerce-api
security.jwt.expiration-seconds=3600
```

`${ECOMMERCE_JWT_SECRET}` means the value is **not hardcoded** — Spring reads it from an
**environment variable** named `ECOMMERCE_JWT_SECRET` at startup. If that variable is missing,
the app fails to start. This keeps the signing key out of source control.

### The rules the secret must satisfy (`config/JwtConfig.java`)

`createSecretKey(...)` enforces two things, or the app refuses to boot:

| Rule | Why |
|---|---|
| Must be **valid Base64** | It is `Base64.getDecoder().decode(...)`-ed into raw bytes |
| Decoded value must be **≥ 32 bytes (256 bits)** | HS256 requires a key at least as large as the hash output; short keys are rejected |

So you cannot use a plain word like `"mysecret"`. You need a **Base64 string that decodes to at
least 32 random bytes**. Generating **48 bytes** (which Base64-encodes to 64 characters) gives you
comfortable headroom.

---

## 2. Generate the Random Secret (terminal scripts)

Pick **one** of the following. Each prints a Base64 string that decodes to 48 random bytes.

### Windows — PowerShell (recommended on this machine)

```powershell
$bytes = New-Object 'System.Byte[]' 48
[System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
[System.Convert]::ToBase64String($bytes)
```

### Windows — cmd (via PowerShell one-liner)

```cmd
powershell -Command "$b=New-Object byte[] 48;[System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($b);[Convert]::ToBase64String($b)"
```

### Cross-platform alternatives

```bash
# OpenSSL
openssl rand -base64 48
```

```bash
# Node.js
node -e "console.log(require('crypto').randomBytes(48).toString('base64'))"
```

```bash
# Python
python -c "import os,base64;print(base64.b64encode(os.urandom(48)).decode())"
```

**How it works:** each command asks the OS cryptographic random generator for 48 unpredictable
bytes, then Base64-encodes them into a text string. That text is what you put in the environment
variable. At startup `JwtConfig` Base64-decodes it back to those 48 bytes and builds the HMAC key.

Example output (yours will differ — never reuse this one):

```
9f3Kq2Xz8vN1pR4tY7wB0cD5eF6gH8jK2lM4nP6qS8u0vX1yZ3aB5cD7eF9gH1jK3l=
```

---

## 3. Set `ECOMMERCE_JWT_SECRET`

Use the value you just generated.

### Windows — current terminal only

```cmd
:: cmd
set ECOMMERCE_JWT_SECRET=PASTE_YOUR_GENERATED_VALUE_HERE
```

```powershell
# PowerShell
$env:ECOMMERCE_JWT_SECRET = "PASTE_YOUR_GENERATED_VALUE_HERE"
```

### Windows — persist for your user (new terminals)

```cmd
setx ECOMMERCE_JWT_SECRET "PASTE_YOUR_GENERATED_VALUE_HERE"
```

> `setx` writes to the user environment permanently, but it does **not** affect the terminal you
> ran it in — open a new terminal (and restart IntelliJ) so the value is picked up.

### IntelliJ Run Configuration (cleanest for development)

`Run > Edit Configurations… > (your Spring Boot app) > Environment variables` and add:

```
ECOMMERCE_JWT_SECRET=PASTE_YOUR_GENERATED_VALUE_HERE
```

This way the secret lives with the run config, not in `application.properties`, and every launch
gets it automatically.

### Verify it is set

```cmd
:: cmd
echo %ECOMMERCE_JWT_SECRET%
```

```powershell
# PowerShell
echo $env:ECOMMERCE_JWT_SECRET
```

If it prints your value, start the app. A boot failure mentioning
*"security.jwt.secret must be a valid Base64 value"* or *"must contain at least 32 bytes"* means
the value is wrong — regenerate it with a script above.

---

## 4. How the Secret Is Used at Runtime (recap)

```
ECOMMERCE_JWT_SECRET  ──▶  application.properties (security.jwt.secret)
                                     │
                                     ▼
                        JwtConfig.createSecretKey()  ── Base64 decode + length check ──▶ HmacSHA256 key
                                     │
                 ┌───────────────────┴────────────────────┐
                 ▼                                         ▼
        JwtEncoder (sign at login)             JwtDecoder (verify every request)
        AuthServiceImpl.generateToken()        SecurityConfig → oauth2ResourceServer.jwt()
```

At login the encoder signs a token containing `iss`, `sub` (email), `roles`, `userId`, `iat`, `exp`.
On every later request the decoder verifies the **signature + issuer + expiry**, and the
`JwtAuthenticationConverter` turns the `roles` claim into `ROLE_*` authorities used by
`hasRole('ADMINISTRATOR')`.

---

## 5. Get Bearer Tokens (customer & admin)

Tokens come from `POST /api/auth/login`. The IntelliJ HTTP Client can **capture the token from the
response into a variable** using a response-handler script (`> {% ... %}`), so you never copy-paste
the JWT by hand.

Open [`Backend/http/auth.http`](../Backend/http/auth.http). It uses `{{baseUrl}}` plus credentials from the private HTTP Client environment and already contains separate requests for both roles:

```http
### LOGIN — CUSTOMER (captures token into {{customerAuthToken}})
POST {{baseUrl}}/auth/login
Content-Type: application/json

{
  "email": "{{customerEmail}}",
  "password": "{{customerPassword}}"
}

> {%
    client.global.set("customerAuthToken", response.body.token);
    client.log("Customer token stored");
%}

### LOGIN — ADMIN (captures token into {{adminAuthToken}})
POST {{baseUrl}}/auth/login
Content-Type: application/json

{
  "email": "{{adminEmail}}",
  "password": "{{adminPassword}}"
}

> {%
    client.global.set("adminAuthToken", response.body.token);
    client.log("Admin token stored");
%}
```

Now any later request can authenticate with:

```http
GET http://localhost:8081/api/users/me
Authorization: Bearer {{customerAuthToken}}
```

```http
GET http://localhost:8081/api/users
Authorization: Bearer {{adminAuthToken}}
```

**What the `> {% ... %}` block does:** after the request runs, IntelliJ executes that JavaScript.
`response.body.token` reads the `token` field from the `AuthResponse` JSON, and
`client.global.set(...)` saves it under a name you can reuse anywhere as `{{name}}`. The variable
lives for the IDE session.

> The customer must exist first (Section 6.1). The admin must be seeded first (Section 6.2) — there
> is deliberately **no admin sign-up endpoint**.

---

## 6. Seeding Data

Order matters: **customer → admin → products → cart → order (checkout) → payment**, plus
**delivery** for admins. The `.http` snippets below chain together using captured variables.

### 6.1 Seed a Customer (self-service endpoint)

```http
### SEED CUSTOMER
POST http://localhost:8081/api/users/register
Content-Type: application/json

{
  "firstName": "Tiyani",
  "lastName": "Baloyi",
  "email": "tiyani@gmail.com",
  "phoneNumber": "0821234567",
  "password": "Password@12345",
  "confirmPassword": "Password@12345"
}
```

Validation rules (from `RegisterRequest`):
- **Password:** 12–64 chars, with uppercase, lowercase, a number, and a special character.
- **Email:** must be Gmail / Outlook / Hotmail / Live / `.co.za`.
- **Phone:** valid 10-digit South African mobile number.
- `password` and `confirmPassword` must match.

The password is stored only as a **BCrypt hash** — the raw value is never persisted.

### 6.2 Seed an Administrator (manual — no endpoint by design)

There is **no `register-admin` route**, so the public cannot self-promote. The role is decided by
JPA **JOINED inheritance**: a user is an admin if a row exists in the `administrator` table.
You create an admin by inserting into two tables — but the password must be a **BCrypt hash**, so
generate that first.

**Step 1 — generate a BCrypt hash** for the admin password. Easiest option: add a temporary
`CommandLineRunner`, run the app once, copy the printed hash, then delete it:

```java
// TEMPORARY — delete after use
@org.springframework.context.annotation.Bean
org.springframework.boot.CommandLineRunner printAdminHash(
        org.springframework.security.crypto.password.PasswordEncoder encoder) {
    return args -> System.out.println(
        "ADMIN HASH => " + encoder.encode("Admin@123456789"));
}
```

Alternatively, if you have the Spring Boot CLI:

```bash
spring encodepassword Admin@123456789
```

Or Python (`pip install bcrypt`) — Spring accepts the `$2a`/`$2b` prefix:

```bash
python -c "import bcrypt;print(bcrypt.hashpw(b'Admin@123456789', bcrypt.gensalt(10)).decode())"
```

**Step 2 — insert the admin** into MySQL (`app_user` = base table, `administrator` = subclass table).
Replace the hash with the one you generated:

```sql
INSERT INTO app_user
  (user_id, first_name, last_name, email, phone_number, password_hash, active, created_at, credential_version)
VALUES
  ('USR-ADMIN0000001', 'System', 'Admin', 'admin@incredibletech.co.za',
   '0110000000', '$2a$10$REPLACE_WITH_GENERATED_BCRYPT_HASH', true, NOW(), 0);

INSERT INTO administrator
  (user_id, employee_number, department)
VALUES
  ('USR-ADMIN0000001', 'EMP-0001', 'Operations');
```

`spring.jpa.hibernate.ddl-auto=update` means Hibernate creates the `app_user` and `administrator`
tables on first startup, so run the app once before inserting. After the insert, log in with
`admin@incredibletech.co.za` — the returned token's `roles` claim will be `["ADMINISTRATOR"]`.

### 6.3 Seed Products (ADMINISTRATOR token required)

Any write to `/api/productcatalog/**` requires an admin token. These requests capture the new
`productId` so you can add it to a cart later.

```http
### SEED PRODUCT — Smart Watch (captures {{watchProductId}})
POST http://localhost:8081/api/productcatalog
Authorization: Bearer {{adminAuthToken}}
Content-Type: application/json

{
  "sku": "HW-WATCH-FIT3-001",
  "productName": "Huawei Watch Fit 3 GPS 43mm",
  "description": "Huawei smartwatch with GPS tracking and fitness monitoring.",
  "brand": "HUAWEI",
  "category": "SMART_WATCHES",
  "price": 2499.99,
  "discountPrice": 2199.99,
  "stockQuantity": 50,
  "primaryImageUrl": "/images/products/smart-watches/huawei-watch-fit-3.png",
  "colour": "Black",
  "specifications": "GPS, AMOLED Display, Heart Rate Monitoring",
  "featured": true,
  "packageHeightCm": 5.0,
  "packageWidthCm": 5.0,
  "packageDepthCm": 5.0,
  "packageWeightKg": 0.5,
  "warrantyPeriodMonths": 12,
  "liquidCooled": false
}

> {% client.global.set("watchProductId", response.body.productId); %}

### SEED PRODUCT — Laptop
POST http://localhost:8081/api/productcatalog
Authorization: Bearer {{adminAuthToken}}
Content-Type: application/json

{
  "sku": "LP-LEGION-5-001",
  "productName": "Lenovo Legion 5 Gaming Laptop",
  "description": "Ryzen 7 gaming laptop with RTX graphics.",
  "brand": "LEGION",
  "category": "LAPTOPS",
  "price": 24999.00,
  "stockQuantity": 15,
  "primaryImageUrl": "/images/products/laptops/legion-front.png",
  "colour": "Storm Grey",
  "storage": "1TB SSD",
  "memory": "16GB DDR5",
  "processor": "AMD Ryzen 7 7840HS",
  "graphicsCard": "NVIDIA RTX 4060",
  "operatingSystem": "Windows 11",
  "warrantyPeriodMonths": 24,
  "featured": true,
  "liquidCooled": false
}

### SEED PRODUCT — Hardware Component (uses componentType)
POST http://localhost:8081/api/productcatalog
Authorization: Bearer {{adminAuthToken}}
Content-Type: application/json

{
  "sku": "HW-GPU-RTX5070-001",
  "productName": "NVIDIA GeForce RTX 5070",
  "description": "High-performance graphics card.",
  "brand": "NVIDIA",
  "category": "COMPUTER_COMPONENTS",
  "price": 13499.00,
  "stockQuantity": 20,
  "primaryImageUrl": "/images/products/components/nvidia-rtx-5070.png",
  "specifications": "12GB GDDR7, PCIe 5.0",
  "componentType": "GRAPHICS_CARD",
  "powerRequirementWatts": 250,
  "componentFormFactor": "Dual-slot",
  "warrantyPeriodMonths": 36,
  "featured": false,
  "liquidCooled": false
}
```

**Valid enum values** (must match the Java constant names exactly — see [`ENUMS.md`](./ENUMS.md)):
- `brand`: `APPLE, SAMSUNG, HUAWEI, HONOR, LENOVO, LEGION, MSI, ASUS, VICTUS, REDRAGON, LOGITECH, NVIDIA, CORSAIR, MICROSOFT, PLAYSTATION, INCREDIBLE_TECH, OTHER`
- `category`: `LAPTOPS, DESKTOP_COMPUTERS, COMPUTER_COMPONENTS, SMARTPHONES, SMART_WATCHES, ACCESSORIES`
- `componentType` (optional, components only): `GRAPHICS_CARD, MEMORY, ...`

> A **customer** token here is rejected — the `/api/productcatalog/**` write rule requires
> `ADMINISTRATOR`.

### 6.4 Seed a Cart Item, then Checkout to create an Order

Payments require an existing order, and orders are created from the customer's active cart.

```http
### ADD ITEM TO CART (customer)
POST http://localhost:8081/api/cart/items
Authorization: Bearer {{customerAuthToken}}
Content-Type: application/json

{
  "productId": "{{watchProductId}}",
  "quantity": 1
}

### CHECKOUT — creates the order (captures {{orderId}})
POST http://localhost:8081/api/orders/checkout
Authorization: Bearer {{customerAuthToken}}
Content-Type: application/json

{
  "recipientName": "Tiyani Baloyi",
  "recipientPhone": "0821234567",
  "streetAddress": "12 Long Street",
  "suburb": "Cape Town City Centre",
  "city": "Cape Town",
  "province": "Western Cape",
  "postalCode": "8001",
  "country": "South Africa",
  "deliveryInstructions": "Leave at reception"
}

> {% client.global.set("orderId", response.body.orderId); %}
```

### 6.5 Seed a Payment

Two payment types are supported. Both need the `orderId` from checkout and a `billingLocation`.

```http
### PAYMENT — CARD (captures {{transactionId}})
POST http://localhost:8081/api/payments/card
Authorization: Bearer {{customerAuthToken}}
Content-Type: application/json

{
  "orderId": "{{orderId}}",
  "billingLocation": {
    "billingName": "Tiyani Baloyi",
    "streetAddress": "12 Long Street",
    "suburb": "Cape Town City Centre",
    "city": "Cape Town",
    "province": "Western Cape",
    "postalCode": "8001",
    "country": "South Africa"
  },
  "paymentToken": "tok_test_1234567890abcdef",
  "cardBrand": "VISA",
  "cardholderName": "Tiyani Baloyi",
  "cardLastFourDigits": "4242"
}

> {% client.global.set("transactionId", response.body.transactionId); %}

### PAYMENT — DIGITAL WALLET (alternative)
POST http://localhost:8081/api/payments/wallet
Authorization: Bearer {{customerAuthToken}}
Content-Type: application/json

{
  "orderId": "{{orderId}}",
  "billingLocation": {
    "billingName": "Tiyani Baloyi",
    "streetAddress": "12 Long Street",
    "suburb": "Cape Town City Centre",
    "city": "Cape Town",
    "province": "Western Cape",
    "postalCode": "8001",
    "country": "South Africa"
  },
  "walletProvider": "GOOGLE_PAY",
  "walletToken": "wallet_tok_1234567890abcdef"
}
```

Notes:
- `cardBrand`: `VISA, MASTERCARD, AMERICAN_EXPRESS, OTHER`.
- `walletProvider`: `APPLE_PAY, GOOGLE_PAY, SAMSUNG_PAY, PAYPAL, OTHER`.
- `paymentToken` / `walletToken` must be 12–500 chars (a simulated gateway processes them).
- `cardLastFourDigits` and `postalCode` must be exactly 4 digits.

### 6.6 Seed a Delivery (ADMINISTRATOR)

`POST /api/deliveries` is admin-only. Use the `orderId` from checkout.

```http
### SEED DELIVERY (admin)
POST http://localhost:8081/api/deliveries
Authorization: Bearer {{adminAuthToken}}
Content-Type: application/json

{
  "deliveryId": "DEL-0000000001",
  "orderId": "{{orderId}}",
  "trackingNumber": "TRK-INCTECH-0001",
  "deliveryStatus": "PREPARING",
  "deliveryAddress": {
    "recipientName": "Tiyani Baloyi",
    "recipientPhone": "0821234567",
    "streetAddress": "12 Long Street",
    "suburb": "Cape Town City Centre",
    "city": "Cape Town",
    "province": "Western Cape",
    "postalCode": "8001",
    "country": "South Africa"
  }
}

### ADD A TRACKING EVENT to that delivery (admin)
POST http://localhost:8081/api/deliveries/DEL-0000000001/tracking
Authorization: Bearer {{adminAuthToken}}
Content-Type: application/json

{
  "deliveryStatus": "DISPATCHED",
  "currentLocation": "Cape Town Depot",
  "statusMessage": "Parcel dispatched from the depot"
}
```

`deliveryStatus` values: `PREPARING, DISPATCHED, IN_TRANSIT, OUT_FOR_DELIVERY, DELIVERED, DELIVERY_FAILED, CANCELLED`.

---

## 7. IntelliJ HTTP Client Tips

- Run any request by clicking the green ▶ gutter icon next to `###`.
- Run all requests in a file top-to-bottom to seed everything in one pass (log in first so the
  `{{customerAuthToken}}` / `{{adminAuthToken}}` variables get set before the requests that use them).
- Select the tracked `local` environment from the `Backend/http/http-client.env.json`; it provides `baseUrl` as `http://localhost:8081/api`.
- Copy `Backend/http/http-client.private.env.example.json` to `Backend/http/http-client.private.env.json` and fill in `adminEmail`, `adminPassword`, `customerEmail`, and `customerPassword` locally.
- The real private file is ignored. Never commit credentials or tokens.

---

## 8. Quick Checklist

1. Generate secret → `openssl rand -base64 48` (or the PowerShell script).
2. Set `ECOMMERCE_JWT_SECRET` (IntelliJ run config or `setx`).
3. Start the app on port 8081; confirm no boot error about the JWT secret.
4. `POST /api/users/register` → seed the customer.
5. Insert the admin row (with a BCrypt hash) into `app_user` + `administrator`.
6. Log in as customer and admin in `Backend/http/auth.http` → capture `{{customerAuthToken}}` / `{{adminAuthToken}}`.
7. Admin: seed products (and deliveries). Customer: add to cart → checkout → pay.
