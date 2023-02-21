package com.glolearn.newbook.domain;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    @Test
    public void 테스트(){
        for(Category category : Category.values()){
            System.out.println(category.getValue());
        }
    }
}