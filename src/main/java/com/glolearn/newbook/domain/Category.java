package com.glolearn.newbook.domain;

public enum Category {
    Development("개발"),
    AI("AI");

    private final String value;

    private Category(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
