
/*
   Administrator.java
   Owenkosi Nxasana (230240887)
   Date: 20 June 2026
 */
package cput.ac.za.ecommerce.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "administrator")
public class Administrator extends User {

    @Column(
            name = "employee_number",
            nullable = false,
            unique = true,
            length = 50
    )
    private String employeeNumber;

    @Column(
            nullable = false,
            length = 100
    )
    private String department;

    protected Administrator() {
    }

    private Administrator(Builder builder) {
        super(builder);
        this.employeeNumber = builder.employeeNumber;
        this.department = builder.department;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public String getDepartment() {
        return department;
    }

    @Override
    public UserRole getRole() {
        return UserRole.ADMINISTRATOR;
    }

    public static class Builder
            extends User.Builder<Builder> {

        private String employeeNumber;
        private String department;

        public Builder setEmployeeNumber(
                String employeeNumber
        ) {
            this.employeeNumber = employeeNumber;
            return this;
        }

        public Builder setDepartment(
                String department
        ) {
            this.department = department;
            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }

        public Administrator build() {
            return new Administrator(this);
        }
    }
}