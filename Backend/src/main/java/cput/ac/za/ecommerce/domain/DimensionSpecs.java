
/* DimensionSpecs.java
   Value Object for Product Catalog Service
   Author: Nomhle Njengele (216227488)
   Date: 21 June 2026 */

package cput.ac.za.ecommerce.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public class DimensionSpecs {

    private double packageHeightCm;
    private double packageWidthCm;
    private double packageDepthCm;
    private double packageWeightKg;

    protected DimensionSpecs() {
    }

    private DimensionSpecs(Builder builder) {
        this.packageHeightCm =
                builder.packageHeightCm;

        this.packageWidthCm =
                builder.packageWidthCm;

        this.packageDepthCm =
                builder.packageDepthCm;

        this.packageWeightKg =
                builder.packageWeightKg;
    }

    public double getPackageHeightCm() {
        return packageHeightCm;
    }

    public double getPackageWidthCm() {
        return packageWidthCm;
    }

    public double getPackageDepthCm() {
        return packageDepthCm;
    }

    public double getPackageWeightKg() {
        return packageWeightKg;
    }

    public static class Builder {

        private double packageHeightCm;
        private double packageWidthCm;
        private double packageDepthCm;
        private double packageWeightKg;

        public Builder setPackageHeightCm(
                double packageHeightCm
        ) {
            this.packageHeightCm =
                    packageHeightCm;
            return this;
        }

        public Builder setPackageWidthCm(
                double packageWidthCm
        ) {
            this.packageWidthCm =
                    packageWidthCm;
            return this;
        }

        public Builder setPackageDepthCm(
                double packageDepthCm
        ) {
            this.packageDepthCm =
                    packageDepthCm;
            return this;
        }

        public Builder setPackageWeightKg(
                double packageWeightKg
        ) {
            this.packageWeightKg =
                    packageWeightKg;
            return this;
        }

        public DimensionSpecs build() {
            return new DimensionSpecs(this);
        }
    }
}