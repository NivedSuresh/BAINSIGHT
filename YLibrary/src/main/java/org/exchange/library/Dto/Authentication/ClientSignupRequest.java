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
    @NotBlank
    private String email;
    @Size(min = 10, max = 12)
    private String phoneNumber;
    private String username;
    @Size(min = 5)
    private String password;
    //Can be blank if it's an update request
    @Size(min = 5)
    private String confirmPassword;
}
