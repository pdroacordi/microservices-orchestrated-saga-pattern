package br.com.microservices.orchestrated.productvalidationservice.core.service;

import br.com.microservices.orchestrated.productvalidationservice.entrypoint.dto.Event;

public interface ProductValidationService {

    void validateExistingProducts(Event event);

    void rollbackEvent(Event event);

}
