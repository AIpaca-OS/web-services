package com.aipaca.rumbo.platform.profiles.interfaces.rest.transform;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.*;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.AccountId;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.resources.*;

/** Joins trusted authenticated actor identity with validated HTTP resource fields. */
public final class ProfilesCommandsFromResourceAssembler {
    private ProfilesCommandsFromResourceAssembler() { }
    public static CreateProfileCommand createProfile(CreateProfileResource r, Long accountId) {
        return new CreateProfileCommand(new AccountId(accountId),r.profileType(),r.firstName(),r.lastName(),r.phoneNumber());
    }
    public static RegisterStudentCommand registerStudent(CreateStudentResource r, Long tutorProfileId) {
        return new RegisterStudentCommand(tutorProfileId,r.firstName(),r.lastName(),r.birthDate(),r.schoolId(),r.relationshipType());
    }
    public static UpdateStudentInformationCommand updateStudent(Long id,UpdateStudentResource r) {
        return new UpdateStudentInformationCommand(id,r.firstName(),r.lastName(),r.birthDate(),r.schoolId());
    }
    public static AuthorizeTutorStudentRelationshipCommand authorizeTutor(Long studentId,AuthorizeTutorResource r,Long actorProfileId) {
        return new AuthorizeTutorStudentRelationshipCommand(studentId,r.tutorProfileId(),actorProfileId,r.relationshipType());
    }
    public static RequestServiceEnrollmentCommand requestEnrollment(RequestEnrollmentResource r,Long actorProfileId) {
        return new RequestServiceEnrollmentCommand(r.studentId(),r.driverProfileId(),actorProfileId);
    }
    public static RequestStudentDataDeletionCommand requestDeletion(RequestDeletionResource r,Long actorProfileId) {
        return new RequestStudentDataDeletionCommand(r.studentId(),actorProfileId);
    }
}
