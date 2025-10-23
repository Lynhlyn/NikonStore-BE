package com.example.nikonbe.modules.contact.repository;

import com.example.nikonbe.common.enums.Status;
import com.example.nikonbe.modules.contact.entity.Contact;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer> {
  List<Contact> findByStatus(Status status);

  Page<Contact> findByStatus(Status status, Pageable pageable);

  boolean existsByPhone(String phone);
}
