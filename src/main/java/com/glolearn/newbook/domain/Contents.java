package com.glolearn.newbook.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Contents {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contents_id")
    private Long id;

    private String contents;

    protected Contents(){}

    public Contents(String contents){
        this.contents = contents;
    }

    public void updateContents(String contents){
        this.contents = contents;
    }
}
