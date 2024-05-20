package com.orchard.domain.order.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.orchard.domain.auth.domain.persist.MessageRepository;
import com.orchard.domain.member.domain.persist.Member;
import com.orchard.domain.member.domain.persist.MemberRepository;
import com.orchard.domain.member.domain.vo.UserEmail;
import com.orchard.domain.member.domain.vo.UserPhoneNumber;
import com.orchard.domain.ncp.core.NcpApiOption;
import com.orchard.domain.ncp.core.SmsResponse;
import com.orchard.domain.ncp.core.WebClientNcpSender;
import com.orchard.domain.order.domain.persist.Order;
import com.orchard.domain.order.domain.persist.OrderRepository;
import com.orchard.domain.order.dto.*;
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
    private final MessageRepository messageRepository;

    private final String clientId;
    private final String host;
    private final String serviceId;
    private final String from;
    private final WebClientNcpSender webClientNcpSender;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        MemberRepository memberRepository,
                        WebClientNcpSender webClientNcpSender,
                        MessageRepository messageRepository,
                        @Value("${ncp.from}") String from,
                        @Value("${ncp.serviceId}") String serviceId,
                        @Value("${nice.client-id}") String clientId,
                        @Value("${nice.return-host}") String host) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.memberRepository = memberRepository;
        this.webClientNcpSender = webClientNcpSender;
        this.messageRepository = messageRepository;
        this.serviceId = serviceId;
        this.from = from;
        this.clientId = clientId;
        this.host = host;
    }

    @Transactional(rollbackOn = Exception.class)
    public List<Order> delete(OrderRequestDto orderRequestDto) {
        List<Order> orders = orderRepository.findAllByOrderNoAndDeletedTimeIsNull(orderRequestDto.getOrderNo()).get();
        if (orders.size() > 0) {
            return delete(orderRequestDto.getOrderNo());
        }
        return null;
    }

    @Transactional(rollbackOn = Exception.class)
    public List<Order> delete(String orderNo) {
        return orderRepository.deleteAllByOrderNo(orderNo).get();
    }

    public List<Order> trans(TransRequestDto transRequestDto) {
        List<Order> orders = findOrder(transRequestDto.getOrderNo());
        for(Order order : orders) {
            if(order.getStatus().equals("cancelled")) {
                continue;
            }
            order.updateTransNo(transRequestDto.getTransNo(), transRequestDto.getTransCompany());
            orderRepository.save(order);
        }
        if(orders.size() > 0) {
            Order order = orders.get(0);
            String message = "[킹체리] " + order.getUserName().userName() + "님 상품 배송 요청 되셨습니다.\n\n["+order.getTransCompany()+"]\n운송장 번호 : " + order.getTransNo();
            sendMessage(message, order.getUserPhoneNumber());
        }
        return orders;
    }

    public List<Order> transComplete(TransRequestDto transRequestDto) {
        List<Order> orders = orderRepository.findAllByOrderNoAndTransNoAndTransCompanyAndDeletedTimeIsNull(transRequestDto.getOrderNo(), transRequestDto.getTransNo(), transRequestDto.getTransCompany()).get();
        for(Order order : orders) {
            if(!order.getStatus().equals("trans")) {
                continue;
            }
            order.transComplete();
            orderRepository.save(order);
        }
        if(orders.size() > 0) {
            Order order = orders.get(0);
            String message = "[킹체리] " + order.getUserName().userName() + "님 상품 배송 완료 처리 되셨습니다.";
            sendMessage(message, order.getUserPhoneNumber());
        }
        return orders;
    }

    public List<Order> findOrder(String orderId) {
        return orderRepository.findAllByOrderNoAndDeletedTimeIsNull(orderId).get();
    }

    public List<Order> findOrder(OrderRequestDto orderRequestDto) {
        return orderRepository.findAllByUserPhoneNumberAndOrderNoAndDeletedTimeIsNull(orderRequestDto.getPhoneNumber(), orderRequestDto.getOrderNo())
                .orElseThrow();
    }

    public List<OrderResponseDto> save(OrderRequestDto orderRequestDto) {

        String[] ids = orderRequestDto.getProductId().split(",");
        String[] counts = orderRequestDto.getCount().split(",");

        List<OrderResponseDto> results = new ArrayList<>();
        String orderNo = UUID.randomUUID().toString();
        for (int i = 0; i < ids.length; i++) {
            long id = Long.parseLong(ids[i]);
            int count = Integer.parseInt(counts[i]);
            Product product = productRepository.findById(id).get();

            Order.OrderBuilder orderBuilder = Order.builder()
                    .productIdx(id)
                    .count(count)
                    .orderNo(orderNo)
                    .status("WAIT");

            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            if (!"anonymousUser" .equals(email)) {
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
        List<Order> orderList = orderRepository.findAllByOrderNoAndDeletedTimeIsNull(orderId).get();
        final String tid = responseNode.get("tid").asText();
        List<OrderResponseDto> orderResponseDtos = new ArrayList<>();
        for (final Order order : orderList) {
            order.updateOrder(tid, status, receiptUrl, signature);

            Order result = orderRepository.save(order);
            OrderResponseDto orderResponseDto = new OrderResponseDto(result);
            orderResponseDto.setProduct(productRepository.findById(order.getProductIdx()).get());
            orderResponseDtos.add(orderResponseDto);
        }

        OrderResponseDto orderResponseDto = null;
        OrderCompleteDto orderCompleteDto;
        if (orderResponseDtos.size() > 0 && (orderResponseDto = orderResponseDtos.get(0)).getMemberIdx() == null) {
            String message =  "[킹체리] " + orderResponseDto.getUserName().userName() + "님 결제 완료 되셨습니다.\n주문 번호 : " + orderId;
            sendMessage(message, orderResponseDto.getUserPhoneNumber());
        }
        messageRepository.deleteByPhoneNumber(orderResponseDto.getUserPhoneNumber());
        orderCompleteDto = OrderCompleteDto.builder()
                .orderId(orderId)
                .orders(orderResponseDtos)
                .address(orderResponseDto.getUserAddress())
                .phoneNumber(orderResponseDto.getUserPhoneNumber())
                .receiptUrl(receiptUrl)
                .build();

        return orderCompleteDto;
    }

    @Transactional(rollbackOn = Exception.class)
    public OrderCompleteDto delete(JsonNode responseNode) {
        final String orderId = responseNode.get("orderId").asText();
        final String status = responseNode.get("status").asText();
        final String receiptUrl = responseNode.get("receiptUrl").asText();
        final String signature = responseNode.get("signature").asText();
        List<Order> orderList = orderRepository.findAllByOrderNoAndDeletedTimeIsNull(orderId).get();
        final String tid = responseNode.get("cancelledTid").asText();
        List<OrderResponseDto> orderResponseDtos = new ArrayList<>();
        for (final Order order : orderList) {
            order.updateOrder(tid, status, receiptUrl, signature);
            order.delete();
            Order result = orderRepository.save(order);
            OrderResponseDto orderResponseDto = new OrderResponseDto(result);
            orderResponseDto.setProduct(productRepository.findById(order.getProductIdx()).get());
            orderResponseDtos.add(orderResponseDto);
        }

        OrderResponseDto orderResponseDto = null;
        OrderCompleteDto orderCompleteDto;
        if (orderResponseDtos.size() > 0 && (orderResponseDto = orderResponseDtos.get(0)).getMemberIdx() == null) {
            String message = "[킹체리] " + orderResponseDto.getUserName().userName() + "님 결체 취소 되셨습니다.\n주문 번호 : " + orderId;
            sendMessage(message,  orderResponseDto.getUserPhoneNumber());
        }

        orderCompleteDto = OrderCompleteDto.builder()
                .orderId(orderId)
                .orders(orderResponseDtos)
                .address(orderResponseDto.getUserAddress())
                .phoneNumber(orderResponseDto.getUserPhoneNumber())
                .receiptUrl(receiptUrl)
                .build();

        return orderCompleteDto;
    }

    public List<List<Order>> findAdminOrder() {
        List<Order> orders = orderRepository.findAllByStatusInAndDeletedTimeIsNull(List.of("paid")).get();
        return makeTreeOrder(orders);
    }

    public List<List<Order>> findAdminTrans() {
        List<Order> orders = orderRepository.findAllByStatusInAndDeletedTimeIsNull(List.of("trans", "complete")).get();
        return makeTreeOrder(orders);
    }

    public List<List<Order>> findAdminCancel() {
        List<Order> orders = orderRepository.findAllByStatusAndDeletedTimeIsNotNull("cancelled").get();
        return makeTreeOrder(orders);
    }


    public List<List<Order>> findOrder(UserEmail email) {
        Long idx = memberRepository.findByEmail(email).get().getId();
        List<Order> orders = orderRepository.findAllByMemberIdxOrderByOrderNoAsc(idx).get();
        return makeTreeOrder(orders);
    }

    public List<List<Order>> findAnonOrder(OrderSearchRequestDto orderSearchRequestDto) {
        List<Order> orders = orderRepository.findAllByUserPhoneNumberAndOrderNoAndDeletedTimeIsNull(orderSearchRequestDto.getPhoneNumber(), orderSearchRequestDto.getOrderId()).get();
        return makeTreeOrder(orders);
    }

    private List<List<Order>> makeTreeOrder(List<Order> orders) {
        Map<String, List<Order>> orderMap = new HashMap<>();
        for (Order order : orders) {
            List<Order> temp = orderMap.get(order.getOrderNo());
            if (temp == null) {
                temp = new ArrayList<>(2);
            }
            temp.add(order);
            orderMap.put(order.getOrderNo(), temp);
        }
        return orderMap.values().stream().toList();
    }

    public void sendMessage(String message, UserPhoneNumber phoneNumber) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("type", "SMS");
        map.put("from", from);
        map.put("content",message);
        Map<String, String> messages = new HashMap<>();
        messages.put("to", phoneNumber.userPhoneNumber().replaceAll("-", ""));
        map.put("messages", List.of(messages));

        JSONObject jsonObject = new JSONObject(map);
        NcpApiOption ncpApiOption = NcpApiOption.builder()
                .method(HttpMethod.POST)
                .path("/sms/v2/services/" + serviceId + "/messages")
                .request(jsonObject)
                .build();

        webClientNcpSender.sendWithBlock(ncpApiOption, new ParameterizedTypeReference<SmsResponse>() {
        }).getBody();
    }
}
