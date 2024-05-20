package com.orchard.domain.order.domain.persist;

import com.orchard.domain.member.domain.vo.UserPhoneNumber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<List<Order>> findAllByOrderNoAndDeletedTimeIsNull(String orderNo);
    Optional<List<Order>> findAllByUserPhoneNumberAndOrderNoAndDeletedTimeIsNull(UserPhoneNumber userPhoneNumber, String orderNo);
    Optional<List<Order>> deleteAllByOrderNo(String orderNo);
    Optional<List<Order>> findAllByMemberIdxOrderByOrderNoAsc(Long idx);
    Optional<List<Order>> findAllByOrderNoAndTransNoAndTransCompanyAndDeletedTimeIsNull(String orderNo, String transNo, String transCompany);
    Optional<List<Order>> findAllByStatusInAndDeletedTimeIsNull(List<String> status);
    Optional<List<Order>> findAllByStatusAndDeletedTimeIsNotNull(String status);


}
