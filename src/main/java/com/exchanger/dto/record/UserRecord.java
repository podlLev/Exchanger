package com.exchanger.dto.record;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRecord(
        @NotBlank(message = "First name cannot be blank")
        String firstName,

        @NotBlank(message = "Last name cannot be blank")
        String lastName,

        @Email(message = "Invalid email format")
        @NotBlank(message = "Email cannot be blank")
        String email
) {

}
