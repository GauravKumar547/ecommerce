package org.ecommerce.paymentservice.controllers;

import org.ecommerce.paymentservice.dtos.PaymentLinkRequestDto;
import org.ecommerce.paymentservice.services.IPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private IPaymentService paymentService;

    @Autowired
    public PaymentController(IPaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public String createPaymentLink(@RequestBody PaymentLinkRequestDto request) {
        return paymentService.getPaymentLink(request.getAmount(), request.getOrderId(), request.getPhoneNumber(), request.getName(), request.getEmail());
    }
}
