package cput.ac.za.ecommerce.request;

import cput.ac.za.ecommerce.domain.CardBrand;
import cput.ac.za.ecommerce.util.ValidationPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CardPaymentRequest
        extends PaymentRequest {

    @NotBlank(
            message = "Payment token is required"
    )
    @Size(
            min = 12,
            max = 500,
            message = "Payment token is invalid"
    )
    private String paymentToken;

    @NotNull(
            message = "Card brand is required"
    )
    private CardBrand cardBrand;

    @NotBlank(
            message = "Cardholder name is required"
    )
    @Size(
            min = 2,
            max = 100
    )
    @Pattern(
            regexp = ValidationPatterns.PERSON_NAME,
            message =
                    "Cardholder name may contain letters, spaces, apostrophes and hyphens only"
    )
    private String cardholderName;

    @NotBlank(
            message = "Last four card digits are required"
    )
    @Pattern(
            regexp = "^\\d{4}$",
            message =
                    "Card details must contain exactly the last four digits"
    )
    private String cardLastFourDigits;

    public CardPaymentRequest() {
    }

    public String getPaymentToken() {
        return paymentToken;
    }

    public void setPaymentToken(
            String paymentToken
    ) {
        this.paymentToken = paymentToken;
    }

    public CardBrand getCardBrand() {
        return cardBrand;
    }

    public void setCardBrand(
            CardBrand cardBrand
    ) {
        this.cardBrand = cardBrand;
    }

    public String getCardholderName() {
        return cardholderName;
    }

    public void setCardholderName(
            String cardholderName
    ) {
        this.cardholderName = cardholderName;
    }

    public String getCardLastFourDigits() {
        return cardLastFourDigits;
    }

    public void setCardLastFourDigits(
            String cardLastFourDigits
    ) {
        this.cardLastFourDigits =
                cardLastFourDigits;
    }
}