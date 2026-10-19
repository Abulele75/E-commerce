# IncredibleTech E-Commerce — How Everything Works Together

This guide explains, in plain language, how the whole system fits together: the
main business parts (customer, admin, product, payment, order, delivery,
feedback), how they connect to the React frontend, and how a single feature
travels through the backend layers (domain → repository → factory → helper →
service → controller).

Read this top to bottom the first time. After that, use it as a map.

---

## 1. The Big Picture

The project has two halves that talk over HTTP:

```
┌─────────────────────┐         JSON over HTTP         ┌──────────────────────┐
│   FRONTEND (React)  │  ───────────────────────────▶  │ BACKEND (Spring Boot)│
│   Frontend/src      │                                │ Backend/src/main/java/...│
│                     │  ◀───────────────────────────  │                      │
│  pages → services → │        JSON responses          │  controller → service│
│  apiClient (fetch)  │                                │  → repository → DB   │
└─────────────────────┘                                └──────────────────────┘
```

- The **frontend** is what the user sees and clicks. It never touches the
  database directly. It only makes HTTP requests.
- The **backend** holds all the rules and the database. It decides what is
  allowed, calculates totals, checks stock, and stores everything.
- They connect through a single base URL (`http://localhost:8081/api` by
  default) using a JWT token for login.

The most important idea: **the backend is the source of truth.** The frontend
just asks questions and shows answers.

---

## 2. The Main Business Parts (and how they relate)

Think of these as the "nouns" of the shop. Here is how they connect:

```
                        ┌──────────────┐
                        │     User     │  (abstract base)
                        └──────┬───────┘
                 ┌─────────────┴─────────────┐
                 ▼                            ▼
          ┌────────────┐              ┌───────────────┐
          │  Customer  │              │ Administrator │
          └─────┬──────┘              └───────┬───────┘
                │ shops                        │ manages
                ▼                              ▼
   Cart ─▶ Order ─▶ Payment            Products, Orders, Users
                │        │
                ▼        ▼
            Delivery   (updates stock)
                │
                ▼
            Feedback (customer reviews a product/delivery)
```

**Customer** — a person who shops. Has a cart, places orders, pays, receives
deliveries, and leaves feedback. Identified by a `customerNumber`.

**Administrator** — a staff member. Manages products, updates order statuses,
and oversees users. Has an `employeeNumber` and a `department`.

Both Customer and Administrator are types of **User**. They share login,
password, and profile through the shared `User` base class. The difference is
their **role** (`CUSTOMER` vs `ADMINISTRATOR`), which controls what they are
allowed to do.

**Product** (`ProductCatalog`) — something for sale (laptop, GPU, keyboard...).
Knows its price, discount, stock level, and status (`IN_STOCK`, `LOW_STOCK`,
`OUT_OF_STOCK`, `DISCONTINUED`). It protects its own stock: you cannot sell more
than exists.

**Order** — a snapshot of what a customer is buying. Built from the cart at
checkout. It copies product names and prices at that moment (a "snapshot") so
that later price changes do not rewrite history. It moves through statuses:
`PENDING_PAYMENT → PAID → PROCESSING → SHIPPED → DELIVERED` (or `CANCELLED` /
`REFUNDED`).

**Payment** — the money side of one order. Can be a `CardPayment` or a
`DigitalWalletPayment`. Talks to a payment gateway, and on success it marks the
order paid and reduces product stock.

**Delivery** — tracks getting the order to the customer (tracking number,
delivery status, address).

**Feedback** — a customer's review, either about a product (`ProductReview`) or
about the delivery service (`DeliveryServiceReview`). Goes through content
moderation before showing publicly.

### How they connect in one sentence each
- A **Customer** fills a **Cart**.
- The **Cart** becomes an **Order** at checkout.
- The **Order** is settled by a **Payment**.
- A successful **Payment** reduces **Product** stock and triggers a **Delivery**.
- After delivery, the **Customer** can leave **Feedback**.
- An **Administrator** watches over products, orders, and users the whole time.

---

## 3. The Backend Layers (the most important thing to understand)

Every business part is built from the same six layers. Once you understand the
layers for one part (say, Order), you understand them for all of them. Each
layer has ONE job.

```
   HTTP request from frontend
            │
            ▼
   ┌─────────────────┐   "the front door"       Handles web stuff only:
   │   Controller    │                           URLs, HTTP verbs, who is
   └────────┬────────┘                           logged in, status codes.
            │ calls
            ▼
   ┌─────────────────┐   "the brain"             All business rules and
   │    Service      │                           decisions live here.
   └────────┬────────┘                           Coordinates everything.
            │ uses
     ┌──────┼───────────────┬──────────────┐
     ▼      ▼               ▼              ▼
 ┌────────┐ ┌──────────┐ ┌────────┐  ┌──────────┐
 │Factory │ │ Helper   │ │Domain  │  │Repository│
 └────────┘ └──────────┘ └────────┘  └────┬─────┘
 builds     small reusable  the             │ reads/writes
 objects    utilities       "nouns" +       ▼
 safely                     their rules   ┌──────────┐
                                          │ Database │
                                          └──────────┘
```

