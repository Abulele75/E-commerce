# Payments Guide (Tokens & How It Works)

The backend uses a **simulated payment gateway**
(`SimulatedPaymentGatewayService`). No real bank or money is involved, so you
use **test tokens** instead of real card/wallet credentials.

---

## 1. Tokens you can use

A token is just a string. The gateway rule is simple:

| Token | Result |
|-------|--------|
| Any non-blank string, **12–500 characters** | Payment **SUCCEEDS** |
| The exact value `TEST-PAYMENT-DECLINED` (case-insensitive) | Payment is **DECLINED** |
| Blank / shorter than 12 chars | Rejected by validation (**400 Bad Request**) |

The same rule applies to both **card** payment tokens and **wallet** tokens.

### Ready-to-use test tokens

**Card payment token (success):**
```
tok_test_card_success_0001
```

**Wallet payment token (success):**
```
tok_test_wallet_success_0001
```

**Declined token (for testing a failed payment) — card or wallet:**
```
TEST-PAYMENT-DECLINED
```

> Any other value of 12+ characters also works, e.g.
> `pay_visa_5f3a91c27bd4`, `applepay-9921-abcd-token`. These are just
> placeholders that stand in for what a real gateway (Stripe, PayFast, etc.)
> would hand back after tokenising a card or wallet.

### Allowed enum values

**cardBrand:** `VISA`, `MASTERCARD`, `AMERICAN_EXPRESS`, `OTHER`

**walletProvider:** `APPLE_PAY`, `GOOGLE_PAY`, `SAMSUNG_PAY`, `PAYPAL`, `OTHER`

---

## 2. How a token is "generated" (in real life vs here)

**In a real system** the customer never types a raw card number into your
backend. Instead:

1. The card/wallet details go directly to the payment provider (e.g. Stripe).
2. The provider returns a **one-time token** that represents those details.
3. Your backend sends only that token to the provider to charge it.
   This keeps sensitive card data out of your server (PCI compliance).

**In this project** that provider is simulated. So:

- You (the caller) supply a placeholder token string.
- The gateway checks the rule in section 1 and returns a
  **provider reference** it generates itself, in the form:

  ```
  <PREFIX>-<16 uppercase hex characters>
  ```

  - Card payments use the prefix `CARD`, e.g. `CARD-9F2A7C1B4D6E8012`
  - Wallet payments use the provider name, e.g. `APPLE_PAY-3B1C...`,
    `PAYPAL-77AA...`

  That reference is stored on the payment as the transaction reference. It is
  produced from a random `UUID` — you do not send it, the gateway creates it.

---

## 3. How payment works — step by step

### Prerequisites
- You are logged in (customer JWT).
- You have an **order** in status `PENDING_PAYMENT`. Checkout
  (`POST /api/orders/checkout`) creates this from your active cart.
- The amount charged is taken from the order itself
  (`financialBreakdown.finalInvoiceTotal`) — you never send the amount, so it
  cannot be tampered with.

### The flow

1. **Call the payment endpoint** with the order ID, a token, and a billing
   address:
   - Card: `POST /api/payments/card`
   - Wallet: `POST /api/payments/wallet`

2. **Ownership check** — the backend confirms the order belongs to the
   logged-in customer (otherwise **403 Forbidden**).

3. **Payable check** — the order must be `PENDING_PAYMENT`, not already paid,
   and not empty (otherwise **400 Bad Request**).

4. **Stock lock & validation** — each product in the order is row-locked and
   checked for availability and sufficient stock.

5. **Charge the (simulated) gateway** — the token + order amount are sent to
   the gateway, which returns success or failure plus a provider reference.

6. **On SUCCESS:**
   - Product stock is decreased.
   - The payment is marked `SUCCESSFUL`.
   - The order is marked `PAID`.
   - The source cart is marked checked out.
   - Response: **201 Created** with the payment details.

7. **On FAILURE (declined token):**
   - The payment is saved as `FAILED` with the reason "Payment was declined".
   - The order stays `PENDING_PAYMENT`, so the customer can **try again** with
     a valid token.
   - Response: **201 Created** with a payment whose status is `FAILED`
     (the request itself succeeded; the *payment* did not).

### After payment
- `GET /api/payments/my-payments` — your payments
- `GET /api/payments/{transactionId}` — one payment
- `GET /api/payments/order/{orderId}` — payments for an order
- Admin only: `GET /api/payments` (all) and
  `POST /api/payments/{transactionId}/refund` (restocks items, marks order and
  payment refunded)

---

## 4. Example requests (IntelliJ HTTP Client)

```http
@baseUrl = http://localhost:8081/api
@token = PASTE_YOUR_CUSTOMER_JWT_HERE
@orderId = PASTE_A_PENDING_PAYMENT_ORDER_ID

### Card payment (success)
POST {{baseUrl}}/payments/card
Content-Type: application/json
Authorization: Bearer {{token}}

{
  "orderId": "{{orderId}}",
  "paymentToken": "tok_test_card_success_0001",
  "cardBrand": "VISA",
  "cardholderName": "Thabo Mokoena",
  "cardLastFourDigits": "4242",
  "billingLocation": {
    "billingName": "Thabo Mokoena",
    "streetAddress": "12 Long Street",
    "suburb": "City Centre",
    "city": "Cape Town",
    "province": "Western Cape",
    "postalCode": "8001",
    "country": "South Africa"
  }
}

### Wallet payment (success)
POST {{baseUrl}}/payments/wallet
Content-Type: application/json
Authorization: Bearer {{token}}

{
  "orderId": "{{orderId}}",
  "walletProvider": "APPLE_PAY",
  "walletToken": "tok_test_wallet_success_0001",
  "billingLocation": {
    "billingName": "Thabo Mokoena",
    "streetAddress": "12 Long Street",
    "suburb": "City Centre",
    "city": "Cape Town",
    "province": "Western Cape",
    "postalCode": "8001",
    "country": "South Africa"
  }
}

### Card payment (declined — for testing failure)
POST {{baseUrl}}/payments/card
Content-Type: application/json
Authorization: Bearer {{token}}

{
  "orderId": "{{orderId}}",
  "paymentToken": "TEST-PAYMENT-DECLINED",
  "cardBrand": "MASTERCARD",
  "cardholderName": "Thabo Mokoena",
  "cardLastFourDigits": "0002",
  "billingLocation": {
    "billingName": "Thabo Mokoena",
    "streetAddress": "12 Long Street",
    "suburb": "City Centre",
    "city": "Cape Town",
    "province": "Western Cape",
    "postalCode": "8001",
    "country": "South Africa"
  }
}
```

### Field rules recap
- `paymentToken` / `walletToken`: 12–500 characters, required.
- `cardLastFourDigits`: exactly 4 digits.
- `postalCode`: exactly 4 digits.
- `billingName` / `cardholderName`: letters, spaces, apostrophes, hyphens only.
- `orderId` and `billingLocation` are required on every payment.

---

## 5. Frontend note

On the storefront checkout/payment page you can type any 12+ character value in
the token field to simulate a successful payment, or `TEST-PAYMENT-DECLINED` to
see the declined path. The order must already exist and be awaiting payment.

---

## Development schema changes

This capstone uses Hibernate domain mappings to create/update the local schema. If a payment, order, or inheritance entity mapping changes during development, stop the backend, drop the local `ecommerce` database, restart the backend so Hibernate creates a clean schema, and then run the HTTP seed files again. Do not keep duplicate legacy columns.
