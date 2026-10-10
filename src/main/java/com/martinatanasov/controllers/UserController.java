package com.martinatanasov.controllers;

import com.martinatanasov.models.UserChangeFullNameDto;
import com.martinatanasov.models.UserChangePasswordDto;
import com.martinatanasov.models.UserDetailsDto;
import com.martinatanasov.models.UserRegisterDto;
import com.martinatanasov.results.PageUserResult;
import com.martinatanasov.results.UserResult;
import com.martinatanasov.services.UserService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/v1/users")
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
    public Response getAllUsers(
            @QueryParam("page") @DefaultValue("0") @Min(0) int page,
            @QueryParam("size") @DefaultValue("20") @Min(10) @Max(60) int size) {
        return handleResponse(userService.findAll(page, size));
    }

    @GET
    @Path("/{userId}")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Get user by userId",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDetailsDto.class)
                    )
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "No data found for the current user"
            )
    })
    public Response getById(@PathParam(value = "userId") @NotBlank @Size(max = 255) String userId) {
        return handleResponse(userService.findByUserId(userId));
    }

    @GET
    @Path("/email/{email}")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Get user by userId",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDetailsDto.class)
                    )
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "No data found for the current user"
            )
    })
    public Response getByEmail(@PathParam(value = "email") @NotBlank @Email @Size(max = 255) String email) {
        return handleResponse(userService.findByEmail(email));
    }

    @POST
    @Path("/change-password/{userId}")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Change password success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDetailsDto.class)
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Invalid credentials"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "No data found for the current user"
            )
    })
    public Response changePassword(
            @PathParam(value = "userId") @NotBlank @Size(max = 255) String userId,
            @Valid UserChangePasswordDto userChangePasswordDto) {
        return handleResponse(userService.changeUserPassword(
                userId, userChangePasswordDto.oldPassword(), userChangePasswordDto.newPassword())
        );
    }

    @PUT
    @Path("/update-fullname/{userId}")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Change fullname success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDetailsDto.class)
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Invalid user data"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "No data found for the current user"
            )
    })
    public Response changeFullName(
            @PathParam(value = "userId") @NotBlank @Size(max = 255) String userId,
            @Valid UserChangeFullNameDto userChangeFullNameDto) {
        return handleResponse(userService.changeUserFullName(userId, userChangeFullNameDto.fullName()));
    }

    @POST
    @Path("/register")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "A new user is created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDetailsDto.class)
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Invalid user data"
            ),
            @APIResponse(
                    responseCode = "409",
                    description = "User already exists"
            )
    })
    public Response createNewUser(@Valid UserRegisterDto userRegisterDto) {
        return handleResponse(userService.createUser(userRegisterDto.email(), userRegisterDto.fullName(), userRegisterDto.password()));
    }


    private static Response handleResponse(PageUserResult result) {
        return switch (result) {
            case PageUserResult.Success success -> Response.ok(success.users()).build();
            case PageUserResult.NotFound ignore -> Response.status(Response.Status.NOT_FOUND).build();
        };
    }

    private static Response handleResponse(UserResult result) {
        return switch (result) {
            case UserResult.Success success -> Response.ok(success.userDetailsDto()).build();
            case UserResult.AlreadyExists ignore -> Response.status(Response.Status.CONFLICT).build();
            case UserResult.NotFound ignore -> Response.status(Response.Status.NOT_FOUND).build();
        };
    }

}
