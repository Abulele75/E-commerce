# Security Setup & Seeding Guide

This guide explains how to configure and use authentication and authorization in this application, then seed the application end to end with the IntelliJ HTTP Client files in [`SEEDING DATA`](./SEEDING%20DATA/).

The backend runs at `http://localhost:8081`, as configured by `server.port=8081`.

## 1. Security configuration

The JWT settings in `src/main/resources/application.properties` are:

```properties
security.jwt.secret=${ECOMMERCE_JWT_SECRET}
security.jwt.issuer=e-commerce-api
security.jwt.expiration-seconds=3600
```

`ECOMMERCE_JWT_SECRET` is an environment variable. The signing secret is therefore supplied at runtime instead of being stored in source control. Because there is no default value, the application cannot create its JWT encoder and decoder when the variable is missing or invalid.

> The issuer is exactly `e-commerce-api`. A token with a different issuer fails validation.

## 2. Why the JWT secret is required

The application signs tokens with HMAC-SHA256 (`HS256`). This is a symmetric algorithm: the same secret signs tokens during login and verifies Bearer tokens on later requests.

`JwtConfig.createSecretKey(...)` applies these startup rules:

| Rule | Reason |
|---|---|
| The environment value must be valid standard Base64 | `Base64.getDecoder()` converts it into key bytes. |
| The decoded value must contain at least 32 bytes | HS256 requires a key at least as large as its 256-bit output. |

A plain password such as `mysecret` is not suitable. Generate 48 cryptographically random bytes and Base64-encode them. This produces a 64-character Base64 value before optional padding and exceeds the minimum requirement.
## 3. Generate and set the secret

Generate a new secret for each environment. Do not reuse the example output from documentation or commit a real secret.

### Windows PowerShell

Run these commands in the same PowerShell window that will start the backend:

```powershell
$bytes = New-Object 'System.Byte[]' 48
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
try {
    $rng.GetBytes($bytes)
} finally {
    $rng.Dispose()
}
$env:ECOMMERCE_JWT_SECRET = [System.Convert]::ToBase64String($bytes)
$env:ECOMMERCE_JWT_SECRET
.\mvnw.cmd spring-boot:run
```

The environment assignment applies to the current PowerShell process and programs started from it. Opening a new terminal requires setting it again.

### Windows Command Prompt

From `cmd.exe`, use PowerShell only as the cryptographically secure generator and assign its output to the current Command Prompt session:

```bat
for /f "delims=" %S in ('powershell -NoProfile -Command "$b=New-Object byte[] 48;[System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($b);[Convert]::ToBase64String($b)"') do @set "ECOMMERCE_JWT_SECRET=%S"
mvnw.cmd spring-boot:run
```

When placing the command in a `.bat` file, replace `%S` with `%%S`.

### OpenSSL

Generate a value:

```shell
openssl rand -base64 48
```

Copy the output, then set it before starting the application:

```powershell
$env:ECOMMERCE_JWT_SECRET = "PASTE_GENERATED_VALUE_HERE"
.\mvnw.cmd spring-boot:run
```

OpenSSL can also be assigned directly in PowerShell:

```powershell
$env:ECOMMERCE_JWT_SECRET = (openssl rand -base64 48)
.\mvnw.cmd spring-boot:run
```

### Node.js

Generate a value:

```shell
node -e "console.log(require('node:crypto').randomBytes(48).toString('base64'))"
```

Or generate, assign, and start from PowerShell:

```powershell
$env:ECOMMERCE_JWT_SECRET = (node -e "process.stdout.write(require('node:crypto').randomBytes(48).toString('base64'))")
.\mvnw.cmd spring-boot:run
```

### What happens at startup

1. Spring resolves `${ECOMMERCE_JWT_SECRET}`.
2. `JwtConfig` Base64-decodes the value.
3. Startup stops if decoding fails or produces fewer than 32 bytes.
4. The decoded bytes become an `HmacSHA256` `SecretKey`.
5. `NimbusJwtEncoder` uses the key to issue HS256 tokens.
6. `NimbusJwtDecoder` uses the same key to verify HS256 signatures.
7. The decoder also validates token timestamps and requires issuer `e-commerce-api`.

