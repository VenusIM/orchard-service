package com.orchard.domain.product.application;

import com.orchard.domain.product.domain.pesist.Product;
import com.orchard.domain.product.domain.pesist.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Product findById(Long id) {
       return productRepository.findById(id).get();
    }

}
