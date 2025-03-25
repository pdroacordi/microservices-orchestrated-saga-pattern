package br.com.microservices.orchestrated.orderservice.core.service;

import br.com.microservices.orchestrated.orderservice.core.document.Event;
import br.com.microservices.orchestrated.orderservice.entrypoint.dto.EventFilters;

import java.util.List;

public interface EventService {

    Event save(Event event);

    void notifyEnding(Event event);

    List<Event> findAll();

    Event findByFilters(EventFilters filters);

}
