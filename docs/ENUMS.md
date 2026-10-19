# Enums Guide

This document explains what **enums** are, what they are used for, how they work in Java, and — most importantly — how the enums in this project flow through every layer of the application: **domain → repository → service → controller → factory → request → response**, and out to the frontend.

---

## 1. What Is an Enum?

An **enum** (short for *enumeration*) is a special Java type whose value can only be one of a **fixed, named set of constants**. Instead of using loose strings or magic numbers to represent a fixed choice, you define the exact allowed values once.

Compare the two styles:

```java
// Fragile: any string is accepted, typos compile fine, invalid data slips in
String orderStatus = "SHPPED";   // oops, typo — no compiler error

// Safe: only the defined constants are possible
OrderStatus orderStatus = OrderStatus.SHIPPED;   // typo would not compile
```

In this project, enums live in the `domain` package. The simplest one is `UserRole`:

```java
public enum UserRole {
    CUSTOMER,
    ADMINISTRATOR
}
```

Each constant (`CUSTOMER`, `ADMINISTRATOR`) is a singleton instance of the `UserRole` type.

---

## 2. What Do Enums Serve (Why Use Them)?

Enums exist to model a value that has a **known, closed list of options**. Benefits used throughout this codebase:

1. **Type safety** — the compiler rejects anything that isn't a defined constant.
2. **Self-documenting** — `PaymentStatus.REFUNDED` reads better than `4` or `"refunded"`.
3. **A single source of truth** — the list is defined in one file and reused everywhere (domain, DB mapping, validation, JSON).
4. **Safe state machines** — order/payment/cart lifecycles are expressed as enum transitions with guard rules.
5. **Automatic JSON + validation support** — Spring converts JSON strings to enum constants on the way in, and enum constants to strings on the way out, and rejects unknown values.

---

## 3. The Enums in This Project

All are in `Backend/src/main/java/cput/ac/za/ecommerce/domain/`:

| Enum | Represents | Style |
|---|---|---|
| `UserRole` | CUSTOMER / ADMINISTRATOR | Simple constants |
| `OrderStatus` | PENDING_PAYMENT → PAID → PROCESSING → SHIPPED → DELIVERED / CANCELLED / REFUNDED | Simple + used as a state machine |
| `PaymentStatus` | PENDING / SUCCESSFUL / FAILED / CANCELLED / REFUNDED | Simple + state machine |
| `PaymentMethod` | CARD / DIGITAL_WALLET | Simple |
| `CartStatus` | ACTIVE / CHECKED_OUT / … | Simple |
| `DeliveryStatus` | PREPARING / DISPATCHED / IN_TRANSIT / … | Simple |
| `ProductStatus` | IN_STOCK / LOW_STOCK / … | Simple |
| `ProductCategory` | LAPTOPS, SMARTPHONES, … | **Has fields** (code + display name) |
| `Brand` | APPLE, SAMSUNG, … | **Has fields** (display name) |
| `ComponentType` | GRAPHICS_CARD, MEMORY, … | **Has fields** (display name) |
| `CardBrand` | VISA / MASTERCARD / … | Simple |
| `WalletProvider` | APPLE_PAY / GOOGLE_PAY / … | Simple |

### 3.1 Enums with extra data (fields, constructor, methods)

An enum can carry data and behaviour. `ProductCategory` attaches a code and a human-friendly label to each constant:

```java
public enum ProductCategory {

    LAPTOPS("CAT-001", "Laptops"),
    DESKTOP_COMPUTERS("CAT-002", "Desktop Computers"),
    // ...
    ACCESSORIES("CAT-006", "Accessories");

    private final String categoryCode;
    private final String displayName;

    ProductCategory(String categoryCode, String displayName) {  // constructor runs once per constant
        this.categoryCode = categoryCode;
        this.displayName = displayName;
    }

    public String getCategoryCode() { return categoryCode; }
    public String getDisplayName()  { return displayName; }
}
```

So `ProductCategory.LAPTOPS.getDisplayName()` returns `"Laptops"`. `Brand` and `ComponentType` follow the same pattern with a `displayName`.

### 3.2 Useful built-in methods every enum gets for free

- `name()` → the constant's exact text, e.g. `OrderStatus.SHIPPED.name()` → `"SHIPPED"`.
- `valueOf("SHIPPED")` → the constant back from a string (throws if unknown).
- `values()` → array of all constants (handy for building dropdown lists).
- `ordinal()` → zero-based position.

---

## 4. How Enums Connect Across the Layers

An enum is defined once in `domain` and then **referenced** by every other layer. Here is the journey of an enum value, using `OrderStatus` and `ProductCategory` as the running examples.

