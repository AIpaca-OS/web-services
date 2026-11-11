package com.aipaca.rumbo.platform.profiles.domain.model.aggregates;

import com.aipaca.rumbo.platform.profiles.domain.model.commands.RegisterStudentCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.PersonName;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.StudentStatus;
import com.aipaca.rumbo.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

@Getter
public class Student extends AbstractDomainAggregateRoot<Student> {

    private Long id;
    private PersonName name;
    private LocalDate birthDate;
    private Long schoolId;
    private StudentStatus status;
    private Instant anonymizedAt;

    public Student(
            Long id,
            PersonName name,
            LocalDate birthDate,
            Long schoolId,
            StudentStatus status,
            Instant anonymizedAt
    ) {
        if (id != null && id < 1) {
            throw new IllegalArgumentException("Invalid student id");
        }

        if (schoolId != null && schoolId < 1) {
            throw new IllegalArgumentException("Invalid school id");
        }

        this.id = id;
        this.name = Objects.requireNonNull(
                name, "Student name is required");

        this.birthDate = Objects.requireNonNull(
                birthDate, "Birth date is required");

        this.schoolId = schoolId;

        this.status = Objects.requireNonNull(
                status, "Student status is required");

        this.anonymizedAt = anonymizedAt;
    }

    public Student(RegisterStudentCommand command) {
        this(
                null,
                new PersonName(
                        command.firstName(),
                        command.lastName()
                ),
                command.birthDate(),
                command.schoolId(),
                StudentStatus.ACTIVE,
                null
        );
    }

    public static Student reconstitute(
            Long id,
            PersonName name,
            LocalDate birthDate,
            Long schoolId,
            StudentStatus status,
            Instant anonymizedAt
    ) {
        if (id == null || id < 1) {
            throw new IllegalArgumentException("Invalid student id");
        }

        return new Student(
                id,
                name,
                birthDate,
                schoolId,
                status,
                anonymizedAt
        );
    }

    public void updateInformation(
            String firstName,
            String lastName,
            LocalDate birthDate,
            Long schoolId
    ) {
        if (this.anonymizedAt != null) {
            throw new IllegalStateException(
                    "Anonymized students cannot be updated"
            );
        }

        if (schoolId != null && schoolId < 1) {
            throw new IllegalArgumentException("Invalid school id");
        }

        var updatedName = new PersonName(firstName, lastName);
        Objects.requireNonNull(birthDate, "Birth date is required");

        this.name = updatedName;
        this.birthDate = birthDate;
        this.schoolId = schoolId;
    }

    public String getFullName() {
        return this.name.getFullName();
    }
}
