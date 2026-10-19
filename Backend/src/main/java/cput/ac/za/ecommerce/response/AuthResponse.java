
/*
   AuthResponse.java
   Authentication response object
*/

package cput.ac.za.ecommerce.response;


public class AuthResponse {


    private String token;

    private String type;

    private String userId;

    private String email;

    private String role;


    public AuthResponse() {
    }


    public AuthResponse(
            String token,
            String userId,
            String email,
            String role
    ) {

        this.token = token;
        this.type = "Bearer";
        this.userId = userId;
        this.email = email;
        this.role = role;

    }


    public String getToken() {
        return token;
    }


    public void setToken(
            String token
    ) {
        this.token = token;
    }


    public String getType() {
        return type;
    }


    public void setType(
            String type
    ) {
        this.type = type;
    }


    public String getUserId() {
        return userId;
    }


    public void setUserId(
            String userId
    ) {
        this.userId = userId;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(
            String email
    ) {
        this.email = email;
    }


    public String getRole() {
        return role;
    }


    public void setRole(
            String role
    ) {
        this.role = role;
    }
}
