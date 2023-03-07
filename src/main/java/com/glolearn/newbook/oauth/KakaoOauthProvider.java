package com.glolearn.newbook.oauth;

import com.glolearn.newbook.oauth.exception.AccessTokenIssueRejectedException;
import com.glolearn.newbook.oauth.exception.InvalidAccessTokenException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class KakaoOauthProvider implements OauthProvider {

    /*
        #shorts:
            Resource server 로 접근을 위한 access token 획득
        #params:
            code : resource owner 가 resource server 로부터 받아온 access code
        #return:
            access token ( ours -> resource server )
     */
    @Override
    public String getAccessToken(String code) {

        // REST API
        String accessTokenResponse;
        try {
            accessTokenResponse = WebClient.create("https://kauth.kakao.com")
                .post()
                .uri(uriBuilder -> uriBuilder.path("/oauth/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", "3dcf523718ad3e80be2e1d8d2e898520")
                        .queryParam("redirect_uri", "http://localhost:8080/oauth/kakao/token") // 보안상 필요
                        .queryParam("code", code)
                        .build()
                ).retrieve().bodyToMono(String.class).block();
        }catch (Exception e){
            throw new AccessTokenIssueRejectedException("[Kakao] Issuing access token rejected");
        }

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;
        try{
            jsonObject = (JSONObject) jsonParser.parse(accessTokenResponse);
        }catch (ParseException e){
            throw new IllegalStateException("Kakao response always meet JSON format.");
        }

        // access token 반환
        String accessToken = jsonObject.get("access_token").toString();
        return accessToken;
    }

    /*
        #shorts:
            resource owner 의 oauth 회원 ID 조회
        #params:
            accessToken ( ours -> resource server)
        #return :
            resource owner 의 oauth 회원 ID
     */
    @Override
    public String getOAuthId(String accessToken) {

        // REST API
        String userIdResponse;
        try {
            userIdResponse = WebClient.create("https://kapi.kakao.com")
                .post()
                .uri(uriBuilder -> uriBuilder.path("/v2/user/me")
                        .build()
                ).headers(h -> h.setBearerAuth(accessToken))
                .retrieve().bodyToMono(String.class).block();
        }catch (Exception e) {
            throw new InvalidAccessTokenException("[Kakao] Invalid access");
        }

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;
        try{
            jsonObject = (JSONObject) jsonParser.parse(userIdResponse);
        }catch (ParseException e){
            throw new IllegalStateException("Kakao response always meet JSON format.");
        }

        Long userId = (Long)jsonObject.get("id");
        return userId.toString();
    }

    /*
        #shorts:
            resource server 에 대한 access token 만료
        #params :
            accessToken (ours -> resource server)
     */
    @Override
    public void expireAccess(String accessToken) {
        try {
            WebClient.create("https://kapi.kakao.com")
                    .post()
                    .uri(uriBuilder -> uriBuilder.path("/v1/user/logout")
                            .build()
                    ).headers(h -> h.setBearerAuth(accessToken))
                    .retrieve().bodyToMono(String.class).block();
        }catch (Exception e){
            return;
        }
    }


}
