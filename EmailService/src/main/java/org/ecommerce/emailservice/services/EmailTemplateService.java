package org.ecommerce.emailservice.services;

import org.ecommerce.emailservice.dtos.EmailTemplateRequest;
import org.ecommerce.emailservice.models.EmailTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmailTemplateService {
    EmailTemplate createTemplate(EmailTemplateRequest request);
    EmailTemplate updateTemplate(Long id, EmailTemplateRequest request);
    void deleteTemplate(Long id);
    EmailTemplate getTemplateById(Long id);
    EmailTemplate getTemplateByName(String name);
    EmailTemplate getTemplateByNameAndLanguage(String name, String language);
    Page<EmailTemplate> getAllTemplates(Pageable pageable);
    String processTemplate(String templateName, String language, java.util.Map<String, Object> data);
} 