### 3.1 Domain — the "nouns" and their rules
Location: `Backend/src/main/java/.../domain/`

Domain classes are the real-world things: `Order`, `Customer`, `Payment`,
`ProductCatalog`. They are stored in the database (marked `@Entity`).

Important: they are **not** just bags of data. They protect their own rules.
Examples from the real code:
- `Order.markPaid()` refuses to run unless the order is `PENDING_PAYMENT`.
- `ProductCatalog.decreaseStock()` throws an error if you try to remove more
  stock than exists.
- `Payment.markRefunded()` only works on a `SUCCESSFUL` payment.

This is called "rich domain" — the object guards itself, so no other layer can
put it into an invalid state.

Many domain objects use a **Builder** (e.g. `Order.Builder`, `Customer.Builder`)
so they are created step by step and are read-only afterward.

### 3.2 Repository — the database doorway
Location: `Backend/src/main/java/.../repository/`

Repositories are interfaces that read and write domain objects to the database.
They extend Spring's `JpaRepository`, which gives `save()`, `findById()`,
`findAll()`, `delete()` for free.

You add custom lookups by declaring a method or a query, for example in
`OrderRepository`:
- `findAllForCustomer(email)` — every order for one customer.
- `findByIdForUpdate(orderId)` — locks the row so two payments cannot process
  the same order at once (important for money and stock).

The repository is the ONLY layer allowed to touch the database.

### 3.3 Factory — the safe object builder
Location: `Backend/src/main/java/.../factory/`

A factory is a helper whose only job is to **create a valid domain object**, or
return `null` if the inputs are bad. It centralizes creation rules so they are
not copy-pasted everywhere.

Example: `OrderFactory.createOrder(...)` checks that the customer is active, the
cart is not empty, the cart belongs to that customer, and totals exist. Only
then does it generate an ID and build the `Order`. If anything is wrong, it
returns `null`, and the service turns that into a clear error.

Factories also generate IDs (e.g. `ORD-1A2B3C...`).

### 3.4 Helper / Util — small shared tools
Location: `Backend/src/main/java/.../util/`

Helpers are small reusable utilities that do not belong to any single business
part — things like validation patterns (`ValidationPatterns`) or generic
`Helper` methods. Use them to avoid repeating the same little piece of logic.
(In this project `Helper` is currently a placeholder; most shared validation
sits in `ValidationPatterns` and inside the factories.)

### 3.5 Service — the brain / coordinator
Location: `Backend/src/main/java/.../service/` (interfaces) and `.../service/impl/`
(implementations)

The service holds the **business workflow**. It is where the steps of a real
task are ordered and coordinated. It uses repositories to load/save data,
factories to build objects, and domain methods to apply rules.

Notice the pattern: an interface like `IOrderService` (the contract) plus an
implementation like `OrderServiceImpl` (the actual code). Controllers depend on
the interface, which keeps things swappable and testable.

Example — `OrderServiceImpl.checkout()` does the whole checkout dance:
1. Load the customer's active cart (with a lock).
2. If the cart is empty, stop.
3. If this cart was already checked out, return the existing order (so a
   double-click does not create two orders).
4. Build the delivery address (via a factory).
5. Turn each cart item into an order item, re-checking stock for each.
6. Calculate totals (via `FinancialBreakdownFactory`).
7. Build the order (via `OrderFactory`) and save it.

Services are marked `@Transactional`, meaning: if any step fails, the whole
thing is rolled back so the database is never left half-changed.

### 3.6 Controller — the front door
Location: `Backend/src/main/java/.../controller/`

Controllers map web requests to service calls. They deal only with web concerns:
- Which URL and HTTP method (`@PostMapping("/checkout")`).
- Reading the request body (`@RequestBody CheckoutRequest`).
- Who is logged in (`Authentication`).
- Who is allowed in (`@PreAuthorize("hasRole('ADMINISTRATOR')")`).
- What HTTP status to send back (`201 Created`, `200 OK`).

They do NOT contain business rules. They take input, hand it to the service, and
convert the result into a response object.

### 3.7 Request / Response — the shapes on the wire
Locations: `.../request/` and `.../response/`

- **Request** objects (`CheckoutRequest`, `CardPaymentRequest`) describe what the
  frontend is allowed to send, with validation annotations.
- **Response** objects (`OrderResponse`, `PaymentResponse`) describe exactly what
  the backend sends back. They deliberately hide internal fields (like password
  hashes or full customer records) using a `from(...)` mapper, e.g.
  `OrderResponse.from(order)`.

This keeps the database shape separate from the public API shape.

---

## 4. Follow One Real Request End to End

Here is a checkout, step by step, through every layer:

