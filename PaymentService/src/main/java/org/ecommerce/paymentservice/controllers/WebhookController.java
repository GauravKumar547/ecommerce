package org.ecommerce.paymentservice.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook")
public class WebhookController {
    @PostMapping
    public void handleWebhook(@RequestBody String event) {
        // Handle the webhook event here
        // You can access the request body and process the event accordingly
        System.out.println("Received webhook event: " + event);
    }
}
