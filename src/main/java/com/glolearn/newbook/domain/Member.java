package com.glolearn.newbook.domain;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "oauth_id")
    @NotNull
    private String oAuthId;

    @Enumerated(EnumType.STRING)
    @Column(name = "oauth_domain")
    @NotNull
    private OAuthDomain oAuthDomain;

    @NotBlank
    @Size(min = 2, max = 50)
    private String nickname;    // unique

    @OneToMany(mappedBy = "lecturer", fetch = FetchType.LAZY)
    private List<Course> teaches = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<Enrollment> enrollments = new ArrayList<>();

    public static Member createMember(String oAuthId, OAuthDomain oAuthDomain, String nickname){
        Member member = new Member();
        member.oAuthId = oAuthId;
        member.oAuthDomain = oAuthDomain;
        member.nickname = nickname;

        return member;
    }
}
