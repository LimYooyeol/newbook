package com.glolearn.newbook.domain;

import com.glolearn.newbook.domain.Auth.OauthDomain;
import com.sun.istack.NotNull;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "MEMBERS")
@Getter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String oauthId;

    @Enumerated(EnumType.STRING)
    private OauthDomain oauthDomain;

    // UNIQUE
    private String nickname;
}
