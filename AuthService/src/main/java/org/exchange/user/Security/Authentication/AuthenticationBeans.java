package org.exchange.user.Security.Authentication;

import org.exchange.user.Security.Authentication.Admin.AdminAuthenticationManager;
import org.exchange.user.Security.Authentication.Admin.AdminDetailsService;
import org.exchange.user.Security.Authentication.Client.ClientAuthenticationManager;
import org.exchange.user.Security.Authentication.Client.ClientDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AuthenticationBeans {


    @Bean(name = "CLIENT_AUTH_MANAGER")
    public ClientAuthenticationManager brokerAuthenticationManager(
            ClientDetailService brokerDetailService,
            PasswordEncoder encoder) {
        return new ClientAuthenticationManager(brokerDetailService, encoder);
    }

    @Bean(name = "ADMIN_AUTH_MANAGER")
    @Primary
    public AdminAuthenticationManager adminAuthenticationmanager(
            AdminDetailsService adminDetailsService,
            PasswordEncoder encoder) {
        return new AdminAuthenticationManager(adminDetailsService, encoder);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
