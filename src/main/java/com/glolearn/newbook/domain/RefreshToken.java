package com.glolearn.newbook.domain;

import com.sun.istack.NotNull;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Getter
public class RefreshToken {
    @Id
    private String tokenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @NotNull
    private LocalDateTime expireDate;

    public static RefreshToken createRefreshToken(String tokenId, Member member, LocalDateTime expireDate){
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.tokenId = tokenId;
        refreshToken.member = member;
        refreshToken.expireDate = expireDate;

        return refreshToken;
    }

}
