package org.ecommerce.emailservice.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailTemplateRequest {
    @NotBlank(message = "Template name is required")
    private String name;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Content is required")
    private String content;

    @NotBlank(message = "Language is required")
    private String language;

    private String description;
    private boolean isHtml = true;
    private boolean isActive = true;
} 