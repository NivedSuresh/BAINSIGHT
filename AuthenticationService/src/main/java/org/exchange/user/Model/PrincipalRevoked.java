package org.exchange.user.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.io.Serial;
import java.io.Serializable;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrincipalRevoked implements Serializable {
    @Serial
    private static final long serialVersionUID = -7508341890025703846L;
    @Id
    private String username;
    private Boolean revoked;
}
