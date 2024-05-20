package com.orchard.domain.payment;

import java.time.Duration;
import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.orchard.domain.ncp.NcpService;
import com.orchard.domain.order.application.OrderService;
import com.orchard.domain.order.domain.persist.Order;
import com.orchard.domain.order.dto.OrderCompleteDto;
import com.orchard.domain.order.dto.OrderResponseDto;
import com.orchard.domain.product.application.ProductService;
import com.orchard.domain.product.domain.pesist.Product;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Controller
public class PaymentController {

    private WebClient client;
    private final String CLIENT_ID;
    private final String SECRET_KEY;
    private final OrderService orderService;
    private final ProductService productService;
    private final NcpService ncpService;
    private final String niceApi;

    public PaymentController(@Value("${nice.client-id}") String clientId,
                             @Value("${nice.secret-key}") String secretKey,
                             @Value("${nice.api}") String niceApi,
                             OrderService orderService,
                             ProductService productService,
                             NcpService ncpService) {
        this.CLIENT_ID = clientId;
        this.SECRET_KEY = secretKey;
        this.niceApi = niceApi;
        this.orderService = orderService;
        this.productService = productService;
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
                .baseUrl(niceApi)
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
            return "payment/complete";
        }
        return "payment/fail";
    }

    @GetMapping("/cancelAuth/{orderId}")
    public String requestCancel(@PathVariable String orderId, Model model) throws Exception {

        List<Order> orders = orderService.findOrder(orderId);
        if(orders.size() < 1) {
            return "order/cancel-fail";
        }
        int amount = 0;
        String tid = null;

        List<OrderResponseDto> orderResponseDtos = new ArrayList<>();
        for(Order order : orders) {
            Product product = productService.findById(order.getProductIdx());
            if(tid == null) {
               tid = order.getTid();
            }
            amount += product.getPrice() / 100 * (100 - product.getSale()) * order.getCount();

            OrderResponseDto orderResponseDto = new OrderResponseDto(order);
            orderResponseDto.setProduct(product);
            orderResponseDtos.add(orderResponseDto);
        }

        if(amount < 70000) {
            amount += 3500;
        }

        if(tid == null) {
            return "order/cancel-fail";
        }

        String accessToken = getAccessToken();

        Map<String, Object> authenticationMap = new HashMap<>();
        authenticationMap.put("amount", amount);
        authenticationMap.put("reason", "회원 요청");
        authenticationMap.put("orderId", orderId);

        JsonNode responseNode = client.post()
                .uri("/v1/payments/"+ tid +"/cancel")
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
            model.addAttribute("orders", orderService.delete(responseNode));
            return "order/cancel";
        }

        return "order/cancel-fail";
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
