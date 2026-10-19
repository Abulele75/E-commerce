package cput.ac.za.ecommerce.service.impl;

import cput.ac.za.ecommerce.domain.PasswordResetToken;
import cput.ac.za.ecommerce.domain.User;
import cput.ac.za.ecommerce.repository.PasswordResetTokenRepository;
import cput.ac.za.ecommerce.repository.UserManagementRepository;
import cput.ac.za.ecommerce.request.ForgotPasswordRequest;
import cput.ac.za.ecommerce.request.LoginRequest;
import cput.ac.za.ecommerce.request.ResetPasswordRequest;
import cput.ac.za.ecommerce.response.AuthResponse;
import cput.ac.za.ecommerce.response.ForgotPasswordResponse;
import cput.ac.za.ecommerce.service.IAuthService;
import cput.ac.za.ecommerce.util.ValidationPatterns;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Service
public class AuthServiceImpl implements IAuthService {

    private static final String INVALID_CREDENTIALS =
            "Invalid email or password";

    private static final String RESET_RESPONSE =
            "If an eligible account exists, use the reset instructions provided.";

    private static final String INVALID_RESET_TOKEN =
            "Reset token is invalid or expired";

    private final UserManagementRepository userRepository;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${security.jwt.issuer}")
    private String issuer;

    @Value("${security.jwt.expiration-seconds}")
    private long expirationSeconds;

    @Value("${app.password-reset.ttl-minutes:15}")
    private long resetTokenTtlMinutes;

    @Value("${app.password-reset.expose-token:true}")
    private boolean exposeResetToken;

    public AuthServiceImpl(
            UserManagementRepository userRepository,
            PasswordResetTokenRepository resetTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtEncoder jwtEncoder
    ) {
        this.userRepository = userRepository;
        this.resetTokenRepository = resetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        if (request == null) {
            throw new BadCredentialsException(INVALID_CREDENTIALS);
        }

        String email = ValidationPatterns.normalizeEmail(
                request.getEmail()
        );

        User user = userRepository
                .findByEmailIgnoreCase(email)
                .filter(User::isActive)
                .orElseThrow(() ->
                        new BadCredentialsException(
                                INVALID_CREDENTIALS
                        )
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        )) {
            throw new BadCredentialsException(
                    INVALID_CREDENTIALS
            );
        }

        user.recordSuccessfulLogin();
        userRepository.save(user);

        return new AuthResponse(
                generateToken(user),
                user.getUserId(),
                user.getAccountProfile().getEmail(),
                user.getRole().name()
        );
    }

    @Override
    @Transactional
    public ForgotPasswordResponse forgotPassword(
            ForgotPasswordRequest request
    ) {
        String rawToken = generateResetToken();
        LocalDateTime now = LocalDateTime.now();

        userRepository.findByEmailIgnoreCase(
                        ValidationPatterns.normalizeEmail(
                                request.getEmail()
                        )
                )
                .filter(User::isActive)
                .ifPresent(user -> {
                    List<PasswordResetToken> existing =
                            resetTokenRepository
                                    .findAllByUser_UserIdAndUsedAtIsNull(
                                            user.getUserId()
                                    );

                    existing.forEach(token ->
                            token.markUsed(now)
                    );
                    resetTokenRepository.saveAll(existing);

                    resetTokenRepository.save(
                            new PasswordResetToken(
                                    "RST-" + UUID.randomUUID()
                                            .toString()
                                            .replace("-", "")
                                            .substring(0, 16)
                                            .toUpperCase(),
                                    user,
                                    hashToken(rawToken),
                                    now,
                                    now.plusMinutes(
                                            resetTokenTtlMinutes
                                    )
                            )
                    );
                });

        return new ForgotPasswordResponse(
                RESET_RESPONSE,
                exposeResetToken ? rawToken : null
        );
    }

    @Override
    @Transactional
    public void resetPassword(
            ResetPasswordRequest request
    ) {
        LocalDateTime now = LocalDateTime.now();
        PasswordResetToken resetToken =
                resetTokenRepository
                        .findByTokenHashForUpdate(
                                hashToken(request.getToken())
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        INVALID_RESET_TOKEN
                                )
                        );

        User user = resetToken.getUser();
        if (!resetToken.isUsableAt(now)
                || !user.isActive()) {
            throw new IllegalArgumentException(
                    INVALID_RESET_TOKEN
            );
        }

        if (!request.getNewPassword().equals(
                request.getConfirmPassword()
        )) {
            throw new IllegalArgumentException(
                    "New passwords do not match"
            );
        }

        if (passwordEncoder.matches(
                request.getNewPassword(),
                user.getPasswordHash()
        )) {
            throw new IllegalArgumentException(
                    "New password must be different from the current password"
            );
        }

        user.changePasswordHash(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );
        resetToken.markUsed(now);

        userRepository.save(user);
        resetTokenRepository.save(resetToken);
    }

    private String generateToken(User user) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plus(
                        expirationSeconds,
                        ChronoUnit.SECONDS
                ))
                .subject(user.getAccountProfile().getEmail())
                .claim("roles", List.of(
                        user.getRole().name()
                ))
                .claim("userId", user.getUserId())
                .claim(
                        "credentialVersion",
                        user.getCredentialVersion()
                )
                .build();

        JwsHeader headers = JwsHeader
                .with(MacAlgorithm.HS256)
                .type("JWT")
                .build();

        return jwtEncoder.encode(
                        JwtEncoderParameters.from(
                                headers,
                                claims
                        )
                )
                .getTokenValue();
    }

    private String generateResetToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private String hashToken(String rawToken) {
        try {
            byte[] digest = MessageDigest
                    .getInstance("SHA-256")
                    .digest(rawToken.getBytes(
                            StandardCharsets.UTF_8
                    ));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "Password reset is unavailable",
                    exception
            );
        }
    }
}
