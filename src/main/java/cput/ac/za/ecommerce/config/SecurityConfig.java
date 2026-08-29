package cput.ac.za.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
            JwtAuthenticationConverter jwtAuthenticationConverter,
            CorsConfigurationSource corsConfigurationSource)
            throws Exception {http
                .csrf(csrf -> csrf.disable())

                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(authorize -> authorize

                        // CORS preflight requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**")
                        .permitAll()

                        // Error endpoint
                        .requestMatchers("/error")
                        .permitAll()

                        // Authentication
                        .requestMatchers("/api/auth/**")
                        .permitAll()

                        // Registration
                        .requestMatchers(HttpMethod.POST, "/api/users/register")
                        .permitAll()

                        // Public catalogue
                        .requestMatchers(HttpMethod.GET, "/api/productcatalog/**")
                        .permitAll()

                        // Public product reviews
                        .requestMatchers(HttpMethod.GET, "/api/feedback/product/**")
                        .permitAll()

                        // Catalogue management
                        .requestMatchers("/api/productcatalog/**")
                        .hasRole("ADMINISTRATOR")

                        // Everything else
                        .anyRequest()
                        .authenticated()
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter authoritiesConverter =
                new JwtGrantedAuthoritiesConverter();

        authoritiesConverter.setAuthoritiesClaimName("roles");
        authoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

        return converter;
    }
}