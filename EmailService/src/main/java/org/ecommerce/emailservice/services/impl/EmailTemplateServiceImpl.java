package org.ecommerce.emailservice.services.impl;

import jakarta.persistence.EntityNotFoundException;
import org.ecommerce.emailservice.dtos.EmailTemplateRequest;
import org.ecommerce.emailservice.models.EmailTemplate;
import org.ecommerce.emailservice.repositories.EmailTemplateRepository;
import org.ecommerce.emailservice.services.EmailTemplateService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
public class EmailTemplateServiceImpl implements EmailTemplateService {

    private final EmailTemplateRepository templateRepository;
    private final ITemplateEngine templateEngine;

    public EmailTemplateServiceImpl(EmailTemplateRepository templateRepository, ITemplateEngine templateEngine) {
        this.templateRepository = templateRepository;
        this.templateEngine = templateEngine;
    }

    @Override
    public EmailTemplate createTemplate(EmailTemplateRequest request) {
        EmailTemplate template = new EmailTemplate();
        template.setName(request.getName());
        template.setSubject(request.getSubject());
        template.setContent(request.getContent());
        template.setLanguage(request.getLanguage());
        template.setDescription(request.getDescription());
        template.setHtml(request.isHtml());
        template.setActive(request.isActive());
        return templateRepository.save(template);
    }

    @Override
    public EmailTemplate updateTemplate(Long id, EmailTemplateRequest request) {
        EmailTemplate template = getTemplateById(id);
        template.setName(request.getName());
        template.setSubject(request.getSubject());
        template.setContent(request.getContent());
        template.setLanguage(request.getLanguage());
        template.setDescription(request.getDescription());
        template.setHtml(request.isHtml());
        template.setActive(request.isActive());
        return templateRepository.save(template);
    }

    @Override
    public void deleteTemplate(Long id) {
        templateRepository.deleteById(id);
    }

    @Override
    public EmailTemplate getTemplateById(Long id) {
        return templateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Template not found with id: " + id));
    }

    @Override
    public EmailTemplate getTemplateByName(String name) {
        return templateRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Template not found with name: " + name));
    }

    @Override
    public EmailTemplate getTemplateByNameAndLanguage(String name, String language) {
        return templateRepository.findByNameAndLanguage(name, language)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Template not found with name: %s and language: %s", name, language)));
    }

    @Override
    public Page<EmailTemplate> getAllTemplates(Pageable pageable) {
        return templateRepository.findAll(pageable);
    }

    @Override
    public String processTemplate(String templateName, String language, Map<String, Object> data) {
        EmailTemplate template = getTemplateByNameAndLanguage(templateName, language);
        
        if (!template.isActive()) {
            throw new IllegalStateException("Template is not active: " + templateName);
        }

        Context context = new Context();
        context.setVariables(data);

        return templateEngine.process(template.getContent(), context);
    }
} 