Example shape only—generate your own value:

```text
<64-character Base64 value generated from 48 random bytes>
```

## 4. Authentication at runtime

### Password handling

`PasswordEncoderConfig` provides Spring Security's `BCryptPasswordEncoder`.

- Registration hashes the submitted password before storing it.
- Login compares the submitted password with the stored BCrypt hash by calling `PasswordEncoder.matches(...)`.
- The API never needs to decrypt a password.
- `User.passwordHash` is annotated with `@JsonIgnore`, so it is excluded from normal JSON serialization.

### Login and token creation

Login is public:

```http
POST http://localhost:8081/api/auth/login
Content-Type: application/json

{
  "email": "tiyani@gmail.com",
  "password": "Password@12345"
}
```

A successful response has these fields:

```json
{
  "token": "eyJ...",
  "type": "Bearer",
  "userId": "USR-...",
  "email": "tiyani@gmail.com",
  "role": "CUSTOMER"
}
```

`AuthServiceImpl` puts the following claims in the token:

| Claim | Application value |
|---|---|
| `iss` | `e-commerce-api` |
| `iat` | Time of login |
| `exp` | Login time plus 3,600 seconds |
| `sub` | User email address |
| `roles` | A one-item array containing `CUSTOMER` or `ADMINISTRATOR` |
| `userId` | The application's user ID |

The JWT header specifies `alg=HS256` and `typ=JWT`.

### Bearer-token verification

The application uses Spring Security's OAuth2 Resource Server support rather than a custom JWT filter. Send the login token on protected requests:

```http
Authorization: Bearer {{customerToken}}
```

The configured `JwtAuthenticationConverter` reads the `roles` claim and adds the `ROLE_` prefix. The resulting authorities are:

- `CUSTOMER` becomes `ROLE_CUSTOMER`.
- `ADMINISTRATOR` becomes `ROLE_ADMINISTRATOR`.

This is why application checks use `hasRole('ADMINISTRATOR')`, while the token itself contains `ADMINISTRATOR`.

### Token lifetime and sessions

The API is stateless:

- `SessionCreationPolicy.STATELESS` prevents authentication from being stored in an HTTP session.
- Every protected request must include its Bearer token.
- Tokens expire after one hour.
- The application does not currently issue refresh tokens or keep a token-revocation store.
- Log in again after expiration to obtain a new token.
- A role change does not modify an already-issued token; log in again after changing a user subtype.

## 5. Authorization rules

`SecurityConfig` enables both URL rules and method-level security with `@EnableMethodSecurity`.

### Public requests

These requests do not require a token:

- CORS preflight requests: `OPTIONS /**`
- Error handling: `/error`
- Authentication: `/api/auth/**`
- Customer registration: `POST /api/users/register`
- Catalogue reads: `GET /api/productcatalog/**`
- Product-review reads: `GET /api/feedback/product/**`

### Administrator requests

All non-GET catalogue operations under `/api/productcatalog/**` require `ADMINISTRATOR`. Method-level rules also protect administration operations such as:

- Listing, activating, and deactivating users
- Listing all orders and changing order status
- Listing all payments and issuing refunds
- Creating, listing, changing, deleting, and tracking deliveries
- Updating or deleting feedback

### Other authenticated requests

Every route not matched above requires a valid JWT. Customer-facing services additionally enforce domain rules such as ownership of a cart, order, or payment.

The URL configuration does not apply a blanket `CUSTOMER` role to every customer-facing endpoint. In practice, use:

- The customer token for cart, checkout, customer payment, and feedback requests.
- The administrator token for catalogue management, delivery management, and administrative operations.

Expected security responses:

