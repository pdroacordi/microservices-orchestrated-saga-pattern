package br.com.microservices.orchestrated.orderservice.entrypoint.controller;

import br.com.microservices.orchestrated.orderservice.core.document.Order;
import br.com.microservices.orchestrated.orderservice.core.service.OrderService;
import br.com.microservices.orchestrated.orderservice.entrypoint.dto.OrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping()
    public Order createOrder(@RequestBody OrderRequest orderRequest) {
        return orderService.create(orderRequest);
    }

}
