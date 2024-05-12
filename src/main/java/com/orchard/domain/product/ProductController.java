package com.orchard.domain.product;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/product")
public class ProductController {

    private final String clientId;
    private final String secretKey;

    public ProductController(@Value("${nice.client-id}") String clientId, @Value("${nice.secret-key}")String secretKey) {
        this.clientId = clientId;
        this.secretKey = secretKey;
    }
    @GetMapping("/list")
    public String productList(Model model) {
        return "product/product";
    }

    @GetMapping("/detail")
    public String detail(Model model) {
        model.addAttribute("clientId", clientId);
        return "product/detail";
    }
}
