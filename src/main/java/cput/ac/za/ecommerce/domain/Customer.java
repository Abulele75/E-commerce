
/*
   Customer.java
   Owenkosi Nxasana (230240887)
   Date: 20 June 2026
 */
package cput.ac.za.ecommerce.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer")
public class Customer extends User {

    @Column(
            name = "customer_number",
            nullable = false,
            unique = true,
            length = 50
    )
    private String customerNumber;

    protected Customer() {
    }

    private Customer(Builder builder) {
        super(builder);
        this.customerNumber = builder.customerNumber;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    @Override
    public UserRole getRole() {
        return UserRole.CUSTOMER;
    }

    public static class Builder
            extends User.Builder<Builder> {

        private String customerNumber;

        public Builder setCustomerNumber(
                String customerNumber
        ) {
            this.customerNumber = customerNumber;
            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }

        public Customer build() {
            return new Customer(this);
        }
    }
}