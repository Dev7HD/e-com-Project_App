package ma.dev7hd.ecominventoryservice.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter=new JwtGrantedAuthoritiesConverter();


    /**
     * Converts a given JWT into an {@code AbstractAuthenticationToken}.
     * This method extracts authorities from the JWT by combining those derived
     * through a {@link JwtGrantedAuthoritiesConverter} with roles extracted from
     * the "realm_access" claim of the token. The resulting authorities are then
     * used to create a {@link JwtAuthenticationToken}.
     *
     * @param jwt the {@code Jwt} to be converted into an authentication token
     * @return an {@code AbstractAuthenticationToken} that represents the given JWT,
     *         including its granted authorities and "preferred_username" claim
     */
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                extractResourceRoles(jwt).stream()
        ).collect(Collectors.toSet());
        return new JwtAuthenticationToken(jwt, authorities,jwt.getClaim("preferred_username"));
    }

    /**
     * Extracts roles from the "realm_access" claim of the provided JWT and converts them into a collection
     * of {@code GrantedAuthority}.
     *
     * This method retrieves the "roles" collection from the "realm_access" claim in the JWT. If the claim
     * is absent or does not contain any roles, it returns an empty collection. Each role is transformed
     * into a {@code SimpleGrantedAuthority}.
     *
     * @param jwt the {@code Jwt} object from which the roles are extracted
     * @return a {@code Collection<GrantedAuthority>} containing the roles as {@code SimpleGrantedAuthority},
     *         or an empty collection if no roles are present
     */
    private Collection<GrantedAuthority> extractResourceRoles(Jwt jwt) {
        Map<String , Object> realmAccess;
        Collection<String> roles;
        if(jwt.getClaim("realm_access")==null){
            return Set.of();
        }
        realmAccess = jwt.getClaim("realm_access");
        roles = (Collection<String>) realmAccess.get("roles");
        return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }
}