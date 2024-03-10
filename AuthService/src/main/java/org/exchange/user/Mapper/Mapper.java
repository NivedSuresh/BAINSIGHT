package org.exchange.user.Mapper;


import lombok.RequiredArgsConstructor;
import org.exchange.library.Dto.Authentication.JwtResponse;
import org.exchange.library.Dto.Authentication.ClientSignupRequest;
import org.exchange.library.Dto.Authentication.ClientAuthResponse;
import org.exchange.user.Model.Client;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Mapper {


    public Client requestToClient(ClientSignupRequest request) {
        Client client = new Client();
        BeanUtils.copyProperties(request, client);
        return client;
    }


    public ClientAuthResponse getClientDto(Client client, JwtResponse t2) {
        ClientAuthResponse clientAuthResponse = new ClientAuthResponse();
        BeanUtils.copyProperties(client, clientAuthResponse);
        clientAuthResponse.setAuthResponse(t2);
        return clientAuthResponse;
    }


}
