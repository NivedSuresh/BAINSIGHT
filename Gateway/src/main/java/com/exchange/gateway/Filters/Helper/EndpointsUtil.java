package com.exchange.gateway.Filters.Helper;

import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class EndpointsUtil {
    private final List<String> open;

    private final List<String> client;

    private final List<String> admin;

    public EndpointsUtil() {
        this.open = initializeOpenEndpoints();
        this.client = initializeClientEndpoints();
        this.admin = initializeAdminEndpoints();
    }


    public boolean isAllowed(String endpoint, String role){
        if(role.equals("CLIENT") && iterate(client, endpoint)) return true;

        return role.equals("ADMIN") && iterate(admin, endpoint);
    }

    public boolean checkIfOpenEndpoint(String endpoint){
        return iterate(open, endpoint);
    }

    private boolean iterate(List<String> endpoints, String to){
        for(String endpoint : endpoints){
            System.out.println(to + " starts with " + endpoint + " = " + to.startsWith(endpoint));
            if(to.startsWith(endpoint)) return true;
        }
        return false;
    }


    private List<String> initializeAdminEndpoints() {
        return List.of(
                "/api/bainsight/admin/",
                "/api/bainsight/order"
        );
    }

    private List<String> initializeClientEndpoints() {
        return List.of(
                "/api/bainsight/order",
                "/api/bainsight/watchlist",
                "/api/bainsight/history"
        );
    }

    private List<String> initializeOpenEndpoints() {
        return List.of(
                "/ws",
                "/api/bainsight/auth/"
        );
    }

}
