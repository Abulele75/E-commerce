 /*
  * FinancialBreakdownFactory.java
  * Author: Sinethemba Nyimbinya (220085870)
  * Date: 22 June 2026
  */

 package cput.ac.za.ecommerce.factory;

 import cput.ac.za.ecommerce.domain.FinancialBreakdown;
 import cput.ac.za.ecommerce.domain.OrderItem;

 import java.math.BigDecimal;
 import java.math.RoundingMode;
 import java.util.List;

 public final class FinancialBreakdownFactory {

     private FinancialBreakdownFactory() {
     }

     public static FinancialBreakdown
     createFinancialBreakdown(
             List<OrderItem> orderItems,
             BigDecimal deliveryFee,
             BigDecimal discountAmount,
             BigDecimal vatRate
     ) {
         if (orderItems == null
                 || orderItems.isEmpty()) {
             return null;
         }

         BigDecimal normalizedDeliveryFee =
                 normalizeNonNegative(
                         deliveryFee
                 );

         BigDecimal normalizedDiscount =
                 normalizeNonNegative(
                         discountAmount
                 );

         BigDecimal normalizedVatRate =
                 normalizeNonNegative(vatRate);

         BigDecimal subtotal =
                 orderItems.stream()
                         .map(
                                 OrderItem::
                                         getLineTotalSnapshot
                         )
                         .reduce(
                                 BigDecimal.ZERO,
                                 BigDecimal::add
                         )
                         .setScale(
                                 2,
                                 RoundingMode.HALF_UP
                         );

         if (normalizedDiscount.compareTo(
                 subtotal
         ) > 0) {
             return null;
         }

         BigDecimal taxableAmount =
                 subtotal.subtract(
                         normalizedDiscount
                 );

         BigDecimal vatAmount =
                 taxableAmount
                         .multiply(
                                 normalizedVatRate
                         )
                         .setScale(
                                 2,
                                 RoundingMode.HALF_UP
                         );

         BigDecimal total =
                 taxableAmount
                         .add(vatAmount)
                         .add(
                                 normalizedDeliveryFee
                         )
                         .setScale(
                                 2,
                                 RoundingMode.HALF_UP
                         );

         return new FinancialBreakdown.Builder()
                 .setBasketSubtotal(subtotal)
                 .setDiscountAmount(
                         normalizedDiscount
                 )
                 .setVatAmount(vatAmount)
                 .setDeliveryFee(
                         normalizedDeliveryFee
                 )
                 .setFinalInvoiceTotal(total)
                 .build();
     }

     private static BigDecimal
     normalizeNonNegative(
             BigDecimal value
     ) {
         if (value == null) {
             return BigDecimal.ZERO
                     .setScale(2);
         }

         if (value.compareTo(
                 BigDecimal.ZERO
         ) < 0) {
             throw new IllegalArgumentException(
                     "Financial values cannot be negative"
             );
         }

         return value.setScale(
                 2,
                 RoundingMode.HALF_UP
         );
     }
 }