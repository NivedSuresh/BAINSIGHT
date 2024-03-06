package org.exchange.user.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.exchange.library.Enums.MfaType;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("admin")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Admin {
    @Id
    private Long id;
    private String email;
    private String password;
    private String authority;
    @Column("is_banned")
    private Boolean is_banned;
    private String otp;
    @Column("mfa_type")
    private MfaType mfa_type;
}
