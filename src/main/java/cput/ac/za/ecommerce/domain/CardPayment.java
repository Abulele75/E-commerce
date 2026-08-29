/*
   CardPayment.java
   Ngwana Tiyani (231266731)
   Date: 20 June 2026
 */
package cput.ac.za.ecommerce.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "card_payment")
@PrimaryKeyJoinColumn(
        name = "transaction_id"
)
public class CardPayment extends Payment {

    @Column(
            name = "payment_gateway_reference",
            nullable = false,
            unique = true,
            length = 100
    )
    private String paymentGatewayReference;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "card_brand",
            nullable = false,
            length = 30
    )
    private CardBrand cardBrand;

    @Column(
            name = "cardholder_name",
            nullable = false,
            length = 100
    )
    private String cardholderName;

    @Column(
            name = "card_last_four_digits",
            nullable = false,
            length = 4
    )
    private String cardLastFourDigits;

    protected CardPayment() {
    }

    private CardPayment(Builder builder) {
        super(builder);

        this.paymentGatewayReference =
                builder.paymentGatewayReference;

        this.cardBrand =
                builder.cardBrand;

        this.cardholderName =
                builder.cardholderName;

        this.cardLastFourDigits =
                builder.cardLastFourDigits;
    }

    public String getPaymentGatewayReference() {
        return paymentGatewayReference;
    }

    public CardBrand getCardBrand() {
        return cardBrand;
    }

    public String getCardholderName() {
        return cardholderName;
    }

    public String getCardLastFourDigits() {
        return cardLastFourDigits;
    }

    @Transient
    public String getMaskedCardNumber() {
        return "**** **** **** "
                + cardLastFourDigits;
    }

    public static class Builder
            extends PaymentBuilder<Builder> {

        private String paymentGatewayReference;
        private CardBrand cardBrand;
        private String cardholderName;
        private String cardLastFourDigits;

        public Builder() {
            setPaymentMethod(
                    PaymentMethod.CARD
            );
        }

        public Builder setPaymentGatewayReference(
                String paymentGatewayReference
        ) {
            this.paymentGatewayReference =
                    paymentGatewayReference;

            return this;
        }

        public Builder setCardBrand(
                CardBrand cardBrand
        ) {
            this.cardBrand = cardBrand;
            return this;
        }

        public Builder setCardholderName(
                String cardholderName
        ) {
            this.cardholderName =
                    cardholderName;

            return this;
        }

        public Builder setCardLastFourDigits(
                String cardLastFourDigits
        ) {
            this.cardLastFourDigits =
                    cardLastFourDigits;

            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }

        public CardPayment build() {
            return new CardPayment(this);
        }
    }
}