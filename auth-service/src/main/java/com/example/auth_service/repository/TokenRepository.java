package com.example.auth_service.repository;

import com.example.auth_service.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {

    @Query(
            """
                    select t from Token t inner  join  User u on t.user.id = u.id where u.id = :userId
                    """
    )
    List<Token> findAllTokenByUser(String userId);

    Optional<Token> findByAccessToken(String accessToken);

    Optional<Token> findByRefreshToken(String refreshToken);
}