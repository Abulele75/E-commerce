# Seeding Products with IntelliJ HTTP Client

The HTTP Client collection is under [`Backend/http/`](../Backend/http/). It contains **44 seed products across five populated categories**; `DESKTOP_COMPUTERS` is supported by the API but has no seed payload.

| Seed file | Category | Products |
|---|---|---:|
| `Backend/http/catalog/seed/smart-watches.http` | Smart Watches | 9 |
| `Backend/http/catalog/seed/smartphones.http` | Smartphones | 17 |
| `Backend/http/catalog/seed/laptops.http` | Laptops | 4 |
| `Backend/http/catalog/seed/accessories.http` | Accessories | 9 |
| `Backend/http/catalog/seed/computer-components.http` | Computer Components | 5 |
| | **Total** | **44** |

The separate `Backend/http/catalog/catalog.http` file contains safe read-only catalogue examples and does not create additional products.

## Prerequisites

1. Run MySQL with an `ecommerce` database. Set `ECOMMERCE_DB_USERNAME` when the `root` default is unsuitable, and set the required `ECOMMERCE_DB_PASSWORD`; do not commit credentials.
2. Set `ECOMMERCE_JWT_SECRET` to a valid Base64 value that decodes to at least 32 bytes.
3. Start the backend on `http://localhost:8081`.
4. Ensure an administrator account exists. Public registration creates only `CUSTOMER` accounts; follow [`SECURITY_SETUP_AND_SEEDING.md`](SECURITY_SETUP_AND_SEEDING.md) for administrator setup.

## Configure IntelliJ credentials

Copy `Backend/http/http-client.private.env.example.json` to `Backend/http/http-client.private.env.json`, fill in the four local credential values, and leave the private file uncommitted. Select the `local` HTTP Client environment, whose tracked `baseUrl` is `http://localhost:8081/api`.

## Authenticate and seed

1. Open `Backend/http/auth.http`. Run the optional sample registration only when the customer does not exist.
2. Run the customer login to capture `response.body.token` as `customerAuthToken`.
3. Run the administrator login to capture `response.body.token` as `adminAuthToken`.
4. Run one or more files in `Backend/http/catalog/seed/`. Product creation requires the captured administrator token.

Each successful create returns `201 Created`. SKU values are unique and creation is not idempotent: rerunning a payload after it has been seeded will fail because duplicate SKUs are rejected.

## Payload rules

Every product requires `sku`, `productName`, `brand`, `category`, a positive `price`, and a non-negative `stockQuantity`. `LAPTOPS` and `DESKTOP_COMPUTERS` additionally require `operatingSystem`; `COMPUTER_COMPONENTS` requires `componentType`. A `discountPrice`, when supplied, must be below `price`.

Valid categories are `LAPTOPS`, `DESKTOP_COMPUTERS`, `COMPUTER_COMPONENTS`, `SMARTPHONES`, `SMART_WATCHES`, and `ACCESSORIES`. See [`ENUMS.md`](ENUMS.md) for all enum values.
