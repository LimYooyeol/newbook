package com.glolearn.newbook.dto.course;

public enum Sort {
    RECENT("최신순"),
    POPULAR("인기순");

    private final String value;

    private Sort(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
    public static Sort of(String str) {
        for(Sort sort : Sort.values()){
            if(sort.toString().toLowerCase().equals(str)){
                return sort;
            }
        }

        return null;
    }
}
