package com.aipaca.rumbo.platform.profiles.application.internal.queryservices;
import com.aipaca.rumbo.platform.profiles.application.queryservices.SchoolQueryService;
import com.aipaca.rumbo.platform.profiles.domain.repositories.SchoolRepository;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.*;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.School;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly=true)
public class SchoolQueryServiceImpl implements SchoolQueryService {
    private final SchoolRepository schools;
    public SchoolQueryServiceImpl(SchoolRepository schools) { this.schools=schools; }
    @Override public List<School> handle(GetAllSchoolsQuery query) { return schools.findAll(); }
    @Override public Optional<School> handle(GetSchoolByIdQuery query) { return schools.findById(query.schoolId()); }
}