```
Frontend string "SHIPPED"
        │  (JSON request body)
        ▼
[request]   UpdateOrderStatusRequest.orderStatus : OrderStatus   ← Spring converts "SHIPPED" → enum, @NotNull validates
        │
        ▼
[controller] OrderController → calls service with the enum
        │
        ▼
[service]   OrderServiceImpl.updateOrderStatus(...) → switch on enum, business rules
        │
        ▼
[domain]    Order.orderStatus : OrderStatus  (@Enumerated(EnumType.STRING)) ← state machine + how it's stored
        │
        ▼
[repository] OrderRepository.findAllByStatusWithDetails(OrderStatus) ← query by enum; DB column stores "SHIPPED"
        │
        ▼
[response]  OrderResponse.orderStatus : OrderStatus → serialized back to JSON string "SHIPPED"
        │
        ▼
Frontend reads "SHIPPED"
```

### 4.1 `domain` — the definition and the storage mapping

The enum type is declared here, and entities use it as a field. JPA is told to store the **text** of the enum, not its numeric position, using `@Enumerated(EnumType.STRING)`:

```java
// Order.java
@Enumerated(EnumType.STRING)
@Column(name = "order_status", nullable = false, length = 30)
private OrderStatus orderStatus;
```

> **Why `EnumType.STRING` and not `ORDINAL`?** STRING stores `"SHIPPED"` in the database column. ORDINAL would store the index (`3`), which breaks the moment someone reorders or inserts a constant. STRING is the safe, readable choice, and this project uses it consistently (`ProductCatalog.brand`, `ProductCatalog.category`, `Payment.paymentStatus`, `Cart.cartStatus`, etc.).

The domain layer also puts **behaviour** on enums. `Order` is a state machine whose transitions are guarded:

```java
public void markShipped() {
    if (orderStatus != OrderStatus.PROCESSING) {
        throw new IllegalStateException("Only a processing order may be shipped");
    }
    this.orderStatus = OrderStatus.SHIPPED;
    touch();
}
```

`Payment` does the same with `PaymentStatus` (`markSuccessful`, `markRefunded`, etc.). The enum makes these rules explicit and impossible to express with an invalid value.

### 4.2 `repository` — querying by enum

Spring Data repositories accept enums directly, both in **derived query methods** and in **JPQL**. Because the column is `EnumType.STRING`, the enum is matched against the stored text automatically.

```java
// ProductCatalogRepository.java (derived query)
List<ProductCatalog> findAllByCategoryAndActiveTrueOrderByProductNameAsc(ProductCategory category);

// OrderRepository.java (JPQL)
@Query("""
        SELECT o FROM Order o
        JOIN FETCH o.customer
        WHERE o.orderStatus = :orderStatus
        ORDER BY o.createdAt DESC
        """)
List<Order> findAllByStatusWithDetails(@Param("orderStatus") OrderStatus orderStatus);
```

`CartRepository` (`cartStatus = :cartStatus`) and `PaymentRepository` (`existsByOrder_OrderIdAndPaymentStatus`) follow the same pattern.

### 4.3 `service` — business logic driven by enums

Services read and change enum state and often branch on it. A clean example is `OrderServiceImpl`, which uses a `switch` over `OrderStatus` to apply the right transition:

```java
private void applyStatusTransition(Order order, OrderStatus newStatus) {
    if (order.getOrderStatus() == newStatus) return;
    switch (newStatus) {
        case CANCELLED -> {
            if (order.getOrderStatus() != OrderStatus.PENDING_PAYMENT) {
                throw new IllegalStateException(...);
            }
            // ...
        }
        // other cases delegate to the domain methods (markPaid, markShipped, ...)
    }
}
```

Service **interfaces** also speak in enums, so the contract is type-safe:

```java
// IOrderService.java
List<Order> getOrdersByStatus(OrderStatus orderStatus);
Order updateOrderStatus(String orderId, OrderStatus newStatus);
```

### 4.4 `controller` — receiving and returning enums

Controllers don't usually handle enums themselves; they receive a **request object** whose field is an enum and hand it to the service, then return a **response object** whose field is an enum. For example the admin endpoint that changes order status accepts `UpdateOrderStatusRequest` (below) and product creation accepts `ProductCatalogRequest` (which carries `Brand`, `ProductCategory`, and `ComponentType`).

### 4.5 `request` — JSON in, validated as an enum

Request DTOs declare enum-typed fields. Spring's Jackson converter turns the incoming JSON **string** into the enum constant, and Bean Validation (`@NotNull`) guarantees it's present:

