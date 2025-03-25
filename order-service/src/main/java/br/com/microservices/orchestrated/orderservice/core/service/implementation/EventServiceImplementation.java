package br.com.microservices.orchestrated.orderservice.core.service.implementation;

import br.com.microservices.orchestrated.orderservice.core.document.Event;
import br.com.microservices.orchestrated.orderservice.core.repository.EventRepository;
import br.com.microservices.orchestrated.orderservice.core.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImplementation implements EventService {

    private final EventRepository repository;


    @Override
    public Event save(Event event) {
        return repository.save(event);
    }

    @Override
    public void notifyEnding(Event event) {
        event.setOrderId( event.getOrderId() );
        event.setCreatedAt( LocalDateTime.now() );

        save(event);

        log.info("Order {} with saga notified. TransactionId: {}", event.getOrderId(), event.getTransactionId());
    }

    @Override
    public List<Event> findAll() {
        return repository.findAllByOrderByCreatedAtDesc();
    }
}
