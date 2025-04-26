package org.ecommerce.paymentservice.services.impl;

import org.ecommerce.paymentservice.gateways.IPaymentGateway;
import org.ecommerce.paymentservice.gateways.PaymentGatewayStrategy;
import org.ecommerce.paymentservice.services.IPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements IPaymentService {

    private final PaymentGatewayStrategy paymentGatewayStrategy;

    @Autowired
    public PaymentServiceImpl(PaymentGatewayStrategy paymentGatewayStrategy) {
        this.paymentGatewayStrategy = paymentGatewayStrategy;
    }

    @Override
    public String getPaymentLink(Long amount, String orderId, String phoneNumber, String name, String email) {
        IPaymentGateway paymentGateway = paymentGatewayStrategy.getBestPaymentGateway(amount);
        return paymentGateway.getPaymentLink(amount, orderId, phoneNumber, name, email);
    }
}
