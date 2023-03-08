package com.glolearn.newbook.exception;

public class InvalidAccessException extends RuntimeException{
    private String message;


    public InvalidAccessException(String message){
        this.message = message;
    }

    @Override
    public String getMessage(){
        return message;
    }
}
