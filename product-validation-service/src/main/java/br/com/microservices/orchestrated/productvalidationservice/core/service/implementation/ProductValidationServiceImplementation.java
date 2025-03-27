package br.com.microservices.orchestrated.productvalidationservice.core.service.implementation;

import br.com.microservices.orchestrated.productvalidationservice.config.exception.ValidationException;
import br.com.microservices.orchestrated.productvalidationservice.core.entity.Validation;
import br.com.microservices.orchestrated.productvalidationservice.core.enums.SagaStatus;
import br.com.microservices.orchestrated.productvalidationservice.core.repository.ProductRepository;
import br.com.microservices.orchestrated.productvalidationservice.core.repository.ValidationRepository;
import br.com.microservices.orchestrated.productvalidationservice.core.service.ProductValidationService;
import br.com.microservices.orchestrated.productvalidationservice.entrypoint.dto.Event;
import br.com.microservices.orchestrated.productvalidationservice.entrypoint.dto.History;
import br.com.microservices.orchestrated.productvalidationservice.entrypoint.dto.OrderProducts;
import br.com.microservices.orchestrated.productvalidationservice.entrypoint.producer.KafkaProducer;
import br.com.microservices.orchestrated.productvalidationservice.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static br.com.microservices.orchestrated.productvalidationservice.core.enums.SagaStatus.*;
import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductValidationServiceImplementation implements ProductValidationService {

    private static final String CURRENT_SOURCE = "PRODUCT_VALIDATION_SERVICE";

    private final JsonUtil jsonUtil;
    private final KafkaProducer producer;
    private final ProductRepository productRepository;
    private final ValidationRepository validationRepository;

    @Override
    public void validateExistingProducts(Event event) {
        try{
            checkCurrentValidation(event);
            createValidation(event, true);
            handleSuccess(event);
        }catch (Exception e){
            log.error("Error while validating product event: ", e);
            handleFailCurrentNotExecuted(event, e.getMessage());
        }

        producer.sendEvent( jsonUtil.toJson(event) );
    }

    @Override
    public void rollbackEvent(Event event) {
        changeValidationToFail(event);
        event.setStatus(FAIL);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Rollback executed at product validation");
        producer.sendEvent( jsonUtil.toJson(event) );
    }

    private void checkCurrentValidation(Event event) {
        validatePayload(event);
        if( validationRepository.existsByOrderIdAndTransactionId(
                event.getOrderId(), event.getTransactionId()) )
            throw new ValidationException("The orderId and transactionId cannot be duplicated.");

        event.getPayload().getProducts().forEach(this::validateProduct);
    }

    private void validateProduct(OrderProducts product) {
        if( isEmpty(product.getProduct()) || isEmpty(product.getProduct().getCode()) )
            throw new ValidationException("The product code cannot be empty.");

        if( !productRepository.existsByCode(product.getProduct().getCode()) )
            throw new ValidationException("There is no correspondent product to the product code.");

    }

    private void validatePayload(Event event) {
        if( isEmpty(event.getPayload()) || isEmpty(event.getPayload().getProducts()) )
            throw new ValidationException("Product list is empty");

        if( isEmpty(event.getId()) || isEmpty(event.getTransactionId()) )
            throw new ValidationException("OrderId and TransactionId must be informed");
    }

    private void createValidation(Event event, boolean success) {
        var validation = Validation.builder()
                .orderId(event.getPayload().getId())
                .transactionId(event.getTransactionId())
                .success(success)
                .build();
        validationRepository.save(validation);
    }

    private void handleSuccess(Event event) {
        event.setStatus(SUCCESS);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Products successfully validated.");
    }

    private void addHistory(Event event, String message) {
        var history = History.builder()
                .source(event.getSource())
                .status(event.getStatus())
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
        event.addToHistory(history);
    }

    private void handleFailCurrentNotExecuted(Event event, String message) {
        event.setStatus(ROLLBACK_PENDING);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Validation of product failed: " + message);
    }

    private void changeValidationToFail(Event event) {
        validationRepository
                .findByOrderIdAndTransactionId(event.getPayload().getId(), event.getTransactionId())
                .ifPresentOrElse(validation -> {
                    validation.setSuccess(false);
                    validationRepository.save(validation);
                }, () -> createValidation(event, false));
    }
}
