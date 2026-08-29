
/*
   UserManagementFactory.java
   Owenkosi Nxasana (230240887)
   Date: 27 June 2026
 */
package cput.ac.za.ecommerce.factory;


import cput.ac.za.ecommerce.domain.*;
import cput.ac.za.ecommerce.util.ValidationPatterns;


import java.util.UUID;



public final class UserManagementFactory {


    private UserManagementFactory(){

    }



    public static Customer createCustomer(
            String firstName,
            String lastName,
            String email,
            String phoneNumber,
            String passwordHash
    ){

        AccountProfile profile =
                createAccountProfile(
                        firstName,
                        lastName,
                        email,
                        phoneNumber
                );


        if(passwordHash == null
                || passwordHash.isBlank()){

            throw new IllegalArgumentException(
                    "Password hash is required"
            );
        }



        return Customer.builder()
                .setUserId(generateUserId())
                .setAccountProfile(profile)
                .setPasswordHash(passwordHash)
                .setCustomerNumber(
                        generateCustomerNumber()
                )
                .setActive(true)
                .build();
    }





    public static AccountProfile createAccountProfile(
            String firstName,
            String lastName,
            String email,
            String phoneNumber
    ){

        String fName =
                ValidationPatterns
                        .normalizeName(firstName);



        String lName =
                ValidationPatterns
                        .normalizeName(lastName);



        String mail =
                ValidationPatterns
                        .normalizeEmail(email);



        String phone =
                ValidationPatterns
                        .normalizePhoneNumber(phoneNumber);




        if(!ValidationPatterns.isValidName(fName)){

            throw new IllegalArgumentException(
                    "Invalid first name"
            );
        }



        if(!ValidationPatterns.isValidName(lName)){

            throw new IllegalArgumentException(
                    "Invalid last name"
            );
        }



        if(!ValidationPatterns.isValidEmail(mail)){

            throw new IllegalArgumentException(
                    "Invalid email"
            );
        }



        if(!ValidationPatterns.isValidPhoneNumber(phone)){

            throw new IllegalArgumentException(
                    "Invalid phone number"
            );
        }




        return AccountProfile.builder()
                .setFirstName(fName)
                .setLastName(lName)
                .setEmail(mail)
                .setPhoneNumber(phone)
                .build();

    }





    private static String generateUserId(){

        return "USR-"+
                UUID.randomUUID()
                        .toString()
                        .replace("-","")
                        .substring(0,12)
                        .toUpperCase();
    }





    private static String generateCustomerNumber(){

        return "CUST-"+
                UUID.randomUUID()
                        .toString()
                        .replace("-","")
                        .substring(0,10)
                        .toUpperCase();
    }

}

