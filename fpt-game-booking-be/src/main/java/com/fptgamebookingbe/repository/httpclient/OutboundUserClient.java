package com.fptgamebookingbe.repository.httpclient;

import com.fptgamebookingbe.dto.OutboundUserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "outbound-user-client", url = "https://www.googleapis.com")
public interface OutboundUserClient {

    /**
     * Retrieves user information from the OAuth2 userinfo endpoint.
     *
     * @param alt the format of the response (e.g., json)
     * @param accessToken the access token used for authentication
     * @return the user information as an OutboundUserDTO
     */
    @GetMapping(value = "/oauth2/v1/userinfo")
    OutboundUserDTO getUserInfo(@RequestParam("alt") String alt,
                                @RequestParam("access_token") String accessToken);
}