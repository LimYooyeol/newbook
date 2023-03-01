package com.glolearn.newbook.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Introduction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "introduction_id")
    private Long id;

    private String introduction;
}
