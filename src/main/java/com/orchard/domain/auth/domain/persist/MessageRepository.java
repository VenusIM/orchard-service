package com.orchard.domain.auth.domain.persist;

import com.orchard.domain.member.domain.vo.UserPhoneNumber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    Optional<Message> findByPhoneNumber(UserPhoneNumber phoneNumber);
    Optional<Message> deleteByPhoneNumber(UserPhoneNumber phoneNumber);

}
