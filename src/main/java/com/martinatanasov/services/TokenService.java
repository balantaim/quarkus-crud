package com.martinatanasov.services;

import com.martinatanasov.entities.User;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class TokenService {

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    private String TOKEN_ISSUER;

    @ConfigProperty(name = "mp.jwt.verify.audiences")
    private String AUDIENCE;

    @ConfigProperty(name = "token.access-expiration")
    private long ACCESS_TOKEN_EXPIRATION;

    @ConfigProperty(name = "token.refresh-expiration")
    private long REFRESH_TOKEN_EXPIRATION;

    public static final String REFRESH_AUDIENCE = "refresh";

    public String createAccessToken(User user) {
        Set<String> roles = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toSet());

        return Jwt.issuer(TOKEN_ISSUER)
                .subject(user.getUserId())
                .upn(user.getEmail())
                .groups(roles)
                .audience(AUDIENCE)
                .expiresIn(Duration.ofMillis(ACCESS_TOKEN_EXPIRATION))
                .sign();
    }

    public String createRefreshToken(User user) {
        return Jwt.issuer(TOKEN_ISSUER)
                .subject(user.getUserId())
                .audience(REFRESH_AUDIENCE)
                .claim("type", REFRESH_AUDIENCE)
                .expiresIn(Duration.ofMillis(REFRESH_TOKEN_EXPIRATION))
                .sign();
    }

}
