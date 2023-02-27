package com.glolearn.newbook.oauth;

public interface OauthProvider {

    public String getAccessToken(String accessCode) throws Exception;

    public String getOAuthId(String accessToken) throws Exception;

    public void expireAccess(String accessToken);

}
