package com.pappgroup.pappapp.repository;

import com.pappgroup.pappapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByResetToken(String resetToken);

    @Query("SELECT u FROM User u WHERE u.email = :email OR u.oauthId = :oauthId")
    Optional<User> findByEmailOrOauthId(@Param("email") String email, @Param("oauthId") String oauthId);

    Optional<User> findByOauthProviderAndOauthId(String oauthProvider, String oauthId);
}