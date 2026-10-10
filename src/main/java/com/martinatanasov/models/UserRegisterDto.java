package com.martinatanasov.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(
        description = "Request body for registering a new customer account",
        examples = """
                {
                  "email": "jane.doe@example.com",
                  "fullName": "Jane Doe",
                  "password": "Str0ng!Passw0rd"
                }
                """
)
public record UserRegisterDto(

        @Schema(description = "Unique email address used to log in",
                examples = "jane.doe@example.com",
                maxLength = 255)
        @NotBlank(message = "Email is required")
        @Pattern(
                regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$",
                message = "Invalid email format"
        )
        String email,

        @Schema(description = "Full name: letters and single spaces only",
                examples = "Jane Doe")
        @NotBlank(message = "Full name is required")
        @Size(min = 2, max = 150, message = "Full name must be between 2 and 150 characters")
        @Pattern(
                regexp = "^[A-Za-z]+(?: [A-Za-z]+)*$",
                message = "Full name may contain only letters and single spaces (no new lines)"
        )
        String fullName,

        @Schema(description = "8-50 characters with upper and lower case letters, a digit and a special character",
                examples = "Str0ng!Passw0rd",
                format = "password",
                writeOnly = true)
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,50}$",
                message = "Password must contain uppercase, lowercase, number, and special character"
        )
        String password

) {

}
