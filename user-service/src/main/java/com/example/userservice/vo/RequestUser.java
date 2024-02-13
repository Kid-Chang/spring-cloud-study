package com.example.userservice.vo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class RequestUser {
    @NotNull(message = "Email cannot be null")
    private String email;
    @Email
    @Size(min = 2, message = "Name cannot be less than two characters")
    @NotNull(message = "Name cannot be null")
    private String name;
    @Size(min = 8, message = "Password must be equal or greater than 8 characters")
    @NotNull(message = "Password cannot be null")
    private String pwd;
}
