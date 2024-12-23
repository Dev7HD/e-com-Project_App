package ma.dev7hd.ecomorderservice.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {
    public static String clientId = "e-com_client";

    public String clientSecret = "tdP4NuHTfD2WcRfly5IrOWcGzb975Ot4";

    private String username = "admin";

    private String password = "1234";

    public String realm = "e-com-realm";

    public String kcServerUrl = "http://localhost:8080/";


    /**
     * Configures and provides a Keycloak client instance. This instance is used to connect
     * to the Keycloak server using the specified configuration parameters, including
     * the server URL, realm, client ID, client secret, username, password, and grant type.
     *
     * @return an instance of {@link Keycloak} built with the configured parameters
     *         for interacting with the Keycloak server.
     */
    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(kcServerUrl)       // Replace with your Keycloak server URL
                .realm(realm)                          // Use "master" for admin, or your custom realm
                .clientId(clientId)                    // "admin-cli" is typically used for admin access
                .clientSecret(clientSecret)
                .username(username)               // Admin username for Keycloak
                .password(password)               // Admin password for Keycloak
                .grantType(OAuth2Constants.PASSWORD)                    // Grant type, "password" for direct access
                .build();
    }
}
