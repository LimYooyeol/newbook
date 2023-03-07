package com.glolearn.newbook.oauth;

public interface OauthProvider {

    /*
        #shorts:
            Resource server 로 접근을 위한 access token 획득
        #params:
            code : resource owner 가 resource server 로부터 받아온 access code
        #return:
            access token ( ours -> resource server )
     */
    public String getAccessToken(String accessCode);


    /*
        #shorts:
            resource owner 의 oauth 회원 ID 조회
        #params:
            accessToken ( ours -> resource server)
        #return :
            resource owner 의 oauth 회원 ID
     */
    public String getOAuthId(String accessToken);

    /*
        #shorts:
            resource server 에 대한 access token 만료
        #params :
            accessToken (ours -> resource server)
     */
    public void expireAccess(String accessToken);

}