- `401 Unauthorized`: the token is missing, malformed, expired, signed with another secret, or has the wrong issuer.
- `403 Forbidden`: the token is valid, but its role is not allowed to perform the operation.

CSRF is disabled because this is a stateless Bearer-token API. CORS is enabled through the application's `CorsConfigurationSource`.

## 6. Customer and administrator roles

The role is derived from the concrete JPA subtype:

- A row in `customer` represents a `Customer` and returns `UserRole.CUSTOMER`.
- A row in `administrator` represents an `Administrator` and returns `UserRole.ADMINISTRATOR`.
- Both subtypes share their base account data through `app_user` using joined inheritance.

There is no public administrator-registration endpoint and no mutable role column. `POST /api/users/register` always creates a `Customer`.

### Local seeding: convert the registered admin candidate

Run the administrator registration request in `User_and_Admin_seeding.http`. Before that account has customer-owned application data, convert its subtype in MySQL:

```sql
START TRANSACTION;

SET @admin_user_id = (
    SELECT user_id
    FROM app_user
    WHERE email = 'admin@incredibletech.co.za'
);

DELETE FROM customer
WHERE user_id = @admin_user_id;

INSERT INTO administrator
    (user_id, employee_number, department)
VALUES
    (@admin_user_id, 'EMP-0001', 'Operations');

COMMIT;
```

Do not leave the same user ID in both subtype tables. This conversion is intended only for controlled local seeding. A production application should use a dedicated, audited administrator-provisioning process.

After conversion, run the administrator login request. The new token must return:

```json
{
  "type": "Bearer",
  "email": "admin@incredibletech.co.za",
  "role": "ADMINISTRATOR"
}
```

Always log in after promotion. A token issued before promotion still contains the old `CUSTOMER` claim until it expires.

## 7. IntelliJ HTTP Client conventions

The `.http` files use IntelliJ HTTP Client variables:

```http
@customerToken = PASTE_CUSTOMER_JWT_HERE
@productId = PASTE_CREATED_PRODUCT_ID_HERE
```

A variable is referenced with double braces:

```http
Authorization: Bearer {{customerToken}}

{
  "productId": "{{productId}}"
}
```

Do not commit real JWTs. Leave placeholders in version-controlled files and paste short-lived development tokens only into your local working copy.

## 8. End-to-end seeding order

Keep the backend running on port `8081`, then execute the requests in the following order.

### Step 1: Register and log in the customer

Open `SEEDING DATA/User_and_Admin_seeding.http` and run:

1. `SEED CUSTOMER`
2. `LOGIN — CUSTOMER`

Copy the response's `token` value. Paste it into each required `@customerToken` placeholder without adding the word `Bearer`; the request header already adds it.

### Step 2: Register and convert the administrator

In the same file:

1. Run the administrator-candidate registration request.
2. Perform the MySQL subtype conversion shown above.
3. Run `LOGIN — ADMIN`.
4. Confirm that the response role is `ADMINISTRATOR`.
5. Copy the response's `token` into each `@adminToken` placeholder.

### Step 3: Seed products

Open `SEEDING DATA/product_seeding.http`:

```http
@adminToken = PASTE_ADMIN_JWT_HERE
```

The file creates products with authenticated `POST /api/productcatalog` requests. Catalogue creation requires the administrator role. Prices are numeric South African rand amounts; for example, `23999.00` represents `R23,999.00`.

The API generates each product ID. Save at least one returned `PRD-*` value for cart, order, and feedback seeding.

### Step 4: Create a pending order

Open `SEEDING DATA/order_seeding.http` and set:

```http
@customerToken = PASTE_CUSTOMER_JWT_HERE
@productId = PASTE_CREATED_PRODUCT_ID_HERE
```

Run the requests in order:

1. `POST /api/cart/items` adds an available product to the authenticated customer's active cart.
2. `POST /api/orders/checkout` converts the cart contents into an order with status `PENDING_PAYMENT`.

Copy the returned `ORD-*` order ID.

### Step 5: Pay for the order

