package org.ecommerce.emailservice.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Map;

@Data
public class EmailRequest {
    @NotBlank(message = "Recipient email is required")
    @Email(message = "Invalid email format")
    private String recipient;

    @NotBlank(message = "Template name is required")
    private String templateName;

    @NotBlank(message = "Language is required")
    private String language;

    @NotNull(message = "Template data is required")
    private Map<String, Object> templateData;

    @NotBlank(message = "Message ID is required")
    private String messageId;
} 