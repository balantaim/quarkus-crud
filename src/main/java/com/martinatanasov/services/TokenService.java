package com.martinatanasov.services;

import com.martinatanasov.entities.User;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class TokenService {

    private final String TOKEN_ISSUER;
    private final String AUDIENCE;
    private final long ACCESS_TOKEN_EXPIRATION;
    private final long REFRESH_TOKEN_EXPIRATION;
    public static final String REFRESH_AUDIENCE = "refresh";

    @Inject
    public TokenService(
            @ConfigProperty(name = "mp.jwt.verify.issuer") String tokenIssuer,
            @ConfigProperty(name = "mp.jwt.verify.audiences") String audience,
            @ConfigProperty(name = "token.access-expiration") long accessTokenExpiration,
            @ConfigProperty(name = "token.refresh-expiration") long refreshTokenExpiration) {
        this.TOKEN_ISSUER = tokenIssuer;
        this.AUDIENCE = audience;
        this.ACCESS_TOKEN_EXPIRATION = accessTokenExpiration;
        this.REFRESH_TOKEN_EXPIRATION = refreshTokenExpiration;
    }

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
