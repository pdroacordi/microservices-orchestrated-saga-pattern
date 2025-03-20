package br.com.microservices.orchestrated.paymentservice.entrypoint.dto;

import br.com.microservices.orchestrated.paymentservice.core.enums.SagaStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class History {

    private String source;
    private SagaStatus status;
    private String message;
    private LocalDateTime createdAt;
}
