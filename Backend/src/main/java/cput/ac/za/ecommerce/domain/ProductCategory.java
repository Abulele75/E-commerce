package cput.ac.za.ecommerce.domain;

public enum ProductCategory {

    LAPTOPS(
            "CAT-001",
            "Laptops"
    ),

    DESKTOP_COMPUTERS(
            "CAT-002",
            "Desktop Computers"
    ),

    COMPUTER_COMPONENTS(
            "CAT-003",
            "Computer Components"
    ),

    SMARTPHONES(
            "CAT-004",
            "Smartphones"
    ),

    SMART_WATCHES(
            "CAT-005",
            "Smart Watches"
    ),

    ACCESSORIES(
            "CAT-006",
            "Accessories"
    );

    private final String categoryCode;
    private final String displayName;

    ProductCategory(
            String categoryCode,
            String displayName
    ) {
        this.categoryCode = categoryCode;
        this.displayName = displayName;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public String getDisplayName() {
        return displayName;
    }
}