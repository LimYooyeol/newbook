package com.glolearn.newbook.domain;

import com.glolearn.newbook.domain.Auth.OauthDomain;
import com.sun.istack.NotNull;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "MEMBERS", uniqueConstraints = {@UniqueConstraint(    // @UniqueConstraint -> ddl-auto: create 인 경우에만 유효
        columnNames = {"nickname"}
)})
@Getter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @NotNull
    private String oauthId;

    @Enumerated(EnumType.STRING)
    @NotNull
    private OauthDomain oauthDomain;

    @NotBlank
    @Size(min = 2, max = 50)
    private String nickname;    // unique

    public static Member createMember(String oAuthId, OauthDomain oAuthDomain, String nickname){
        Member member = new Member();
        member.oauthId = oAuthId;
        member.oauthDomain = oAuthDomain;
        member.nickname = nickname;

        return member;
    }
}
