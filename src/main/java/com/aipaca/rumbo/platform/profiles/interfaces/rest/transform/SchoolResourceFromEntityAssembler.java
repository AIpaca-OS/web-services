package com.aipaca.rumbo.platform.profiles.interfaces.rest.transform;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.School;
import com.aipaca.rumbo.platform.profiles.interfaces.rest.resources.SchoolResource;
public final class SchoolResourceFromEntityAssembler {
    private SchoolResourceFromEntityAssembler() { }
    public static SchoolResource toResourceFromEntity(School s) {
        return new SchoolResource(s.getId(),s.getName(),s.getAddress(),s.getDistrict());
    }
}
