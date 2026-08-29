package cput.ac.za.ecommerce.controller;


import cput.ac.za.ecommerce.request.LoginRequest;
import cput.ac.za.ecommerce.response.AuthResponse;
import cput.ac.za.ecommerce.service.IAuthService;


import jakarta.validation.Valid;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/auth")
public class AuthController {



    private final IAuthService authService;



    public AuthController(
            IAuthService authService
    ){

        this.authService = authService;

    }




    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(

            @Valid
            @RequestBody
            LoginRequest request

    ){


        return ResponseEntity.ok(

                authService.login(
                        request
                )

        );

    }

}