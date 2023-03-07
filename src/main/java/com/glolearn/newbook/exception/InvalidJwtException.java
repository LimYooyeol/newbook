package com.glolearn.newbook.exception;

public class InvalidJwtException extends RuntimeException{
    private String message;

    public InvalidJwtException(String message){
        this.message = message;
    }

    @Override
    public String getMessage(){
        return this.message;
    }
}
