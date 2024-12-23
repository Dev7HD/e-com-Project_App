package ma.dev7hd.ecomorderservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.SecurityFilterChain;

import java.util.*;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@ComponentScan(basePackages = "ma.dev7hd.ecomorderservice.security")
public class SecurityConfiguration {
    private final JwtAuthConverter jwtAuthConverter;

    /**
     * Represents an array of URL patterns that are whitelisted and do not require authentication.
     * These URLs are permitted access without any security constraints.
     * Commonly used for public endpoints, API documentation, actuator endpoints, and initial authentication.
     */
    private static final String[] WHITE_LIST_URL = { "/api/v1/auth/**", "/v2/api-docs", "/v3/api-docs",
            "/v3/api-docs/**", "/swagger-resources", "/swagger-resources/**", "/configuration/ui",
            "/configuration/security", "/swagger-ui/**", "/webjars/**", "/swagger-ui.html", "/api/auth/**",
            "/api/test/**", "/authenticate", "/actuator/**", "/h2-console/**" };

    /**
     * Constructs a new SecurityConfiguration instance.
     *
     * @param jwtAuthConverter the JwtAuthConverter used to convert JWT tokens into authentication tokens and extract authorities
     */
    public SecurityConfiguration(JwtAuthConverter jwtAuthConverter) {
        this.jwtAuthConverter = jwtAuthConverter;
    }

    /**
     * Configures the security filter chain for the application.
     *
     * The method sets up a stateless session management policy, disables CSRF protection,
     * configures request authorization rules to allow access to white-listed URLs while
     * requiring authentication for all other requests, disables frame options headers,
     * and configures OAuth2 resource server with a custom JWT authentication converter.
     *
     * @param http the {@code HttpSecurity} object to configure security settings
     * @return a configured {@code SecurityFilterChain} instance
     * @throws Exception if an error occurs during the configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement(sm->sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(WHITE_LIST_URL).permitAll()
                                .anyRequest().authenticated()
                )
                .headers(h->h.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .oauth2ResourceServer(ors->ors.jwt(jwt->jwt.jwtAuthenticationConverter(jwtAuthConverter)))
        ;

        return http.build();
    }

    /**
     * Maps a collection of granted authorities to another collection by processing OIDC claims.
     * For each authority of type {@code OidcUserAuthority}, it retrieves the ID token claims
     * and maps them to a set of new granted authorities using the {@code mapAuthorities} method.
     *
     * @return a {@code GrantedAuthoritiesMapper} that maps the provided authorities into a
     *         set of newly processed {@code GrantedAuthority} objects.
     */
    @Bean
    public GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return (authorities) -> {
            final Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
            authorities.forEach((authority) -> {
                if (authority instanceof OidcUserAuthority oidcAuth) {
                    mappedAuthorities.addAll(mapAuthorities(oidcAuth.getIdToken().getClaims()));
                }
            });
            return mappedAuthorities;
        };
    }

    /**
     * Maps the roles from the specified attributes to a list of {@link SimpleGrantedAuthority}.
     * This method extracts the "roles" collection from the "realm_access" attribute
     * and transforms each role into a {@code SimpleGrantedAuthority}.
     *
     * @param attributes a {@code Map<String, Object>} representing the attributes from which
     *                   roles are extracted. Typically, these attributes come from a JWT token.
     * @return a {@code List<SimpleGrantedAuthority>} containing the mapped authorities
     *         corresponding to the roles found in the provided attributes.
     */
    private List<SimpleGrantedAuthority> mapAuthorities(final Map<String, Object> attributes) {
        final Map<String, Object> realmAccess = ((Map<String, Object>)attributes.getOrDefault("realm_access", Collections.emptyMap()));
        final Collection<String> roles = ((Collection<String>)realmAccess.getOrDefault("roles", Collections.emptyList()));
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

}