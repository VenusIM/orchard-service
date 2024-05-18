package com.orchard.domain.order.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.orchard.domain.member.domain.persist.Member;
import com.orchard.domain.member.domain.persist.MemberRepository;
import com.orchard.domain.member.domain.vo.UserEmail;
import com.orchard.domain.ncp.core.NcpApiOption;
import com.orchard.domain.ncp.core.SmsResponse;
import com.orchard.domain.ncp.core.WebClientNcpSender;
import com.orchard.domain.order.domain.persist.Order;
import com.orchard.domain.order.domain.persist.OrderRepository;
import com.orchard.domain.order.dto.OrderCompleteDto;
import com.orchard.domain.order.dto.OrderRequestDto;
import com.orchard.domain.order.dto.OrderResponseDto;
import com.orchard.domain.product.domain.pesist.Product;
import com.orchard.domain.product.domain.pesist.ProductRepository;
import jakarta.transaction.Transactional;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    private final String clientId;
    private final String host;
    private final String serviceId;
    private final String from;
    private final WebClientNcpSender webClientNcpSender;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        MemberRepository memberRepository,
                        WebClientNcpSender webClientNcpSender,
                        @Value("${ncp.from}") String from,
                        @Value("${ncp.serviceId}") String serviceId,
                        @Value("${nice.client-id}") String clientId,
                        @Value("${nice.return-host}") String host) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.memberRepository = memberRepository;
        this.webClientNcpSender = webClientNcpSender;
        this.serviceId = serviceId;
        this.from = from;
        this.clientId = clientId;
        this.host = host;
    }

    public List<OrderResponseDto> save(OrderRequestDto orderRequestDto) {

        String[] ids = orderRequestDto.getProductId().split(",");
        String[] counts = orderRequestDto.getCount().split(",");

        List<OrderResponseDto> results = new ArrayList<>();
        String orderNo = UUID.randomUUID().toString();
        for(int i = 0; i < ids.length; i++) {
            long id = Long.parseLong(ids[i]);
            int count = Integer.parseInt(counts[i]);
            Product product = productRepository.findById(id).get();

            Order.OrderBuilder orderBuilder = Order.builder()
                    .productIdx(id)
                    .count(count)
                    .orderNo(orderNo)
                    .status("WAIT");

            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            if(!"anonymousUser".equals(email)) {
                Member member = memberRepository.findByEmail(UserEmail.from(email)).get();
                orderBuilder.memberIdx(member.getId());
                orderBuilder.userName(member.getName());
                orderBuilder.userPhoneNumber(member.getPhoneNumber());
                orderBuilder.userAddress(member.getAddress());
            } else {
                orderBuilder.userPhoneNumber(orderRequestDto.getPhoneNumber());
                orderBuilder.userName(orderRequestDto.getUserName());
                orderBuilder.userAddress(orderRequestDto.getUserAddress());
            }
            Order order = orderRepository.save(orderBuilder.build());
            OrderResponseDto orderResponseDto = new OrderResponseDto(order);
            orderResponseDto.setProduct(product);
            orderResponseDto.setClientId(clientId);
            orderResponseDto.setHost(host);
            results.add(orderResponseDto);
        }

        return results;
    }

    @Transactional(rollbackOn = Exception.class)
    public OrderCompleteDto update(JsonNode responseNode) {
        final String orderId = responseNode.get("orderId").asText();
        final String status = responseNode.get("status").asText();
        final String receiptUrl = responseNode.get("receiptUrl").asText();
        final String signature = responseNode.get("signature").asText();
        List<Order> orderList = orderRepository.findAllByOrderNo(orderId).get();
        final String tid = responseNode.get("tid").asText();
        List<OrderResponseDto> orderResponseDtos = new ArrayList<>();
        for(final Order order : orderList) {
            order.updateOrder(tid, status, receiptUrl, signature);
            Order result = orderRepository.save(order);
            orderResponseDtos.add(new OrderResponseDto(result));
        }

        OrderResponseDto orderResponseDto;
        OrderCompleteDto orderCompleteDto = null;
        if(orderResponseDtos.size() > 0 && (orderResponseDto = orderResponseDtos.get(0)).getMemberIdx() > 0L) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "SMS");
            jsonObject.put("from", from);
            jsonObject.put("content",
                    orderResponseDto.getUserName().userName()+"님 결제 완료 되셨습니다.\n비회원 조회시 가입시 입력하신 전화 번호와 주문 번호로 조회 가능합니다.\n주문 번호 : "+orderResponseDto.getOrderNo()
            );
            Map<String, String> messages = new HashMap<>();
            messages.put("to",orderResponseDto.getUserPhoneNumber().userPhoneNumber().replaceAll("-", ""));
            jsonObject.put("messages", List.of(messages));

            NcpApiOption ncpApiOption = NcpApiOption.builder()
                    .method(HttpMethod.POST)
                    .path("/sms/v2/services/"+serviceId+"/messages")
                    .request(jsonObject)
                    .build();

            webClientNcpSender.sendWithBlock(ncpApiOption, new ParameterizedTypeReference<SmsResponse>() {})
                    .getBody();

            orderCompleteDto = OrderCompleteDto.builder()
                    .orders(orderResponseDtos)
                    .address(orderResponseDto.getUserAddress())
                    .phoneNumber(orderResponseDto.getUserPhoneNumber())
                    .receiptUrl(receiptUrl)
                    .build();
        }
        return orderCompleteDto;
    }
}
