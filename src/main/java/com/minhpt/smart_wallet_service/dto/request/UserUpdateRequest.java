package com.minhpt.smart_wallet_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserUpdateRequest {

    private String fullName;

    @Pattern(regexp = "^(0|\\+84)[0-9]{9}$", message = "Invalid phone number")
    private String phoneNumber;

    @Email(message = "Invalid Email")
    private String email;
}
