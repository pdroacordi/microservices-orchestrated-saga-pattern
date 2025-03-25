package br.com.microservices.orchestrated.orderservice.core.service.implementation;

import br.com.microservices.orchestrated.orderservice.core.document.Event;
import br.com.microservices.orchestrated.orderservice.core.repository.EventRepository;
import br.com.microservices.orchestrated.orderservice.core.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventServiceImplementation implements EventService {

    private final EventRepository repository;


    @Override
    public Event save(Event event) {
        return repository.save(event);
    }
}
