package br.com.microservices.orchestrated.orderservice.core.service.implementation;

import br.com.microservices.orchestrated.orderservice.config.exception.ValidationException;
import br.com.microservices.orchestrated.orderservice.core.document.Event;
import br.com.microservices.orchestrated.orderservice.core.repository.EventRepository;
import br.com.microservices.orchestrated.orderservice.core.service.EventService;
import br.com.microservices.orchestrated.orderservice.entrypoint.dto.EventFilters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.util.ObjectUtils.isEmpty;

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

    @Override
    public Event findByFilters(EventFilters filters) {
        validateFilters(filters);

        if( isEmpty( filters.getOrderId() ) ) {
            return findByTransactionId(filters.getTransactionId());
        }

        return findByOrderId(filters.getOrderId());
    }

    private Event findByOrderId(String orderId) {
        return repository
                .findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Event not found by orderID"));
    }

    private Event findByTransactionId(String orderId) {
        return repository
                .findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Event not found by transactionID"));
    }

    private void validateFilters(EventFilters filters) {
        if( isEmpty( filters.getOrderId() ) && isEmpty(filters.getTransactionId()) )
            throw new ValidationException("Either OrderId or TransactionId must be informed");
    }
}
