package org.exchange.library.Dto.Authentication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminAuthResponse {
    private String email;
    private JwtResponse jwtResponse;
}
