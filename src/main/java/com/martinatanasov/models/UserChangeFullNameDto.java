package com.martinatanasov.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record UserChangeFullNameDto(


        @Schema(description = "Full name: letters and single spaces only",
                examples = "Jane Doe")
        @NotBlank(message = "Full name is required")
        @Size(min = 2, max = 150, message = "Full name must be between 2 and 150 characters")
        @Pattern(
                regexp = "^[A-Za-z]+(?: [A-Za-z]+)*$",
                message = "Full name may contain only letters and single spaces (no new lines)"
        )
        String fullName
) {

}
