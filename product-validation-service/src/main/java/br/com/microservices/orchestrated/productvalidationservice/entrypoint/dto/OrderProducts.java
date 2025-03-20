package br.com.microservices.orchestrated.productvalidationservice.entrypoint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderProducts {

    Product product;
    private int quantity;

}
