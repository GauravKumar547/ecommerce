package org.ecommerce.emailservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.ecommerce.emailservice.dtos.EmailRequest;
import org.ecommerce.emailservice.dtos.EmailResponse;
import org.ecommerce.emailservice.services.EmailService;
import org.ecommerce.emailservice.utils.response.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/emails")
@Tag(name = "Email Management", description = "APIs for managing emails")
@Validated
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    @Operation(summary = "Send email", description = "Send an email using a template")
    public ResponseEntity<ApiResponse<EmailResponse>> sendEmail(@Valid @RequestBody EmailRequest request) {
        ApiResponse<EmailResponse> apiResponse = new ApiResponse<>();
        EmailResponse response = emailService.sendEmail(request);
        apiResponse.setData(response).setStatus(HttpStatus.CREATED);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get email status", description = "Get the status of a sent email")
    public ResponseEntity<ApiResponse<EmailResponse>> getEmailStatus(@PathVariable Long id) {
        ApiResponse<EmailResponse> apiResponse = new ApiResponse<>();
        EmailResponse response = emailService.getEmailStatus(id);
        apiResponse.setData(response).setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @GetMapping("/recipient/{recipient}")
    @Operation(summary = "Get recipient emails", description = "Get all emails sent to a recipient")
    public ResponseEntity<ApiResponse<Page<EmailResponse>>> getEmailsByRecipient(
            @PathVariable String recipient,
            Pageable pageable) {
        ApiResponse<Page<EmailResponse>> apiResponse = new ApiResponse<>();
        Page<EmailResponse> response = emailService.getEmailsByRecipient(recipient, pageable);
        apiResponse.setData(response).setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @PostMapping("/retry-failed")
    @Operation(summary = "Retry failed emails", description = "Manually trigger retry of failed emails")
    public ResponseEntity<ApiResponse<Void>> retryFailedEmails() {
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        emailService.retryFailedEmails();
        apiResponse.setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @PostMapping("/process-dlq")
    @Operation(summary = "Process dead letters", description = "Manually trigger processing of dead letter queue")
    public ResponseEntity<ApiResponse<Void>> processDeadLetterQueue() {
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        emailService.processDeadLetterQueue();
        apiResponse.setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @DeleteMapping("/cleanup/{daysToKeep}")
    @Operation(summary = "Cleanup old emails", description = "Delete emails older than specified days")
    public ResponseEntity<ApiResponse<Void>> cleanupOldEmails(@PathVariable int daysToKeep) {
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        emailService.cleanupOldEmails(daysToKeep);
        apiResponse.setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }
} 