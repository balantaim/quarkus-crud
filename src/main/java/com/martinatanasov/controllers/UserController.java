package com.martinatanasov.controllers;

import com.martinatanasov.models.UserDetailsDto;
import com.martinatanasov.results.PageUserResult;
import com.martinatanasov.services.UserService;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/v1")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Users")
public class UserController {

    @Inject
    private UserService userService;

    @GET
    @Path("/")
    // Swagger
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Get all users as page",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDetailsDto.class)
                    )
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "No data found for the current page"
            )
    })
//    @RolesAllowed("admin")
    public Response getUserByEmail(
            @QueryParam("page") @DefaultValue("0") @Min(0) int page,
            @QueryParam("size") @DefaultValue("20") @Min(1) @Max(100) int size
    ) {
        return handleResponse(userService.findAll(page, size));
    }

    private static Response handleResponse(PageUserResult result) {
        return switch (result) {
            case PageUserResult.Success success -> Response.ok(success.users()).build();
            case PageUserResult.NotFound ignored -> Response.status(Response.Status.NOT_FOUND).build();
        };
    }


}
