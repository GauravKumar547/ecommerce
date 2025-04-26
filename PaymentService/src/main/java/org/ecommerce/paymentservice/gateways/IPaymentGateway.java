package org.ecommerce.paymentservice.gateways;

public interface IPaymentGateway {
    String getPaymentLink(Long amount,String orderId,String phoneNumber,String name,String email);
}