package com.glolearn.newbook.repository;

import com.glolearn.newbook.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorizationRepository extends JpaRepository<RefreshToken, String> {

    public void deleteById(String id);

    public RefreshToken findByTokenId(String tokenId);

}
