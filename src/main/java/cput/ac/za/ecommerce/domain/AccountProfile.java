/*
   AccountProfile.java
   Owenkosi Nxasana (230240887)
   Date: 20 June 2026
 */

package cput.ac.za.ecommerce.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;


@Embeddable
public class AccountProfile {


    @Column(
            name="first_name",
            nullable=false,
            length=50
    )
    private String firstName;



    @Column(
            name="last_name",
            nullable=false,
            length=50
    )
    private String lastName;



    @Column(
            name="email",
            nullable=false,
            length=254
    )
    private String email;



    @Column(
            name="phone_number",
            nullable=false,
            length=10
    )
    private String phoneNumber;



    protected AccountProfile(){

    }



    private AccountProfile(
            Builder builder
    ){

        this.firstName =
                builder.firstName;

        this.lastName =
                builder.lastName;

        this.email =
                builder.email;

        this.phoneNumber =
                builder.phoneNumber;
    }



    public static Builder builder(){

        return new Builder();
    }



    public String getFirstName(){

        return firstName;
    }



    public String getLastName(){

        return lastName;
    }



    public String getEmail(){

        return email;
    }



    public String getPhoneNumber(){

        return phoneNumber;
    }



    public String getFullName(){

        return firstName+" "+lastName;
    }



    public Builder toBuilder(){

        return new Builder()
                .setFirstName(firstName)
                .setLastName(lastName)
                .setEmail(email)
                .setPhoneNumber(phoneNumber);
    }




    @Override
    public boolean equals(Object obj){

        if(this==obj)
            return true;


        if(!(obj instanceof AccountProfile profile))
            return false;


        return Objects.equals(
                email,
                profile.email
        );
    }



    @Override
    public int hashCode(){

        return Objects.hash(email);
    }




    public static class Builder{


        private String firstName;

        private String lastName;

        private String email;

        private String phoneNumber;




        public Builder setFirstName(
                String firstName
        ){

            this.firstName =
                    firstName;

            return this;
        }




        public Builder setLastName(
                String lastName
        ){

            this.lastName =
                    lastName;

            return this;
        }




        public Builder setEmail(
                String email
        ){

            this.email =
                    email;

            return this;
        }




        public Builder setPhoneNumber(
                String phoneNumber
        ){

            this.phoneNumber =
                    phoneNumber;

            return this;
        }




        public AccountProfile build(){

            return new AccountProfile(this);
        }
    }
}