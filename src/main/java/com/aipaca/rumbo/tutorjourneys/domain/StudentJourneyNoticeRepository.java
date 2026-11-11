package com.aipaca.rumbo.tutorjourneys.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StudentJourneyNoticeRepository
        extends JpaRepository<StudentJourneyNotice, Long> {
    boolean existsByEventKey(String eventKey);
    List<StudentJourneyNotice> findByStudentIdInOrderByCreatedAtDesc(List<String> studentIds);
}
