package org.exchange.library.Dto.Client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ClientDto {
    private UUID ucc;
    private String username;
    private String email;
    private String phoneNumber;
}
