package ma.dev7hd.ecomorderservice.config;

import feign.RequestInterceptor;
import lombok.AllArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
@ComponentScan(basePackages = "ma.dev7hd.ecomorderservice.config")
public class FeignClientConfig {

    private final Keycloak keycloak;

    /**
     * Creates a {@link RequestInterceptor} to inject the Keycloak access token
     * into the Authorization header of outgoing requests. The interceptor retrieves
     * the access token from the Keycloak token manager and appends it as a Bearer token
     * to ensure proper authentication for secured endpoints.
     *
     * @return a {@link RequestInterceptor} that adds the Keycloak Bearer token to
     *         the Authorization header of HTTP requests.
     */
    @Bean
    public RequestInterceptor keycloakAuthInterceptor() {
        return template -> {
            // Retrieve the access token from Keycloak's token manager
            String accessToken = keycloak.tokenManager().getAccessTokenString();

            // Add the Authorization header with Bearer token
            template.header("Authorization", "Bearer " + accessToken);

            System.out.println("Bearer token has been added to the request: " + accessToken);
        };
    }
}
