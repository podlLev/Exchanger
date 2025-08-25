package com.exchanger.dto;

import com.exchanger.model.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
public class UserDto {

    @NotNull(message = "ID cannot be null")
    private UUID id;

    @NotBlank(message = "Username cannot be blank")
    private String username;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @NotNull(message = "Status cannot be null")
    private UserStatus status;

}
