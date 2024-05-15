package com.orchard.domain.payment;

import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.orchard.domain.order.application.OrderService;
import com.orchard.domain.order.dto.OrderResponseDto;
import com.orchard.domain.product.application.ProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

@Controller("/payment")
public class PaymentController {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String CLIENT_ID;
    private final String SECRET_KEY;

    private final OrderService orderService;
    public PaymentController(@Value("${nice.client-id}") String clientId,
                             @Value("${nice.secret-key}") String secretKey,
                             OrderService orderService) {
        this.CLIENT_ID = clientId;
        this.SECRET_KEY = secretKey;
        this.orderService = orderService;
    }

    @RequestMapping("/serverAuth")
    public String requestPayment(
            @RequestParam String tid,
            @RequestParam Long amount,
            Model model) throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((CLIENT_ID + ":" + SECRET_KEY).getBytes()));
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> AuthenticationMap = new HashMap<>();
        AuthenticationMap.put("amount", String.valueOf(amount));

        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(AuthenticationMap), headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(
                "https://sandbox-api.nicepay.co.kr/v1/payments/" + tid, request, JsonNode.class);

        JsonNode responseNode = responseEntity.getBody();
        String resultCode = responseNode.get("resultCode").asText();
        model.addAttribute("resultMsg", responseNode.get("resultMsg").asText());

        if (resultCode.equalsIgnoreCase("0000")) {
            // 결제 성공 비즈니스 로직 구현
            List<OrderResponseDto> orderResponseDto = orderService.update(responseNode);
        } else {
            // 결제 실패 비즈니스 로직 구현
        }

        return "/response";
    }

    @RequestMapping("/cancelAuth")
    public String requestCancel(
            @RequestParam String tid,
            @RequestParam String amount,
            Model model) throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((CLIENT_ID + ":" + SECRET_KEY).getBytes()));
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> AuthenticationMap = new HashMap<>();
        AuthenticationMap.put("amount", amount);
        AuthenticationMap.put("reason", "test");
        AuthenticationMap.put("orderId", UUID.randomUUID().toString());

        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(AuthenticationMap), headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(
                "https://sandbox-api.nicepay.co.kr/v1/payments/" + tid + "/cancel", request, JsonNode.class);

        JsonNode responseNode = responseEntity.getBody();
        String resultCode = responseNode.get("resultCode").asText();
        model.addAttribute("resultMsg", responseNode.get("resultMsg").asText());

        System.out.println(responseNode.toPrettyString());

        if (resultCode.equalsIgnoreCase("0000")) {
            // 취소 성공 비즈니스 로직 구현
        } else {
            // 취소 실패 비즈니스 로직 구현
        }

        return "/response";
    }

    @RequestMapping("/hook")
    public ResponseEntity<String> hook(@RequestBody HashMap<String, Object> hookMap) throws Exception {
        String resultCode = hookMap.get("resultCode").toString();

        System.out.println(hookMap);

        if (resultCode.equalsIgnoreCase("0000")) {
            return ResponseEntity.status(HttpStatus.OK).body("ok");
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
