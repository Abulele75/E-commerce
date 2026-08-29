package cput.ac.za.ecommerce.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "hardware_component")
@DiscriminatorValue("HARDWARE_COMPONENT")
public class HardwareComponent extends ProductCatalog {

    @Enumerated(EnumType.STRING)
    @Column(
            name = "component_type",
            nullable = false,
            length = 50
    )
    private ComponentType componentType;

    @Column(name = "power_requirement_watts")
    private int powerRequirementWatts;

    @Column(
            name = "component_form_factor",
            length = 100
    )
    private String componentFormFactor;

    protected HardwareComponent() {
    }

    private HardwareComponent(Builder builder) {
        super(builder);
        this.componentType = builder.componentType;
        this.powerRequirementWatts =
                builder.powerRequirementWatts;
        this.componentFormFactor =
                builder.componentFormFactor;
    }

    public ComponentType getComponentType() {
        return componentType;
    }

    public int getPowerRequirementWatts() {
        return powerRequirementWatts;
    }

    public String getComponentFormFactor() {
        return componentFormFactor;
    }

    public static class Builder
            extends ProductCatalog.ProductBuilder<Builder> {

        private ComponentType componentType;
        private int powerRequirementWatts;
        private String componentFormFactor;

        public Builder setComponentType(
                ComponentType componentType
        ) {
            this.componentType = componentType;
            return this;
        }

        public Builder setPowerRequirementWatts(
                int powerRequirementWatts
        ) {
            this.powerRequirementWatts =
                    powerRequirementWatts;
            return this;
        }

        public Builder setComponentFormFactor(
                String componentFormFactor
        ) {
            this.componentFormFactor =
                    componentFormFactor;
            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }

        public HardwareComponent build() {
            if (componentType == null) {
                throw new IllegalStateException(
                        "Component type is required"
                );
            }

            return new HardwareComponent(this);
        }
    }
}