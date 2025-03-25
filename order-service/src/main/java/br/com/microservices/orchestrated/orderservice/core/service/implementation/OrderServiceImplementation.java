package br.com.microservices.orchestrated.orderservice.core.service.implementation;

import br.com.microservices.orchestrated.orderservice.core.document.Event;
import br.com.microservices.orchestrated.orderservice.core.document.Order;
import br.com.microservices.orchestrated.orderservice.core.repository.OrderRepository;
import br.com.microservices.orchestrated.orderservice.core.service.EventService;
import br.com.microservices.orchestrated.orderservice.core.service.OrderService;
import br.com.microservices.orchestrated.orderservice.entrypoint.dto.OrderRequest;
import br.com.microservices.orchestrated.orderservice.entrypoint.kafka.producer.SagaProducer;
import br.com.microservices.orchestrated.orderservice.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImplementation implements OrderService {

    private static final String TRANSACTION_ID_PATTERN = "%s_%s";

    private final OrderRepository repository;
    private final JsonUtil jsonUtil;
    private final SagaProducer producer;
    private final EventService eventService;

    @Override
    public Order create(OrderRequest orderRequest) {
        var order = Order.builder()
                .products(orderRequest.getProducts())
                .createdAt(LocalDateTime.now())
                .transactionId(
                        String.format(TRANSACTION_ID_PATTERN, Instant.now().toEpochMilli(), UUID.randomUUID())
                )
                .build();

        order = repository.save(order);
        var payload = createPayload(order);
        producer.sendEvent(jsonUtil.toJson( payload ) );

        return order;
    }

    private Event createPayload(Order order) {
        var event = Event.builder()
                .orderId( order.getId() )
                .transactionId( order.getTransactionId() )
                .payload( order )
                .createdAt( LocalDateTime.now() )
                .build();

        return eventService.save(event);
    }
}
