package org.ecommerce.paymentservice.gateways;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentGatewayStrategy {

    private final StripeGateway stripeGateway;
    private final RazorpayGateway razorpayGateway;

    @Autowired
    public PaymentGatewayStrategy(StripeGateway stripeGateway, RazorpayGateway razorpayGateway) {
        this.stripeGateway = stripeGateway;
        this.razorpayGateway = razorpayGateway;
    }

    public IPaymentGateway getBestPaymentGateway(double amount) {
        if (amount %2 == 0) {
            return stripeGateway;
        } else {
            return razorpayGateway;
        }
    }
}
