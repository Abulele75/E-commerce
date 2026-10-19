 /*
  * OrderItemFactory.java
  * Author: Sinethemba Nyimbinya (220085870)
  * Date: 21 June 2026
  */

 package cput.ac.za.ecommerce.factory;

 import cput.ac.za.ecommerce.domain.OrderItem;
 import cput.ac.za.ecommerce.domain.ProductCatalog;

 import java.math.BigDecimal;
 import java.util.UUID;

 public final class OrderItemFactory {

     private OrderItemFactory() {
     }

     public static OrderItem createOrderItem(
             ProductCatalog product,
             int quantity
     ) {
         if (product == null
                 || quantity <= 0
                 || quantity
                 > product.getStockQuantity()) {
             return null;
         }

         BigDecimal effectivePrice =
                 product.getEffectivePrice();

         if (effectivePrice == null
                 || effectivePrice.compareTo(
                 BigDecimal.ZERO
         ) <= 0) {
             return null;
         }

         return new OrderItem.Builder()
                 .setOrderItemId(
                         generateOrderItemId()
                 )
                 .setProductIdSnapshot(
                         product.getProductId()
                 )
                 .setSkuSnapshot(
                         product.getSku()
                 )
                 .setProductNameSnapshot(
                         product.getProductName()
                 )
                 .setBrandSnapshot(
                         product.getBrand()
                 )
                 .setImageUrlSnapshot(
                         product.getPrimaryImageUrl()
                 )
                 .setUnitPriceSnapshot(
                         effectivePrice
                 )
                 .setQuantityPurchased(quantity)
                 .build();
     }

     private static String
     generateOrderItemId() {
         return "OI-"
                 + UUID.randomUUID()
                 .toString()
                 .replace("-", "")
                 .substring(0, 14)
                 .toUpperCase();
     }
 }