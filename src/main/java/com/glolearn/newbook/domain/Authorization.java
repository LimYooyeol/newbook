package com.glolearn.newbook.domain;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
public class Authorization {

    @Id
    private String tokenId;


    private Long memberId;

    public Authorization(){}
    public Authorization(String tokenId, Long memberId) {
        this.tokenId = tokenId;
        this.memberId = memberId;
    }
}
