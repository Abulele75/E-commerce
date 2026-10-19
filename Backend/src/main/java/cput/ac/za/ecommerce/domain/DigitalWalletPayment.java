/*
   DigitalWalletPayment.java
   Ngwana Tiyani (231266731)
   Date: 20 June 2026
 */

package cput.ac.za.ecommerce.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "digital_wallet_payment")
@PrimaryKeyJoinColumn(
        name = "transaction_id"
)
public class DigitalWalletPayment
        extends Payment {

    @Enumerated(EnumType.STRING)
    @Column(
            name = "wallet_provider",
            nullable = false,
            length = 30
    )
    private WalletProvider walletProvider;

    @Column(
            name = "provider_transaction_reference",
            nullable = false,
            unique = true,
            length = 100
    )
    private String providerTransactionReference;

    protected DigitalWalletPayment() {
    }

    private DigitalWalletPayment(
            Builder builder
    ) {
        super(builder);

        this.walletProvider =
                builder.walletProvider;

        this.providerTransactionReference =
                builder.providerTransactionReference;
    }

    public WalletProvider getWalletProvider() {
        return walletProvider;
    }

    public String
    getProviderTransactionReference() {
        return providerTransactionReference;
    }

    public static class Builder
            extends PaymentBuilder<Builder> {

        private WalletProvider walletProvider;

        private String
                providerTransactionReference;

        public Builder() {
            setPaymentMethod(
                    PaymentMethod.DIGITAL_WALLET
            );
        }

        public Builder setWalletProvider(
                WalletProvider walletProvider
        ) {
            this.walletProvider =
                    walletProvider;

            return this;
        }

        public Builder
        setProviderTransactionReference(
                String providerTransactionReference
        ) {
            this.providerTransactionReference =
                    providerTransactionReference;

            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }

        public DigitalWalletPayment build() {
            return new DigitalWalletPayment(this);
        }
    }
}