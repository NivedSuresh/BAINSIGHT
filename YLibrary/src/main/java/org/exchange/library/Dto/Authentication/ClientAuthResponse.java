package org.exchange.library.Dto.Authentication;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClientAuthResponse {
    private String ucc;
    private String username;
    private String email;
    private String phoneNumber;
}
