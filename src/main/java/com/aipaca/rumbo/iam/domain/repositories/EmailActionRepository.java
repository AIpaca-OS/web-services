package com.aipaca.rumbo.iam.domain.repositories;

import com.aipaca.rumbo.iam.domain.model.EmailAction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmailActionRepository extends JpaRepository<EmailAction, Long> {
    Optional<EmailAction> findByTokenHashAndPurpose(String tokenHash, EmailAction.Purpose purpose);
}
