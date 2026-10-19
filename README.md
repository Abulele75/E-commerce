# IncredibleTech

### IncredibleTech is an e-commerce platform born right in the IT labs of CPUT District Six. We skipped the corporate middleman to bring reliable, high-quality pre-built laptops and electronics directly to everyday South Africans at a price that won't break the bank. No tech-jargon, no massive markups just great deals and fast local delivery.

# E-commerce

## Group Members

* **Member 1**: TIYANI NGWANA 231266731 - Payment Entity ( CardPayment, DigitalWalletPayment, BillingLocation)
* **Member 2**: ABULELE NTWANAMBI 218276400 - Feedback Entity (ProductReview, DeliveryServiceReview, ContentModeration)
* **Member 3**: NOMHLE NJENGELE 216227488 - ProductCatalog Entity (HardwareComponent, PreBuiltSystem, DimensionSpecs)
* **Member 4**: OWENKOSI NXASANA 230240887 - UserManagement Entity (Customer, Administrator, AccountProfile)
* **Member 5**: SINAZO NTSIMBI 222765208 - Delivery Entity (DeliveryAddress)
* **Member 6**: SINETHEMBA NYIMBINYA 220085870 - Order Entity (OrderItem,FinancialBreakdown)

## Entities

* **UserManagement**:This handles the people using the system. It keeps track of the differences between customers (who buy things) and administrators (staff members who manage the store).
* **ProductCatalog**:This is the store's inventory. It manages the lists of individual computer parts (like graphics cards or processors) and full, ready-to-use desktop computers that are for sale.
* **Order**:This handles the checkout process. When a customer buys things, this creates a main receipt that lists every single item they purchased and tracks the overall status of the purchase.
* **Payment**:It safely records and processes the financial transactions when a customer pays using credit cards or digital wallets.
* **Delivery**: Logistics tracking, airway bills, and geographic transport milestones.
* **Feedback**: Customer-submitted product reviews and delivery service rating matrices.

## Contributing Guidelines

### Pull Request Process

1. Create a development branch named explicitly with your student number.
2. Implement your vertical layer feature completely alongside corresponding Test-Driven Development (TDD) fixtures.
3. Execute localized compilation to ensure all engineering tests pass successfully.
4. Update class-level documentation if additional properties are added to standard models.
5. Open a formal Pull Request (PR) directing your branch into the `main` trunk.
6. Request an architectural review validation from the team lead before merging.
7. Address any code quality review comments promptly.
8. Merge securely into `main` after receiving official approval.

### Code Review Checklist

* [ ] **Follows Builder Pattern**: Completely hand-coded without any external framework extensions.
* [ ] **Strict Immutability**: Contains public accessors but **zero setter methods**.
* [ ] **Validation Failure Logic**: Intentionally returns `null` instead of throwing execution exceptions when invariants fail.
* [ ] **Includes TDD Tests**: Validates functional parameters using clean JUnit 5 text runners.
* [ ] **Proper Package Placement**: Strictly fits root package domains (`domain`, `factory`, `repository`, `service`, `controller`, % `helper`).
* [ ] **Author Comments Included**: Structured file header metadata block visible at the absolute top of the source code file.
* [ ] **No Merge Conflicts**: Branch line matches cleanly with upstream components.
* [ ] **All Tests Are Passing**

## UML Diagram

<img width="1536" height="1024" alt="final uml" src="https://github.com/user-attachments/assets/eda6382a-5bf6-4091-8384-1de4554c8981" />


## Quick start

### 1. Generate the JWT secret

Run this once in PowerShell:

```powershell
$bytes = New-Object byte[] 32; $rng = [Security.Cryptography.RandomNumberGenerator]::Create(); $rng.GetBytes($bytes); [Convert]::ToBase64String($bytes); $rng.Dispose()
```

Copy the Base64 value printed by the command. Do not commit it to Git.

### 2. Store the secret in IntelliJ IDEA

1. Import `Backend/pom.xml` as the Maven project.
2. Open **Run → Edit Configurations**.
3. Select the Spring Boot configuration for `ECommerceApplication`.
4. Add this environment variable:

```text
ECOMMERCE_JWT_SECRET=paste-generated-value-here
PASSWORD_RESET_EXPOSE_TOKEN=true
```

5. Apply the configuration and run the backend. It starts on `http://localhost:8081`. `PASSWORD_RESET_EXPOSE_TOKEN=true` enables the local-only reset-token handoff; keep it false outside local development.

You can alternatively start it from a terminal at the repository root:

```powershell
.\Backend\mvnw.cmd spring-boot:run
```

### 3. Start the React frontend

Run these commands from the repository root:

```powershell
Set-Location .\Frontend
npm install
npm run dev
```

Open the local URL printed by Vite, normally `http://localhost:5173`.

### 4. Configure and run clean seeding

1. All request and seed files are under `Backend/http/`.
2. Copy `Backend/http/http-client.private.env.example.json` to `Backend/http/http-client.private.env.json` and enter the customer and administrator credentials.
3. Run either `Backend/http/auth.http` with IntelliJ's `local` environment selected, or run the login requests in `Backend/http/seeding/User_and_Admin_seeding.http`; both capture `customerAuthToken` and `adminAuthToken`.
4. Use `Backend/http/seeding/User_and_Admin_seeding.http` to create a customer and register the account that will become the administrator. Promote that account in MySQL using the SQL instructions in the file; public registration intentionally creates customers only.
5. Run the product files under `Backend/http/catalog/seed/` with the captured administrator token.
6. Run cart, checkout, payment, delivery, and feedback examples under `Backend/http/seeding/` as needed.
7. Password recovery examples are in `Backend/http/password-reset.http`; local reset tokens expire after 15 minutes and work once.

Recommended clean order: **backend → customer/admin → login tokens → products → cart → checkout → payment → delivery/feedback**. More detail is available in `docs/SECURITY_SETUP_AND_SEEDING.md`.

## Project structure

- `docs/` — architecture, security, API, product, payment, and frontend guides.
- `Backend/` — Spring Boot API, Maven wrapper, tests, and the consolidated `http/` request collection.
- `Frontend/` — React/Vite storefront and administrator interface.

Run backend verification from `Backend/` with `mvnw.cmd clean verify` and build the frontend from `Frontend/` with `npm.cmd run build`.
