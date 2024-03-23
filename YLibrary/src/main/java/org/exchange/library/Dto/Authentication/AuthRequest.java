package org.exchange.library.Dto.Authentication;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthRequest {
    @Pattern(regexp = "^\\S+$", message = "Please enter valid user information")
    private String identifier;
    @Size(min = 5, max = 20, message = "Password should be of length 5 to 20 characters")
    private String password;
}
