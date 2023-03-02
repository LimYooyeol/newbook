package com.glolearn.newbook.domain;
import com.glolearn.newbook.domain.Auth.OauthDomain;
import lombok.Getter;
import javax.persistence.*;

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

    protected Member(){}

    public static Member createMember(String oauthId, OauthDomain oauthDomain, String nickname){
        Member member = new Member();
        member.oauthId = oauthId;
        member.oauthDomain = oauthDomain;
        member.nickname = nickname;

        return member;
    }
}
