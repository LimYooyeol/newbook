package com.glolearn.newbook.oauth.exception;

public class AccessTokenIssueRejectedException extends RuntimeException {
    private String message;

    public AccessTokenIssueRejectedException(String message){
        this.message = message;
    }

    @Override
    public String getMessage(){
        return this.message;
    }
}
