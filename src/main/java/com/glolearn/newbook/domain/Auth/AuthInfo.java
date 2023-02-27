package com.glolearn.newbook.domain.Auth;

import com.glolearn.newbook.domain.Member;
import com.sun.istack.NotNull;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
public class AuthInfo {
    @Id
    private String tokenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @NotNull
    private LocalDateTime expireDate;

    public static AuthInfo createAuthInfo(String tokenId, Member member, LocalDateTime expireDate){
        AuthInfo authInfo = new AuthInfo();
        authInfo.tokenId = tokenId;
        authInfo.member = member;
        authInfo.expireDate = expireDate;

        return authInfo;
    }

}
