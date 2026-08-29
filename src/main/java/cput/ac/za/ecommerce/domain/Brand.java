package cput.ac.za.ecommerce.domain;


public enum Brand {


    APPLE(
            "Apple"
    ),


    SAMSUNG(
            "Samsung"
    ),


    HUAWEI(
            "Huawei"
    ),


    HONOR(
            "HONOR"
    ),


    LENOVO(
            "Lenovo"
    ),


    LEGION(
            "Legion"
    ),


    MSI(
            "MSI"
    ),


    ASUS(
            "ASUS"
    ),


    VICTUS(
            "Victus"
    ),


    REDRAGON(
            "Redragon"
    ),


    LOGITECH(
            "Logitech"
    ),


    NVIDIA(
            "NVIDIA"
    ),


    CORSAIR(
            "Corsair"
    ),


    MICROSOFT(
            "Microsoft"
    ),


    PLAYSTATION(
            "PlayStation"
    ),


    DELL(
            "Dell"
    ),


    OTHER(
            "Other"
    );



    private final String displayName;



    Brand(
            String displayName
    ) {

        this.displayName = displayName;

    }



    public String getDisplayName() {

        return displayName;

    }


}