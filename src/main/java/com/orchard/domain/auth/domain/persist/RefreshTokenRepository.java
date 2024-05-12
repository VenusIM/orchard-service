package com.orchard.domain.auth.domain.persist;

import com.orchard.domain.member.domain.vo.UserEmail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByEmail(UserEmail email);
}
