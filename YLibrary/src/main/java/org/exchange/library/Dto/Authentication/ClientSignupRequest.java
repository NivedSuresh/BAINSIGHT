package org.exchange.library.Dto.Authentication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientSignupRequest {
    @NotBlank(message = "Email cannot be blank!")
    private String email;
    @Size(min = 10, max = 10, message = "Phone number should of exactly 10 characters")
    private String phoneNumber;
    @Size(min = 5, max = 15, message = "Username should fall between 5-15 characters")
    private String username;
    @Size(min = 5, message = "Password should be minimum 5 characters!")
    private String password;
    //Can be blank if it's an update request
    @Size(min = 5, message = "Password should be minimum 5 characters!")
    private String confirmPassword;
}
