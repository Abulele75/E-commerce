package cput.ac.za.ecommerce.request;

import cput.ac.za.ecommerce.domain.WalletProvider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class DigitalWalletPaymentRequest
        extends PaymentRequest {

    @NotNull(
            message = "Wallet provider is required"
    )
    private WalletProvider walletProvider;

    @NotBlank(
            message = "Wallet payment token is required"
    )
    @Size(
            min = 12,
            max = 500,
            message = "Wallet payment token is invalid"
    )
    private String walletToken;

    public DigitalWalletPaymentRequest() {
    }

    public WalletProvider getWalletProvider() {
        return walletProvider;
    }

    public void setWalletProvider(
            WalletProvider walletProvider
    ) {
        this.walletProvider = walletProvider;
    }

    public String getWalletToken() {
        return walletToken;
    }

    public void setWalletToken(
            String walletToken
    ) {
        this.walletToken = walletToken;
    }
}