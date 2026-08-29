/*
   User.java
   Owenkosi Nxasana (230240887)
   Date: 20 June 2026
 */

package cput.ac.za.ecommerce.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "app_user")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {

    @Id
    @Column(
            name = "user_id",
            nullable = false,
            updatable = false,
            length = 50
    )
    private String userId;


    @Embedded
    private AccountProfile accountProfile;


    @JsonIgnore
    @Column(
            name = "password_hash",
            nullable = false,
            length = 100
    )
    private String passwordHash;


    @Column(
            nullable = false
    )
    private boolean active = true;


    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;


    protected User() {

    }


    protected User(
            Builder<?> builder
    ) {

        this.userId =
                builder.userId;

        this.accountProfile =
                builder.accountProfile;

        this.passwordHash =
                builder.passwordHash;

        this.active =
                builder.active;

        this.createdAt =
                builder.createdAt != null
                        ? builder.createdAt
                        : LocalDateTime.now();
    }



    public String getUserId() {

        return userId;
    }



    public AccountProfile getAccountProfile() {

        return accountProfile;
    }



    public boolean isActive() {

        return active;
    }



    public LocalDateTime getCreatedAt() {

        return createdAt;
    }



    @JsonIgnore
    public String getPasswordHash() {

        return passwordHash;
    }



    public abstract UserRole getRole();



    public void updateProfile(
            AccountProfile updatedProfile
    ) {

        if(updatedProfile == null){

            throw new IllegalArgumentException(
                    "Updated profile is required"
            );
        }


        this.accountProfile =
                updatedProfile;
    }



    public void changePasswordHash(
            String updatedPasswordHash
    ){

        if(updatedPasswordHash == null
                || updatedPasswordHash.isBlank()){

            throw new IllegalArgumentException(
                    "Password hash is required"
            );
        }


        this.passwordHash =
                updatedPasswordHash;
    }



    public void activate(){

        this.active = true;
    }



    public void deactivate(){

        this.active = false;
    }



    protected abstract static class Builder<T extends Builder<T>> {


        protected String userId;

        protected AccountProfile accountProfile;

        protected String passwordHash;

        protected boolean active = true;

        protected LocalDateTime createdAt =
                LocalDateTime.now();



        public T setUserId(
                String userId
        ){

            this.userId =
                    userId;

            return self();
        }



        public T setAccountProfile(
                AccountProfile accountProfile
        ){

            this.accountProfile =
                    accountProfile;

            return self();
        }



        public T setPasswordHash(
                String passwordHash
        ){

            this.passwordHash =
                    passwordHash;

            return self();
        }



        public T setActive(
                boolean active
        ){

            this.active =
                    active;

            return self();
        }



        public T setCreatedAt(
                LocalDateTime createdAt
        ){

            this.createdAt =
                    createdAt;

            return self();
        }



        protected abstract T self();
    }
}