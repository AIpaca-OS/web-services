package com.aipaca.rumbo.platform.profiles.application.internal.queryservices;

import com.aipaca.rumbo.platform.profiles.application.queryservices.SchoolQueryService;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.School;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetAllSchoolsQuery;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.GetSchoolByIdQuery;
import com.aipaca.rumbo.platform.profiles.domain.repositories.SchoolRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SchoolQueryServiceImpl implements SchoolQueryService {

    private final SchoolRepository schoolRepository;

    public SchoolQueryServiceImpl(SchoolRepository schoolRepository) {
        this.schoolRepository = schoolRepository;
    }

    @Override
    public List<School> handle(GetAllSchoolsQuery query) {
        return schoolRepository.findAll();
    }

    @Override
    public Optional<School> handle(GetSchoolByIdQuery query) {
        return schoolRepository.findById(query.schoolId());
    }
}