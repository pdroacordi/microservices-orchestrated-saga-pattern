package br.com.microservices.orchestrated.orderservice.core.service;

import br.com.microservices.orchestrated.orderservice.core.document.Order;
import br.com.microservices.orchestrated.orderservice.entrypoint.dto.OrderRequest;

public interface OrderService {

    Order create(OrderRequest orderRequest);

}
