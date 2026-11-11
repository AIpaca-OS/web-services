package com.aipaca.rumbo.platform.profiles.interfaces.rest.transform;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Student;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.resources.StudentResource;
public final class StudentResourceFromEntityAssembler {
    private StudentResourceFromEntityAssembler() { }
    public static StudentResource toResourceFromEntity(Student s) {
        return new StudentResource(s.getId(),s.getName().firstName(),s.getName().lastName(),s.getBirthDate(),s.getSchoolId(),s.getStatus(),s.getAnonymizedAt());
    }
}
