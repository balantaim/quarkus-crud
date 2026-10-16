package com.martinatanasov.controllers;

import com.martinatanasov.models.request.RefreshTokenRequest;
import com.martinatanasov.models.response.LoginResponse;
import com.martinatanasov.models.response.RefreshTokenResponse;
import com.martinatanasov.results.LoginResult;
import com.martinatanasov.results.RefreshResult;
import com.martinatanasov.services.AuthFacadeService;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestResponse;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Tag(name = "Auth")
@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RunOnVirtualThread
@PermitAll
class AuthController {

    private final AuthFacadeService authFacadeService;
    private static final String BASIC = "Basic";
    private static final String HEADER_USER_ID = "User-Id";

    @Inject
    AuthController(AuthFacadeService authFacadeService) {
        this.authFacadeService = authFacadeService;
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
            throw new NotAuthorizedException(BASIC);
        }
        final String email = credentials[0].trim().toLowerCase();
        final String password = credentials[1];

        return handleResponse(authFacadeService.authenticate(email, password));
    }

    @POST
    @Path("/refresh")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Refresh token success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RefreshTokenResponse.class)
                    )
            ),
            @APIResponse(
                    responseCode = "401",
                    description = "Not authorized"
            )
    })
    public RestResponse<RefreshTokenResponse> refresh(@Valid RefreshTokenRequest request) {
        return handleResponse(authFacadeService.refresh(request.refreshToken()));
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

    private RestResponse<LoginResponse> handleResponse(LoginResult result) {
        return switch (result) {
            case LoginResult.Success success -> RestResponse.ResponseBuilder
                    .ok(new LoginResponse(success.accessToken(), success.refreshToken()))
                    .header(HEADER_USER_ID, success.userId())
                    .build();
            case LoginResult.Failure ignore -> RestResponse.status(Response.Status.UNAUTHORIZED);
        };
    }

    private RestResponse<RefreshTokenResponse> handleResponse(RefreshResult result) {
        return switch (result) {
            case RefreshResult.Success success ->  RestResponse.ok(new RefreshTokenResponse(success.accessToken()));
            case RefreshResult.Failure ignore -> RestResponse.status(Response.Status.UNAUTHORIZED);
        };
    }

}
