package com.fptgamebookingbe.repository.httpclient;

import com.fptgamebookingbe.dto.ExchangeToken.ExchangeTokenRequestDTO;
import com.fptgamebookingbe.dto.ExchangeToken.ExchangeTokenResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import feign.QueryMap;

@FeignClient(name = "outbound-identity", url = "https://oauth2.googleapis.com")
public interface OutboundIdentityClient {

    /**
     * Exchanges an authorization code for an access token.
     *
     * @param request the request containing the authorization code and other parameters
     * @return the response containing the access token and other information
     */
    @PostMapping(value = "/token", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ExchangeTokenResponseDTO exchangeToken(@QueryMap ExchangeTokenRequestDTO request);
}