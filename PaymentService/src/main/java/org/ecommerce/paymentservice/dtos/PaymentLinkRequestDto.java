package org.ecommerce.paymentservice.dtos;

import lombok.Data;

@Data
public class PaymentLinkRequestDto {
    private Long amount;
    private String orderId;
    private String phoneNumber;
    private String name;
    private String email;
}
