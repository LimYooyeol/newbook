package com.glolearn.newbook.domain;

import java.util.Locale;

public enum Category {
    Development("개발"),
    AI("AI");

    private final String value;

    private Category(String value){
        this.value = value;
    }

    public static Category of(String str) {
        for(Category category : Category.values()){
            if(category.toString().toLowerCase().equals(str)){
                return category;
            }
        }

        return null;
    }

    public String getValue(){
        return value;
    }
}
