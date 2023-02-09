package com.glolearn.newbook.repository;

import com.glolearn.newbook.domain.Authorization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorizationRepository extends JpaRepository<Authorization, String> {

    public void deleteById(String id);

    public Authorization findByTokenId(String tokenId);

}
