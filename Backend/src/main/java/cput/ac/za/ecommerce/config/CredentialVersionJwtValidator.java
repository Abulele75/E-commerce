package cput.ac.za.ecommerce.config;

import cput.ac.za.ecommerce.domain.User;
import cput.ac.za.ecommerce.repository.UserManagementRepository;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class CredentialVersionJwtValidator
        implements OAuth2TokenValidator<Jwt> {

    private static final OAuth2Error INVALID_TOKEN =
            new OAuth2Error(
                    "invalid_token",
                    "The session is no longer valid",
                    null
            );

    private final UserManagementRepository userRepository;

    public CredentialVersionJwtValidator(
            UserManagementRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        String userId = jwt.getClaimAsString("userId");
        Number version = jwt.getClaim("credentialVersion");

        if (userId == null || version == null) {
            return OAuth2TokenValidatorResult.failure(
                    INVALID_TOKEN
            );
        }

        User user = userRepository.findById(userId)
                .orElse(null);

        if (user == null
                || !user.isActive()
                || user.getCredentialVersion()
                != version.intValue()) {
            return OAuth2TokenValidatorResult.failure(
                    INVALID_TOKEN
            );
        }

        return OAuth2TokenValidatorResult.success();
    }
}
