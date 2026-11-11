package com.aipaca.rumbo.platform.profiles.application.internal.queryservices;
import com.aipaca.rumbo.platform.profiles.application.queryservices.StudentQueryService;
import com.aipaca.rumbo.platform.profiles.application.security.ProfilesAuthorization;
import com.aipaca.rumbo.platform.profiles.application.security.ProfilesAccessException;
import com.aipaca.rumbo.platform.profiles.domain.repositories.StudentRepository;
import com.aipaca.rumbo.platform.profiles.domain.model.queries.*;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly=true)
public class StudentQueryServiceImpl implements StudentQueryService {
    private final StudentRepository students;
    private final ProfilesAuthorization auth;
    public StudentQueryServiceImpl(StudentRepository students, ProfilesAuthorization auth) {
        this.students=students; this.auth=auth;
    }
    @Override public Optional<Student> handle(GetStudentByIdQuery query) {
        return Optional.of(auth.requireStudentAccess(query.studentId()));
    }
    @Override public List<Student> handle(GetAllStudentsByTutorIdQuery query) {
        var me=auth.currentTutor();
        if (!me.getId().equals(query.tutorId()))
            throw new ProfilesAccessException("Tutor list is not accessible", false);
        return students.findAllByAuthorizedTutorProfileId(me.getId()).stream()
                .filter(s->s.hasAuthorizedTutor(me.getId())).toList();
    }
    @Override public List<TutorStudentRelationship> handle(GetAllTutorStudentRelationshipsByStudentIdQuery query) {
        return auth.requireStudentAccess(query.studentId()).getTutorRelationships();
    }
    @Override public List<TutorStudentRelationship> handle(GetAllTutorStudentRelationshipsByTutorIdQuery query) {
        var me=auth.currentTutor();
        if (!me.getId().equals(query.tutorId()))
            throw new ProfilesAccessException("Tutor relationship history is not accessible", false);
        return students.findAllByTutorProfileId(me.getId()).stream()
                .flatMap(s->s.getTutorRelationships().stream().filter(r->r.getTutorProfileId().equals(me.getId())))
                .toList();
    }
    @Override public Optional<TutorStudentRelationship> handle(GetActiveTutorStudentRelationshipByTutorIdAndStudentIdQuery query) {
        var student=auth.requireStudentAccess(query.studentId());
        return student.getTutorRelationships().stream()
                .filter(r->r.getTutorProfileId().equals(query.tutorId()) && r.isAuthorized()).findFirst();
    }
}
