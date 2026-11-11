package com.aipaca.rumbo.platform.profiles.interfaces.rest.resources;

import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.StudentStatus;

/** Minimum information the assigned driver needs to identify an enrolled student. */
public record DriverStudentResource(Long id, String firstName, String lastName,
                                    Long schoolId, StudentStatus status) { }