Open `SEEDING DATA/payment_seeding.http` and set the customer token and pending order ID.

A successful payment:

1. Validates that the authenticated customer owns the order.
2. Requires order status `PENDING_PAYMENT`.
3. Checks and deducts product stock.
4. Marks the payment `SUCCESSFUL`.
5. Marks the order `PAID`.
6. Marks the source cart `CHECKED_OUT`.

The card and wallet examples require two different pending orders:

```http
@cardOrderId = PASTE_PENDING_CARD_ORDER_ID_HERE
@walletOrderId = PASTE_DIFFERENT_PENDING_WALLET_ORDER_ID_HERE
```

To run both examples:

1. Create an order and pay it with the card request.
2. Run `order_seeding.http` again to create a new active cart and a second pending order.
3. Use the second order ID for the wallet request.

Do not pay the same order twice. After the first successful payment, it is no longer `PENDING_PAYMENT`.

### Step 6: Create and track delivery

Open `SEEDING DATA/delivery_seeding.http` and set:

```http
@adminToken = PASTE_ADMIN_JWT_HERE
@orderId = PASTE_PAID_ORDER_ID_HERE
@deliveryId = DEL-0001
```

Run delivery creation first, followed by tracking requests in this order:

1. `PREPARING` on the created delivery
2. `DISPATCHED`
3. `IN_TRANSIT`
4. `OUT_FOR_DELIVERY`
5. `DELIVERED`

Delivery creation and tracking require the administrator token. Change `DEL-0001` and its tracking number before reseeding if those identifiers already exist.

### Step 7: Seed feedback

Open `SEEDING DATA/feedback_seeding.http` and set:

```http
@customerToken = PASTE_CUSTOMER_JWT_HERE
@productId = PASTE_PURCHASED_PRODUCT_ID_HERE
```

Run the product-review and delivery-service-review requests. The current delivery-review request model also calls its product reference `targetProductId`, so the file intentionally supplies a `PRD-*` value for both review types.

## 9. Troubleshooting

### Application fails during startup

- Confirm `ECOMMERCE_JWT_SECRET` is set in the same terminal that starts the backend.
- Confirm the value is valid Base64.
- Confirm it decodes to at least 32 bytes.
- Regenerate the secret rather than attempting to use a normal word or password.

### Login succeeds but a protected request returns 401

- Copy only the `token` value from the login response.
- Keep the header in the form `Authorization: Bearer {{tokenVariable}}`.
- Ensure the token has not passed its one-hour expiration.
- Ensure the backend was not restarted with a different secret after the token was issued.

### Administrator request returns 403

- Confirm the login response contains `"role": "ADMINISTRATOR"`.
- Confirm the customer subtype row was removed during conversion.
- Log in again after the database conversion and use the newly issued token.

### Product creation returns 403

`POST /api/productcatalog` is catalogue management and requires the administrator token. Public catalogue access applies only to `GET` requests.

### Payment fails

- The order must belong to the logged-in customer.
- The order must still be `PENDING_PAYMENT`.
- The products must have enough available stock.
- Use separate orders for the card and wallet examples.

### A seed request reports a duplicate value

Registration emails, phone numbers, employee numbers, product SKUs, delivery IDs, and similar identifiers may be unique. Either clear the local seed data or change the relevant value before rerunning that request.

## 10. Security practices

- Never commit `ECOMMERCE_JWT_SECRET`, real JWTs, production credentials, or payment credentials.
- Use a different secret for development, testing, staging, and production.
- Rotate a compromised secret immediately. Existing tokens signed with the previous secret will stop validating after rotation.
- Keep tokens out of logs and screenshots.
- Treat the credentials in the seed files as local demonstration accounts only.
- Externalize database credentials and other deployment secrets rather than hardcoding them in application configuration.
- Use HTTPS outside local development so credentials and Bearer tokens are encrypted in transit.
- Provision production administrators through an audited workflow instead of direct ad hoc database changes.
