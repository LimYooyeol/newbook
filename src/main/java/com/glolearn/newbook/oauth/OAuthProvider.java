package com.glolearn.newbook.oauth;

public interface OAuthProvider {

    public String getAccessToken(String accessCode) throws Exception;

    public String getOAuthId(String accessToken) throws Exception;

    public void expireAccess(String accessToken);

}
