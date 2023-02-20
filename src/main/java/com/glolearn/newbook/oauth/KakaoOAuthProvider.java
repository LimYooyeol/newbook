package com.glolearn.newbook.oauth;

import com.glolearn.newbook.exception.InvalidAccessCodeException;
import com.glolearn.newbook.exception.InvalidAccessTokenException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class KakaoOAuthProvider implements OAuthProvider {

    @Override
    public String getAccessToken(String code) throws ParseException, InvalidAccessCodeException {
        String accessTokenResponse = WebClient.create("https://kauth.kakao.com")
                .post()
                .uri(uriBuilder -> uriBuilder.path("/oauth/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", "3dcf523718ad3e80be2e1d8d2e898520")
                        .queryParam("redirect_uri", "http://localhost:8080/oauth/kakao/token") // 보안상 필요
                        .queryParam("code", code)
                        .build()
                ).retrieve().bodyToMono(String.class).block();

        JSONParser jsonParser = new JSONParser();
        org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) jsonParser.parse(accessTokenResponse);

        if (jsonObject.get("code") != null) {
            throw new InvalidAccessCodeException("[Kakao] Invalid access code");
        }

        String accessToken = jsonObject.get("access_token").toString();

        return accessToken;
    }

    @Override
    public String getOAuthId(String accessToken) throws Exception {
        String userIdResponse = WebClient.create("https://kapi.kakao.com")
                .post()
                .uri(uriBuilder -> uriBuilder.path("/v2/user/me")
                        .build()
                ).headers(h -> h.setBearerAuth(accessToken))
                .retrieve().bodyToMono(String.class).block();

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (org.json.simple.JSONObject) jsonParser.parse(userIdResponse);

        if(jsonObject.get("code") != null){
            throw new InvalidAccessTokenException("[Kakao] Invalid access token");
        }

        Long userId = (Long)jsonObject.get("id");

        return userId.toString();
    }

    @Override
    public void expireAccess(String accessToken) {
        WebClient.create("https://kapi.kakao.com")
                .post()
                .uri(uriBuilder -> uriBuilder.path("/v1/user/logout")
                        .build()
                ).headers(h -> h.setBearerAuth(accessToken))
                .retrieve().bodyToMono(String.class).block();
    }


}
