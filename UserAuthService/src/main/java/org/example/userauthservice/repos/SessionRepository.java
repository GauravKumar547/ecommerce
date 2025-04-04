package org.example.userauthservice.repos;

import org.example.userauthservice.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
     Optional<Session> findByTokenAndUserId(String token, Long userId);
     List<Session> findAllByUserId(Long userId);
}