```java
// UpdateOrderStatusRequest.java
public class UpdateOrderStatusRequest {
    @NotNull(message = "Order status is required")
    private OrderStatus orderStatus;   // JSON "SHIPPED" → OrderStatus.SHIPPED
    // getters / setters
}
```

```java
// ProductCatalogRequest.java
@NotNull(message = "Brand is required")
private Brand brand;

@NotNull(message = "Category is required")
private ProductCategory category;

private ComponentType componentType;   // optional, only for hardware components
```

> If the client sends a value that isn't a defined constant (e.g. `"SHPPED"`), deserialization fails and the request is rejected — the enum protects the system boundary automatically.

### 4.6 `response` — enum out, JSON string out

Response DTOs also expose enum fields. On the way out, Jackson serializes the enum back to its `name()` string, so the frontend receives plain text:

```java
// OrderResponse.java (a record)
public record OrderResponse(
        String orderId,
        OrderStatus orderStatus,          // serialized as "SHIPPED"
        List<OrderItemResponse> orderItems,
        FinancialBreakdownResponse financialBreakdown,
        ...
) { ... }
```

Sometimes the code calls `.name()` explicitly to build a description string:

```java
// PaymentResponse.java
String description = payment.getPaymentMethod().name();       // "CARD"
description = cardPayment.getCardBrand().name() + " ending in " + last4;  // "VISA ending in 1234"
```

`ProductCatalogResponse` exposes `Brand` and `ProductCategory`; `CartItemResponse` exposes `Brand` and `ProductCategory`; `OrderItemResponse` exposes `Brand`.

### 4.7 `factory` — building objects with enums

Factories assemble domain objects and pass enum values into builders. For example `ShipmentTrackingFactory.createShipmentTracking(...)` receives a `DeliveryStatus` and sets it on the new `ShipmentTracking`, while `UserManagementFactory` builds `Customer`/`Administrator` whose `getRole()` returns the correct `UserRole`. The factory guarantees objects are created only with valid enum values.

---

## 5. How the Frontend Stays in Sync

The React app doesn't have Java enums, so it mirrors the constants as string arrays in `Frontend/src/constants/catalog/catalogOptions.js`:

```js
export const categories     = ['LAPTOPS', 'DESKTOP_COMPUTERS', 'COMPUTER_COMPONENTS', 'SMARTPHONES', 'SMART_WATCHES', 'ACCESSORIES']
export const brands         = ['APPLE', 'SAMSUNG', 'HUAWEI', /* ... */ 'OTHER']
export const orderStatuses  = ['PENDING_PAYMENT', 'PAID', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED', 'REFUNDED']
export const deliveryStatuses = ['PREPARING', 'DISPATCHED', 'IN_TRANSIT', 'OUT_FOR_DELIVERY', 'DELIVERED', 'DELIVERY_FAILED', 'CANCELLED']
// cardBrands, walletProviders, componentTypes ...
```

These strings must match the Java constant **names** exactly, because that's the format sent in requests and received in responses. They power dropdowns and labels in the UI.

> **Important:** because the frontend list is a manual copy, adding a new constant to a Java enum means you must also add it here. This is the one place the "single source of truth" breaks, so keep them aligned.

---

## 6. End-to-End Example: Changing an Order to SHIPPED

1. **Frontend** sends `PATCH /api/orders/{id}/status` with body `{ "orderStatus": "SHIPPED" }` (the string comes from `orderStatuses`).
2. **request** — Spring binds it into `UpdateOrderStatusRequest.orderStatus` as `OrderStatus.SHIPPED`; `@NotNull` passes.
3. **controller** — the admin-guarded endpoint calls `orderService.updateOrderStatus(id, OrderStatus.SHIPPED)`.
4. **service** — `OrderServiceImpl` validates the transition and calls the domain method.
5. **domain** — `Order.markShipped()` checks the current status is `PROCESSING`, then sets `orderStatus = SHIPPED`.
6. **repository** — saving the order writes `"SHIPPED"` into the `order_status` column (`EnumType.STRING`).
7. **response** — the updated order is mapped to `OrderResponse`, whose `orderStatus` serializes back to `"SHIPPED"`.
8. **Frontend** reads `"SHIPPED"` and displays it.

At every hop the value is a real `OrderStatus`, never a loose string, so invalid states cannot occur inside the system.

---

## 7. Quick Rules of Thumb

- Define a new fixed choice as an enum in `domain`.
- Map it on entities with `@Enumerated(EnumType.STRING)` (never ORDINAL).
- Reference the enum type (not `String`) in requests, responses, service interfaces, repositories, and factories.
- Add `@NotNull` on required enum request fields.
- Put lifecycle rules (allowed transitions) on the domain object as methods.
- When you add or rename a constant, update `catalogOptions.js` on the frontend to match.
