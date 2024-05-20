package com.orchard.domain.member.domain.persist;

import com.orchard.domain.member.domain.vo.UserPhoneNumber;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.orchard.domain.member.domain.vo.UserEmail;
import com.orchard.domain.member.domain.vo.UserName;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(final UserEmail email);
    boolean existsByEmail(final UserEmail email);
    void deleteByEmail(final UserEmail email);
    Page<Member> findAll(final Pageable pageable);
    List<Member> findByName(final UserName name, final Pageable pageable);

    Optional<List<Member>> findAllByPhoneNumber(final UserPhoneNumber phoneNumber);

}
