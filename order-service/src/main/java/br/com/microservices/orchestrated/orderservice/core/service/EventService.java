package br.com.microservices.orchestrated.orderservice.core.service;

import br.com.microservices.orchestrated.orderservice.core.document.Event;

import java.util.List;

public interface EventService {

    public Event save(Event event);

    void notifyEnding(Event event);

    List<Event> findAll();

}
