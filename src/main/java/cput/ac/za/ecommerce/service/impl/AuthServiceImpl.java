package cput.ac.za.ecommerce.service.impl;


import cput.ac.za.ecommerce.domain.User;
import cput.ac.za.ecommerce.repository.UserManagementRepository;
import cput.ac.za.ecommerce.request.LoginRequest;
import cput.ac.za.ecommerce.request.RegisterRequest;
import cput.ac.za.ecommerce.response.AuthResponse;
import cput.ac.za.ecommerce.response.UserResponse;
import cput.ac.za.ecommerce.service.IAuthService;


import jakarta.persistence.EntityNotFoundException;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import org.springframework.stereotype.Service;


import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;



@Service
public class AuthServiceImpl implements IAuthService {


    private final UserManagementRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtEncoder jwtEncoder;


    @Value("${security.jwt.issuer}")
    private String issuer;

    @Value("${security.jwt.expiration-seconds}")
    private long expirationSeconds;


    public AuthServiceImpl(
            UserManagementRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtEncoder jwtEncoder
    ) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;

    }


    @Override
    public UserResponse register(RegisterRequest request) {
        return null;
    }

    @Override
    public AuthResponse login(
            LoginRequest request
    ) {


        if (request == null) {

            throw new IllegalArgumentException(
                    "Login details are required"
            );

        }



        User user =
                userRepository
                        .findByEmailIgnoreCase(
                                request.getEmail()
                        )
                        .orElseThrow(() -> new EntityNotFoundException("Invalid email or password"));


        if (!user.isActive())
        {
            throw new IllegalStateException("Account is inactive");

        }


        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        )) {

            throw new IllegalArgumentException(
                    "Invalid email or password"
            );

        }

        String token = generateToken(user);

        return new AuthResponse(token, user.getUserId(), user.getAccountProfile().getEmail(), user.getRole().name());

    }

    private String generateToken(User user)
    {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder().issuer(issuer).issuedAt(now)
                        .expiresAt(now.plus(expirationSeconds, ChronoUnit.SECONDS))
                        .subject(user.getAccountProfile().getEmail())
                        .claim("roles", List.of(user.getRole().name()))
                        .claim("userId", user.getUserId())
                        .build();

        JwsHeader headers = JwsHeader
                .with(MacAlgorithm.HS256)
                .type("JWT")
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(headers,claims)).getTokenValue();

    }

}