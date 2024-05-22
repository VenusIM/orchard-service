package com.orchard.domain.order.domain.persist;

import com.orchard.domain.member.domain.vo.UserPhoneNumber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<List<Order>> findAllByOrderNoAndDeletedTimeIsNull(String orderNo);
    Optional<List<Order>> findAllByUserPhoneNumberAndOrderNoAndDeletedTimeIsNull(UserPhoneNumber userPhoneNumber, String orderNo);
    Optional<List<Order>> deleteAllByOrderNo(String orderNo);
    Optional<List<Order>> findAllByMemberIdxOrderByIdAsc(Long idx);
    Optional<List<Order>> findAllByOrderNoAndTransNoAndTransCompanyAndDeletedTimeIsNullOrderByIdAsc(String orderNo, String transNo, String transCompany);
    Optional<List<Order>> findAllByStatusInAndDeletedTimeIsNullOrderByIdAsc(List<String> status);
    Optional<List<Order>> findAllByStatusAndDeletedTimeIsNotNullOrderByIdAsc(String status);


}