```
1. USER clicks "Place Order" in the browser
        │
2. FRONTEND page (Checkout.jsx)
     calls orderService.checkout(details)
        │
3. orderService.js  ->  api('/orders/checkout', POST, details)
        │
4. apiClient.js  attaches JWT token, does fetch() to
     http://localhost:8081/api/orders/checkout
        │
   ═══════════════ network boundary ═══════════════
        │
5. OrderController.checkout()  reads who is logged in + the body
        │
6. OrderServiceImpl.checkout()  runs the workflow:
     - CartRepository loads the active cart
     - DeliveryAddressFactory builds the address
     - OrderItemFactory builds each line, re-checking stock
     - FinancialBreakdownFactory adds VAT + delivery fee
     - OrderFactory builds the Order
     - OrderRepository.save() writes it to the DB
        │
7. Controller wraps the saved Order in OrderResponse.from(order)
        │
   ═══════════════ network boundary ═══════════════
        │
8. FRONTEND receives JSON, shows the order confirmation
```

Then payment is a second, similar journey (`Payment.jsx` →
`paymentService` → `PaymentController` → `PaymentServiceImpl` → gateway →
mark order paid + reduce stock).

---

## 5. How the Frontend Connects

The frontend is organized so that **pages never call the network directly**.

```
pages/ (Checkout.jsx, Cart.jsx, Orders.jsx ...)
   │  call
   ▼
services/ (orderService.js, authService.js, productService.js ...)
   │  call
   ▼
services/api/apiClient.js   ← the ONE place that does fetch()
   │
   ▼
Spring Boot backend
```

- **`apiClient.js`** is the single gateway. It:
  - Adds the base URL.
  - Reads the saved login session from `localStorage` and attaches the JWT token
    as `Authorization: Bearer ...`.
  - Sets JSON headers and parses JSON responses.
  - Turns any failure into a clean `ApiError` (and fires an `auth:expired` event
    on a `401` so the app can log the user out).

- **Service files** (like `orderService.js`) are thin: each function is just one
  API path. Example:
  ```js
  checkout: (details) => api('/orders/checkout', json('POST', details)),
  mine:     () => api('/orders/my-orders'),
  cancel:   (id) => api(`/orders/${id}/cancel`, { method: 'PATCH' }),
  ```

- **Pages** import these services, call them (usually through the `useAsync`
  hook for loading/error state), and render the result.

- **Auth** is shared through `AuthProvider.jsx` + `useAuth.js`, and
  `ProtectedRoute.jsx` blocks pages you must be logged in to see.

So the frontend mirrors the backend's layering: **page ≈ controller,
service ≈ service, apiClient ≈ the transport.**

---

## 6. What Is Most Important to Understand

If you remember only a few things, remember these:

1. **The backend is the source of truth.** The frontend only asks and displays.
   Never trust the browser to enforce a rule; the rule lives in the backend.

2. **Each layer has one job.** Controller = web, Service = rules/workflow,
   Repository = database, Factory = safe creation, Domain = the thing + its own
   rules. When adding a feature, put each piece in the right layer.

3. **Domain objects protect themselves.** Status changes and stock changes go
   through methods like `markPaid()`, `decreaseStock()` that refuse invalid
   moves. This is why the data stays consistent.

4. **Snapshots protect history.** Orders copy product name/price at checkout, so
   later edits don't rewrite past orders.

5. **`@Transactional` + row locks protect money and stock.** Payment and
   checkout lock rows and roll back on failure, so you can't double-charge or
   oversell even with double-clicks or two requests at once.

6. **Roles control access.** `CUSTOMER` vs `ADMINISTRATOR` decide what endpoints
   you can hit (`@PreAuthorize`), and the JWT token carries the identity.

7. **Requests/Responses are the contract.** They keep the public API shape
   separate from the internal database shape, and hide sensitive fields.

---

## 7. Quick Reference: Where To Look

| I want to...                          | Look in...                                   |
|---------------------------------------|----------------------------------------------|
| Change a business rule                | `service/impl/*ServiceImpl.java`             |
| Change what a URL does / who can call | `controller/*Controller.java`                |
| Change how something is stored/queried| `repository/*Repository.java`                |
| Change how an object is created       | `factory/*Factory.java`                      |
| Change an object's own rules/fields   | `domain/*.java`                              |
| Change what the API accepts/returns   | `request/*.java`, `response/*.java`          |
| Change what the user sees             | `Frontend/src/pages/*`                       |
| Change an API call from the UI        | `Frontend/src/services/*`                    |
| Change auth/token handling            | `Frontend/src/services/api/apiClient.js`, `state/auth/` |

---

## 8. Mental Model in One Paragraph

A **customer** (a kind of **user**) browses **products**, adds them to a **cart**,
and checks out. Checkout freezes the cart into an **order** with snapshot prices
and calculated totals. A **payment** settles that order; when it succeeds, the
order is marked paid and **product** stock drops. The order then moves through
**delivery** until the customer receives it and can leave **feedback**. An
**administrator** oversees products, orders, and users throughout. On the
backend, every one of these parts is built the same way — a self-protecting
**domain** object, saved by a **repository**, created by a **factory**,
coordinated by a **service**, and exposed to the web by a **controller** — and
the React **frontend** simply calls those controllers through a single
`apiClient` and shows the results.
```
