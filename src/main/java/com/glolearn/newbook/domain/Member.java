package com.glolearn.newbook.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Member {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "oauth_id")
    private String oAuthId;

    @Enumerated(EnumType.STRING)
    @Column(name = "oauth_domain")
    private OAuthDomain oAuthDomain;

    private String nickname;

    public Member() {}

    public Member(String oAuthId, OAuthDomain oAuthDomain, String nickname){
        this.oAuthId = oAuthId;
        this.oAuthDomain = oAuthDomain;
        this.nickname = nickname;
    }
}
