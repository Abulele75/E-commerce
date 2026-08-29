/* PreBuiltSystem.java
   Entity for Product Catalog Service
   Author: Nomhle Njengele (216227488)
   Date: 21 June 2026 */

package cput.ac.za.ecommerce.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "pre_built_system")
@DiscriminatorValue("PRE_BUILT_SYSTEM")
public class PreBuiltSystem
        extends ProductCatalog {

    @Column(
            name = "graphics_card",
            length = 150
    )
    private String graphicsCard;

    @Column(
            name = "operating_system",
            length = 100
    )
    private String operatingSystem;

    @Column(
            name = "warranty_period_months"
    )
    private int warrantyPeriodMonths;

    @Column(
            name = "liquid_cooled"
    )
    private boolean liquidCooled;

    protected PreBuiltSystem() {
    }

    private PreBuiltSystem(Builder builder) {
        super(builder);
        this.graphicsCard = builder.graphicsCard;
        this.operatingSystem =
                builder.operatingSystem;

        this.warrantyPeriodMonths =
                builder.warrantyPeriodMonths;

        this.liquidCooled =
                builder.liquidCooled;
    }

    public String getGraphicsCard() {
        return graphicsCard;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public int getWarrantyPeriodMonths() {
        return warrantyPeriodMonths;
    }

    public boolean isLiquidCooled() {
        return liquidCooled;
    }

    public static class Builder
            extends ProductCatalog
            .ProductBuilder<Builder> {

        private String graphicsCard;
        private String operatingSystem;
        private int warrantyPeriodMonths;
        private boolean liquidCooled;

        public Builder setGraphicsCard(
                String graphicsCard
        ) {
            this.graphicsCard = graphicsCard;
            return this;
        }

        public Builder setOperatingSystem(
                String operatingSystem
        ) {
            this.operatingSystem =
                    operatingSystem;
            return this;
        }

        public Builder setWarrantyPeriodMonths(
                int warrantyPeriodMonths
        ) {
            this.warrantyPeriodMonths =
                    warrantyPeriodMonths;
            return this;
        }

        public Builder setLiquidCooled(
                boolean liquidCooled
        ) {
            this.liquidCooled = liquidCooled;
            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }

        public PreBuiltSystem build() {
            return new PreBuiltSystem(this);
        }
    }
}