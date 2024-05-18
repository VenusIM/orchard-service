package com.orchard.domain.product;

import com.orchard.domain.product.application.ProductService;
import com.orchard.domain.product.domain.pesist.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final ProductService productService;
    @GetMapping("/list")
    public String cart(Model model) {
        model.addAttribute("products", productService.findAll());
        return "product/cart";
    }
}
