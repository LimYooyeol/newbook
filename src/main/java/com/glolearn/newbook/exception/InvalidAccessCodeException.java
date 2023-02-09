package com.glolearn.newbook.exception;

public class InvalidAccessCodeException extends Exception{

    String message;
    public InvalidAccessCodeException(String message){
        this.message = message;
    }

    @Override
    public String getMessage(){
        return this.message;
    }
}
