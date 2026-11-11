package com.aipaca.rumbo.platform.profiles.application.internal.queryservices;

import com.aipaca.rumbo.platform.profiles.application.queryservices.SchoolQueryService;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.School;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetAllSchoolsQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetSchoolByIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.repositories.SchoolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * School Query Service Implementation
 */
@Service
@Transactional(readOnly = true)
public class SchoolQueryServiceImpl implements SchoolQueryService {
    private final SchoolRepository schoolRepository;

    /**
     * Constructor
     *
     * @param schoolRepository The {@link SchoolRepository} instance
     */
    public SchoolQueryServiceImpl(SchoolRepository schoolRepository) {
        this.schoolRepository = schoolRepository;
    }

    // inherited javadoc
    @Override
    public List<School> handle(GetAllSchoolsQuery query) {
        return schoolRepository.findAll();
    }

    // inherited javadoc
    @Override
    public Optional<School> handle(GetSchoolByIdQuery query) {
        return schoolRepository.findById(query.schoolId());
    }
}
