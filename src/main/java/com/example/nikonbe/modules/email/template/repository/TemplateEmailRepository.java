package com.example.nikonbe.modules.email.template.repository;

import com.example.nikonbe.common.enums.EmailAction;
import com.example.nikonbe.modules.email.template.entity.TemplateEmail;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateEmailRepository extends JpaRepository<TemplateEmail, Integer> {

  Optional<TemplateEmail> findByAction(EmailAction action);

  boolean existsByAction(EmailAction action);

  boolean existsByActionAndIdNot(EmailAction action, Integer id);
}
