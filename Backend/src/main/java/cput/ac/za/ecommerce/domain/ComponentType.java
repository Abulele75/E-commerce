package cput.ac.za.ecommerce.domain;


public enum ComponentType {


    GRAPHICS_CARD(
            "Graphics Card"
    ),


    MEMORY(
            "RAM / Memory"
    ),


    PROCESSOR(
            "Processor / CPU"
    ),


    MOTHERBOARD(
            "Motherboard"
    ),


    STORAGE(
            "Storage Device"
    ),


    POWER_SUPPLY(
            "Power Supply Unit"
    ),


    COOLING_SYSTEM(
            "Cooling System"
    ),


    COMPUTER_CASE(
            "Computer Case"
    ),


    NETWORKING(
            "Networking Equipment"
    ),


    ACCESSORY_COMPONENT(
            "Accessory Component"
    ),


    OTHER(
            "Other"
    );



    private final String displayName;



    ComponentType(
            String displayName
    ) {

        this.displayName = displayName;

    }



    public String getDisplayName() {

        return displayName;

    }

}