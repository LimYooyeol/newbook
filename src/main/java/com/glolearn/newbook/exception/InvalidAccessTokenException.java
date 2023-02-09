package com.glolearn.newbook.exception;

public class InvalidAccessTokenException extends Exception{
    String message;

    public InvalidAccessTokenException(String message){
        this.message = message;
    }

    @Override
    public String getMessage(){
        return this.message;
    }
}
