package com.orchard.domain.payment;

import java.time.Duration;
import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.orchard.domain.ncp.NcpService;
import com.orchard.domain.order.application.OrderService;
import com.orchard.domain.order.dto.OrderResponseDto;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Controller("/payment")
public class PaymentController {

    private WebClient client;
    private final String CLIENT_ID;
    private final String SECRET_KEY;
    private final OrderService orderService;
    private final NcpService ncpService;

    public PaymentController(@Value("${nice.client-id}") String clientId,
                             @Value("${nice.secret-key}") String secretKey,
                             OrderService orderService,
                             NcpService ncpService) {
        this.CLIENT_ID = clientId;
        this.SECRET_KEY = secretKey;
        this.orderService = orderService;
        this.ncpService = ncpService;
    }

    @PostConstruct
    private void init() throws Exception {

        ConnectionProvider provider = ConnectionProvider.builder("custom-provider")
                .maxConnections(20)
                .maxIdleTime(Duration.ofSeconds(58))
                .maxLifeTime(Duration.ofSeconds(58))
                .pendingAcquireTimeout(Duration.ofSeconds(60))
                .pendingAcquireMaxCount(-1)
                .evictInBackground(Duration.ofSeconds(30))
                .lifo()
                .metrics(true)
                .build();

        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();

        client = WebClient.builder()
                .clientConnector(
                        new ReactorClientHttpConnector(
                                HttpClient.create(provider)
                                        .secure(t -> t.sslContext(sslContext))
                                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 20000) //miliseconds
                                        .doOnConnected(
                                                conn -> conn.addHandlerLast(new ReadTimeoutHandler(30))  //sec
                                                        .addHandlerLast(new WriteTimeoutHandler(60)) //sec
                                        ).responseTimeout(Duration.ofSeconds(60))
                        )
                )
                .baseUrl("https://sandbox-api.nicepay.co.kr")
                .build();
    }

    @RequestMapping("/serverAuth")
    public String requestPayment(
            @RequestParam String tid,
            @RequestParam Long amount,
            Model model) throws Exception {

        String accessToken = getAccessToken();

        //승인 요청
        Map<String, Object> authenticationMap = new HashMap<>();
        authenticationMap.put("amount", String.valueOf(amount));

        JsonNode responseNode = client.post()
                .uri("/v1/payments/"+tid)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(authenticationMap)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        String resultCode = responseNode.get("resultCode").asText();
        model.addAttribute("resultMsg", responseNode.get("resultMsg").asText());

        if (resultCode.equalsIgnoreCase("0000")) {
            // 결제 성공 비즈니스 로직 구현
            model.addAttribute("orders", orderService.update(responseNode));
            return "/payment/complete";
        }
        return "/payment/error";
    }

    @RequestMapping("/cancelAuth")
    public String requestCancel(
            @RequestParam String tid,
            @RequestParam String amount,
            Model model) throws Exception {

        Map<String, Object> AuthenticationMap = new HashMap<>();
        AuthenticationMap.put("amount", amount);
        AuthenticationMap.put("reason", "test");
        AuthenticationMap.put("orderId", UUID.randomUUID().toString());

//        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(AuthenticationMap), headers);

        /*JsonNode responseNode = webClient.post()
                .uri(tid+"/cancel")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((CLIENT_ID + ":" + SECRET_KEY).getBytes()))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(AuthenticationMap)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        String resultCode = responseNode.get("resultCode").asText();
        model.addAttribute("resultMsg", responseNode.get("resultMsg").asText());

        System.out.println(responseNode.toPrettyString());

        if (resultCode.equalsIgnoreCase("0000")) {
            // 취소 성공 비즈니스 로직 구현
        } else {
            // 취소 실패 비즈니스 로직 구현
        }*/

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

    private String getAccessToken() {

        JsonNode accessToken = client.post()
                .uri("/v1/access-token")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((CLIENT_ID + ":" + SECRET_KEY).getBytes()))
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        return accessToken.get("accessToken").asText();
    }
}
