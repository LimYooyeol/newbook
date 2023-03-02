package com.glolearn.newbook.repository;

import com.glolearn.newbook.domain.Auth.AuthInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthInfoRepository extends JpaRepository<AuthInfo, String> {
    public AuthInfo findByTokenId(String tokenId);

}
