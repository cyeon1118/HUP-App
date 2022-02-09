package com.example.auctionapp.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OAuth2NaverLoginRequest {

    private String accessToken;
    private String targetToken;
}
