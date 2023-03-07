package com.glolearn.newbook.oauth.exception;

public class InvalidAccessTokenException extends RuntimeException{
    private String message;

    public InvalidAccessTokenException(String message){
        this.message = message;
    }

    @Override
    public String getMessage(){
        return this.message;
    }
}
