package com.orchard.domain.order;

import com.orchard.domain.member.domain.vo.UserEmail;
import com.orchard.domain.order.application.OrderService;
import com.orchard.domain.order.domain.persist.Order;
import com.orchard.domain.order.dto.OrderRequestDto;
import com.orchard.domain.order.dto.OrderResponseDto;
import com.orchard.domain.order.dto.OrderSearchRequestDto;
import com.orchard.domain.order.dto.TransRequestDto;
import com.orchard.global.common.ResultResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping("/id")
    public ResponseEntity<List<Order>> cancel(@RequestBody OrderRequestDto orderRequestDto) {
        return ResponseEntity.ok(orderService.delete(orderRequestDto));
    }

    @PostMapping("/anon/history")
    public ResponseEntity<ResultResponseDto> check(@RequestBody OrderRequestDto orderRequestDto) {
        ResultResponseDto resultResponseDto = new ResultResponseDto();
        if(orderService.findOrder(orderRequestDto).size() < 1) {
            resultResponseDto.setCode("400");
            resultResponseDto.setMsg("조회된 주문 내역이 없습니다.");
        } else {
            resultResponseDto.setCode("200");
        }
        return ResponseEntity.ok(resultResponseDto);
    }

    @PatchMapping("/trans")
    public ResponseEntity<ResultResponseDto> trans(@RequestBody TransRequestDto transRequestDto) {
        ResultResponseDto resultResponseDto = new ResultResponseDto();
        if(orderService.trans(transRequestDto).size() < 1) {
            resultResponseDto.setCode("400");
            resultResponseDto.setMsg("배송 가능 상품이 없습니다.");
        } else {
            resultResponseDto.setCode("200");
        }
        return ResponseEntity.ok(resultResponseDto);
    }

    @PatchMapping("/trans/complete")
    public ResponseEntity<ResultResponseDto> complete(@RequestBody TransRequestDto transRequestDto) {
        ResultResponseDto resultResponseDto = new ResultResponseDto();
        if(orderService.transComplete(transRequestDto).size() < 1) {
            resultResponseDto.setCode("400");
            resultResponseDto.setMsg("배송 가능 상품이 없습니다.");
        } else {
            resultResponseDto.setCode("200");
        }
        return ResponseEntity.ok(resultResponseDto);
    }

}
