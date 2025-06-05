package org.lite.quotes.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lite.quotes.filter.JwtRoleValidationFilter;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@AllArgsConstructor
@Slf4j
public class SecurityConfig {

    //It will be called even though you don't use it here, so don't remove it
    private final JwtRoleValidationFilter jwtRoleValidationFilter;

    @Bean
    JwtDecoder jwtDecoder(OAuth2ResourceServerProperties properties) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(properties.getJwt().getJwkSetUri()).build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                // Skip issuer validation or validate against multiple issuers
                token -> OAuth2TokenValidatorResult.success(),
                new JwtTimestampValidator()
        ));
        return decoder;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .x509(x509 -> x509
                        .subjectPrincipalRegex("CN=(.*?)(?:,|$)")  // Extract CN from the certificate
                        .x509PrincipalExtractor((principal -> {
                                    String dn = principal.getSubjectX500Principal().getName();
                                    log.info("Certificate DN: {}", dn);
                                    String cn = dn.split(",")[0].replace("CN=", "");
                                    log.info("Extracted CN: {}", cn);
                                    return cn;
                                })
                        ))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/r/quotes-service/**")  // Update the path to match your actual endpoint
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .oauth2ResourceServer(oauth2-> {
                   oauth2.jwt(Customizer.withDefaults());
                });

        return http.build();
    }
}