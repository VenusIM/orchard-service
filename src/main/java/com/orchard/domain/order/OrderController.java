package com.orchard.domain.order;

import com.orchard.domain.member.application.MemberManagementService;
import com.orchard.domain.member.domain.persist.Member;
import com.orchard.domain.member.domain.vo.UserEmail;
import com.orchard.domain.member.dto.MemberResponseDTO;
import com.orchard.domain.order.application.OrderService;
import com.orchard.domain.order.domain.persist.Order;
import com.orchard.domain.order.domain.persist.OrderRepository;
import com.orchard.domain.order.dto.OrderSearchRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final MemberManagementService memberManagementService;
    @GetMapping("/history")
    public String history(Model model) {
        UserEmail email = UserEmail.from(SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("orders", orderService.findOrder(email));
        return "order/history";
    }

    @GetMapping("/history/manage")
    public String adminOrder(Model model) {
        model.addAttribute("orders", orderService.findAdminOrder());
        return "order/historyadmin";
    }

    @GetMapping("/history/manage/trans")
    public String adminTrans(Model model) {
        model.addAttribute("orders", orderService.findAdminTrans());
        return "order/historyadmin";
    }

    @GetMapping("/history/manage/cancel")
    public String adminCancel(Model model) {
        model.addAttribute("orders", orderService.findAdminCancel());
        return "order/historyadmin";
    }

    @PostMapping("/history/anon")
    public String anon(@RequestBody OrderSearchRequestDto orderSearchRequestDto, Model model) {
        model.addAttribute("orders", orderService.findAnonOrder(orderSearchRequestDto));
        return "order/history";
    }
}
