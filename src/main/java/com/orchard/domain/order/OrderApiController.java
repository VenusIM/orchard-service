package com.orchard.domain.order;

import com.orchard.domain.order.application.OrderService;
import com.orchard.domain.order.dto.OrderRequestDto;
import com.orchard.domain.order.dto.OrderResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderService orderService;

    @PostMapping("/id")
    public ResponseEntity<List<OrderResponseDto>> order(@RequestBody OrderRequestDto orderRequestDto) {
        return ResponseEntity.ok(orderService.save(orderRequestDto));
    }
}
