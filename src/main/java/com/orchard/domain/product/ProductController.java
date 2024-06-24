package com.orchard.domain.product;

import com.orchard.domain.product.application.ProductService;
import com.orchard.domain.product.domain.pesist.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    @GetMapping("/list")
    public String productList(Model model) {
        model.addAttribute("products", productService.findAll());
        return "product/product";
    }

    @GetMapping("/detail/{number}")
    public String detail(Model model, @PathVariable String number) {
       model.addAttribute("product", productService.findById(Long.parseLong(number)));
       return "product/detail";
    }

    @GetMapping("cart")
    public String cart(Model model) {
        model.addAttribute("products", productService.findAll());
        return "product/cart";
    }
}
