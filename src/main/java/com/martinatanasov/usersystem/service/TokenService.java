package com.martinatanasov.usersystem.service;

import com.martinatanasov.usersystem.entity.User;
import io.smallrye.jwt.algorithm.SignatureAlgorithm;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class TokenService {

    public static final String REFRESH_AUDIENCE = "refresh";
    private static final String TOKEN_TYPE_CLAIM = "token_type";
    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final String REFRESH_TOKEN_TYPE = "refresh";
    private static final String AUTHORITIES_CLAIM = "authorities";
    private final String TOKEN_ISSUER;
    private final String AUDIENCE;
    private final long ACCESS_TOKEN_EXPIRATION;
    private final long REFRESH_TOKEN_EXPIRATION;

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
        Set<String> authorities = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());

        Instant timeNow = Instant.now();

        return Jwt.claims()
                .issuer(TOKEN_ISSUER)
                // "sub": the public user UUID identifying the owner of the token
                .subject(user.getUserId())
                // "aud": who the token is for (checked against mp.jwt.verify.audiences)
                .audience(AUDIENCE)
                // "token_type": custom claim, marks this as an access token
                .claim(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_TYPE)
                // "iat": the moment the token was created
                .issuedAt(timeNow)
                // "exp": the token stops being valid after this moment
                .expiresAt(timeNow.plusMillis(ACCESS_TOKEN_EXPIRATION))
                // "authorities": the user's roles, used by @RolesAllowed
                // (needs smallrye.jwt.path.groups=authorities)
                .claim(AUTHORITIES_CLAIM, authorities)
                // Start building the signed token (JWS)
                .jws()
                // Sign with RSA + SHA-256, using smallrye.jwt.sign.key.location
                .algorithm(SignatureAlgorithm.RS256)
                // Sign and return the compact string header.payload.signature
                .sign();
    }

    public String createRefreshToken(final String userId) {
        Instant timeNow = Instant.now();

        return Jwt.claims()
                // "iss": who issued the token
                .issuer(TOKEN_ISSUER)
                // "sub": the public user UUID, used to find the user on refresh
                .subject(userId)
                // "aud": "refresh", so it is rejected as a normal bearer token
                .audience(REFRESH_AUDIENCE)
                // "token_type": custom claim
                .claim(TOKEN_TYPE_CLAIM, Collections.emptySet())
                // "iat": the moment the token was created
                .issuedAt(timeNow)
                // "exp": the (longer) refresh lifetime
                .expiresAt(timeNow.plusMillis(REFRESH_TOKEN_EXPIRATION))
                // No "authorities" claim: a refresh token only gets a new access token
                // Start building the signed token (JWS)
                .jws()
                // Sign with RSA + SHA-256
                .algorithm(SignatureAlgorithm.RS256)
                // Sign and return the compact string
                .sign();
    }

}
