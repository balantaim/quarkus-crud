package com.martinatanasov.controllers;

import com.martinatanasov.entities.User;
import com.martinatanasov.models.AccessTokenResponse;
import com.martinatanasov.models.LoginResponse;
import com.martinatanasov.models.RefreshTokenDto;
import com.martinatanasov.repositories.UserRepository;
import com.martinatanasov.services.PasswordService;
import com.martinatanasov.services.TokenService;
import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.jwt.auth.principal.JWTAuthContextInfo;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestResponse;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Set;

@Tag(name = "Auth")
@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RunOnVirtualThread
@PermitAll
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final TokenService tokenService;
    private final JWTParser jwtParser;
    private final JWTAuthContextInfo refreshContext;
    private static final String HEADER_USER_ID = "User-Id";

    @Inject
    public AuthController(UserRepository userRepository,
            PasswordService passwordService,
            TokenService tokenService,
            JWTParser jwtParser,
            JWTAuthContextInfo defaultContext) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.tokenService = tokenService;
        this.jwtParser = jwtParser;
        // Same keys and issuer as the default config, but expects aud=refresh
        this.refreshContext = new JWTAuthContextInfo(defaultContext);
        this.refreshContext.setExpectedAudience(Set.of(TokenService.REFRESH_AUDIENCE));
    }

    @POST
    @Path("/login")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "User login",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class)
                    )
            ),
            @APIResponse(
                    responseCode = "401",
                    description = "Not authorized"
            )
    })
    public RestResponse<LoginResponse> login(@HeaderParam("Authorization") String authorization) {
        String[] credentials = decodeBasic(authorization);
        if (credentials == null) {
            throw new NotAuthorizedException("Basic");
        }
        User user = userRepository.findActiveByEmail(credentials[0].trim().toLowerCase())
                .filter(u -> passwordService.matches(credentials[1], u.getPassword()))
                .orElseThrow(() -> new NotAuthorizedException("Basic"));

        return RestResponse.ResponseBuilder
                .ok(issue(user))
                .header(HEADER_USER_ID, user.getUserId())
                .build();
    }

    @POST
    @Path("/refresh")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Refresh token success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccessTokenResponse.class)
                    )
            ),
            @APIResponse(
                    responseCode = "401",
                    description = "Not authorized"
            )
    })
    public RestResponse<AccessTokenResponse> refresh(@Valid RefreshTokenDto request) {
        try {
            var jwt = jwtParser.parse(request.refreshToken(), refreshContext);
            User user = userRepository.findActiveByUserId(jwt.getSubject())
                    .orElseThrow(() -> new NotAuthorizedException("Bearer"));

            return RestResponse.ok(new AccessTokenResponse(tokenService.createAccessToken(user)));
        } catch (ParseException e) {
            throw new NotAuthorizedException("Bearer");
        }
    }

    private LoginResponse issue(User user) {
        return new LoginResponse(
                tokenService.createAccessToken(user),
                tokenService.createRefreshToken(user));
    }

    private static String[] decodeBasic(String header) {
        if (header == null || !header.regionMatches(true, 0, "Basic ", 0, 6)) return null;
        try {
            String decoded = new String(Base64.getDecoder().decode(header.substring(6).trim()),
                    StandardCharsets.UTF_8);
            int i = decoded.indexOf(':');
            return i > 0 ? new String[]{decoded.substring(0, i), decoded.substring(i + 1)} : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
