package org.ecommerce.emailservice.repositories;

import org.ecommerce.emailservice.models.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {
    Optional<EmailTemplate> findByName(String name);
    List<EmailTemplate> findByIsActiveTrue();
    Optional<EmailTemplate> findByNameAndLanguage(String name, String language);
} 