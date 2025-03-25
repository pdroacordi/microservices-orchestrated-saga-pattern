package br.com.microservices.orchestrated.orderservice.entrypoint.controller;

import br.com.microservices.orchestrated.orderservice.core.document.Event;
import br.com.microservices.orchestrated.orderservice.core.service.EventService;
import br.com.microservices.orchestrated.orderservice.entrypoint.dto.EventFilters;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/event")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping("/search")
    public Event findByFilters(@RequestParam(name = "orderId", required = false) String orderId,
                               @RequestParam(name = "transactionId", required = false) String transactionId) {
        var filters = EventFilters.builder()
                .orderId(orderId)
                .transactionId(transactionId)
                .build();
        return eventService.findByFilters(filters);
    }

    @GetMapping()
    public List<Event> findAll(){
        return eventService.findAll();
    }

}
