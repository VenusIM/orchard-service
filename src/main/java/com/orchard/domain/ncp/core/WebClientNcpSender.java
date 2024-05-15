package com.orchard.domain.ncp.core;

import java.time.Duration;
import java.util.List;

import com.orchard.global.common.ClassUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.DefaultUriBuilderFactory;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Slf4j
@Component
public class WebClientNcpSender {

    private WebClient client;

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

        client = WebClient.builder().clientConnector(
                new ReactorClientHttpConnector(
                        HttpClient.create(provider)
                                .secure(t -> t.sslContext(sslContext))
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 20000) //miliseconds
                                .doOnConnected(
                                        conn -> conn.addHandlerLast(new ReadTimeoutHandler(30))  //sec
                                                .addHandlerLast(new WriteTimeoutHandler(60)) //sec
                                ).responseTimeout(Duration.ofSeconds(60))
                )
        ).build();

    }

    public <T extends AbstractResponse> ResponseEntity<T> sendWithBlock(NcpApiOption ncpApiOption, ParameterizedTypeReference<T> typeReference) {
        return this.send(ncpApiOption, typeReference).block();
    }

    public <T extends AbstractResponse> List<ResponseEntity<T>> fetchSendWithBlock(List<NcpApiOption> ncpApiOption, ParameterizedTypeReference<T> typeReference) {
        return this.fetchSend(ncpApiOption, typeReference).collectList().block();
    }

    private <T extends AbstractResponse> Flux<ResponseEntity<T>> fetchSend(List<NcpApiOption> ncpApiOption, ParameterizedTypeReference<T> typeReference) {
        return Flux.fromIterable(ncpApiOption)
                .parallel()
                .runOn(Schedulers.boundedElastic())
                .doOnError(e -> log.error(e.getMessage(), e))
                .flatMap(option -> send(option, typeReference))
                .ordered((u1, u2) -> 1 - 2);
    }

    private <T extends AbstractResponse> Mono<ResponseEntity<T>> send(NcpApiOption ncpApiOption, ParameterizedTypeReference<T> typeReference) {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(NcpApiOption.smsApiUrl + ncpApiOption.getPath());
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);

        return client.mutate().uriBuilderFactory(factory).build()
                .method(ncpApiOption.getMethod())
                .uri(NcpApiOption.smsApiUrl + ncpApiOption.getPath())
                .headers(httpHeaders -> {
                    httpHeaders.set(HttpHeaders.CONTENT_TYPE, ncpApiOption.getContentType());
                    httpHeaders.set(NcpHeaders.API_GATEWAY_TIMESTAMP.get(), ncpApiOption.getTimestamp());
                    httpHeaders.set(NcpHeaders.IAM_ACCESS_KEY.get(), NcpApiOption.getAccessKey());
                    httpHeaders.set(NcpHeaders.API_GATEWAY_SIGNATURE.get(), ncpApiOption.getSignatureKey());
                })
//                .accept(ncpApiOption.getMediaType())
                .bodyValue(ncpApiOption.getRequest())
                .retrieve()
                .toEntity(typeReference)
                .flatMap(clientResponse -> {
                    clientResponse.getBody().setData(ncpApiOption.getData());
                    clientResponse.getBody().setNcpStatusCode(clientResponse.getStatusCode().value());
                    clientResponse.getBody().setNcpStatusMessage(String.valueOf(HttpStatus.valueOf(clientResponse.getStatusCode().value())));
                    return Mono.just(clientResponse);
                })
                .onErrorResume(error -> Mono.just(handleError(error, ncpApiOption, typeReference)));
    }

    private <T extends AbstractResponse> ResponseEntity<T> handleError(Throwable error, NcpApiOption ncpApiOption, ParameterizedTypeReference<T> typeReference) {
        ResponseEntity<T> response = null;
        try {
            Class<T> paramCls = (Class<T>) ClassUtils.getClass(typeReference.getType());
            response = new ResponseEntity<>(paramCls.getDeclaredConstructor().newInstance(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("err: {}", e);
        }

        if (error instanceof WebClientException) {
            //http 에러
            WebClientResponseException err = (WebClientResponseException) error;
            response.getBody().setNcpStatusCode(err.getStatusCode().value());
            response.getBody().setNcpStatusMessage(String.valueOf(HttpStatus.valueOf(err.getStatusCode().value())));
        } else if (error instanceof ReadTimeoutException) {
            //readtimeout 에러
            response.getBody().setNcpStatusCode(NcpErrorStatus.READ_TIMEOUT.value());
            response.getBody().setNcpStatusMessage(String.valueOf(NcpErrorStatus.valueOf(NcpErrorStatus.READ_TIMEOUT.value())));
        } else {
            //기타에러
            log.error("err:{}", error);
            response.getBody().setNcpStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.getBody().setNcpStatusMessage(String.valueOf(HttpStatus.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())));
        }

        response.getBody().setData(ncpApiOption.getData());
        response.getBody().setThrowable(error);
        return response;
    }

}