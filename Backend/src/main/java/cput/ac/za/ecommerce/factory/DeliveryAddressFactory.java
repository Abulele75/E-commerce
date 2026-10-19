package cput.ac.za.ecommerce.factory;

import cput.ac.za.ecommerce.domain.DeliveryAddress;
import cput.ac.za.ecommerce.util.ValidationPatterns;

public final class DeliveryAddressFactory {

    private DeliveryAddressFactory() {
    }

    public static DeliveryAddress
    createDeliveryAddress(
            String recipientName,
            String recipientPhone,
            String streetAddress,
            String suburb,
            String city,
            String province,
            String postalCode,
            String country
    ) {
        String normalizedName =
                ValidationPatterns.normalizeName(
                        recipientName
                );

        String normalizedPhone =
                ValidationPatterns
                        .normalizePhoneNumber(
                                recipientPhone
                        );

        if (!ValidationPatterns.isValidName(
                normalizedName
        )) {
            return null;
        }

        if (!ValidationPatterns
                .isValidPhoneNumber(
                        normalizedPhone
                )) {
            return null;
        }

        if (isBlank(streetAddress)
                || isBlank(city)
                || isBlank(province)
                || postalCode == null
                || !postalCode.matches(
                "^\\d{4}$"
        )) {
            return null;
        }

        return new DeliveryAddress.Builder()
                .setRecipientName(
                        normalizedName
                )
                .setRecipientPhone(
                        normalizedPhone
                )
                .setStreetAddress(
                        streetAddress.trim()
                )
                .setSuburb(
                        trimToNull(suburb)
                )
                .setCity(city.trim())
                .setProvince(province.trim())
                .setPostalCode(
                        postalCode.trim()
                )
                .setCountry(
                        isBlank(country)
                                ? "South Africa"
                                : country.trim()
                )
                .build();
    }

    private static boolean isBlank(
            String value
    ) {
        return value == null
                || value.isBlank();
    }

    private static String trimToNull(
            String value
    ) {
        return isBlank(value)
                ? null
                : value.trim();
    }
}