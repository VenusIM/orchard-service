package com.orchard.domain.order.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.orchard.domain.member.domain.persist.Member;
import com.orchard.domain.member.domain.persist.MemberRepository;
import com.orchard.domain.member.domain.vo.UserEmail;
import com.orchard.domain.order.domain.persist.Order;
import com.orchard.domain.order.domain.persist.OrderRepository;
import com.orchard.domain.order.dto.OrderRequestDto;
import com.orchard.domain.order.dto.OrderResponseDto;
import com.orchard.domain.product.domain.pesist.Product;
import com.orchard.domain.product.domain.pesist.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final String clientId;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        MemberRepository memberRepository,
                        @Value("${nice.client-id}") String clientId) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.memberRepository = memberRepository;
        this.clientId = clientId;
    }

    public OrderResponseDto save(OrderRequestDto orderRequestDto) {
        Product product = productRepository.findById(orderRequestDto.getProductId()).get();

        Order.OrderBuilder orderBuilder = Order.builder()
                .productIdx(orderRequestDto.getProductId())
                .count(orderRequestDto.getCount())
                .orderNo(UUID.randomUUID().toString())
                .status("WAIT");

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!"anonymousUser".equals(email)) {
            Member member = memberRepository.findByEmail(UserEmail.from(email)).get();
            orderBuilder.memberIdx(member.getId());
            orderBuilder.userName(member.getName());
            orderBuilder.userPhoneNumber(member.getPhoneNumber());
        } else {
            orderBuilder.userPhoneNumber(orderRequestDto.getPhoneNumber());
            orderBuilder.userName(orderRequestDto.getUserName());
        }
        Order order = orderRepository.save(orderBuilder.build());
        OrderResponseDto orderResponseDto = new OrderResponseDto(order);
        orderResponseDto.setProduct(product);
        orderResponseDto.setClientId(clientId);
        return orderResponseDto;
    }

    @Transactional(rollbackOn = Exception.class)
    public List<OrderResponseDto> update(JsonNode responseNode) {
        final String orderId = responseNode.get("orderId").asText();
        List<Order> orderList = orderRepository.findAllByOrderNo(orderId).get();
        final String tid = responseNode.get("tid").asText();
        List<OrderResponseDto> orderResponseDtos = new ArrayList<>();
        for(final Order order : orderList) {
            order.updateOrder(tid, "COMPLETE");
            Order result = orderRepository.save(order);
            orderResponseDtos.add(new OrderResponseDto(result));
        }
        return orderResponseDtos;
    }
}
