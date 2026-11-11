package com.aipaca.rumbo.platform.profiles.infrastructure.configuration;

import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.Profile;
import com.aipaca.rumbo.platform.profiles.domain.model.aggregates.School;
import com.aipaca.rumbo.platform.profiles.domain.model.commands.CreateProfileCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.AccountId;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.ProfileType;
import com.aipaca.rumbo.platform.profiles.domain.repositories.ProfileRepository;
import com.aipaca.rumbo.platform.profiles.domain.repositories.SchoolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** Seeds only fictitious profiles and a school for a dedicated LOCAL demo database. */
@Component
@org.springframework.context.annotation.Profile("dev")
@ConditionalOnProperty(prefix="app.profiles.demo",name="enabled",havingValue="true")
public class LocalDemoProfilesSeeder implements CommandLineRunner {
    private final ProfileRepository profiles;
    private final SchoolRepository schools;
    public LocalDemoProfilesSeeder(ProfileRepository profiles,SchoolRepository schools) {
        this.profiles=profiles;this.schools=schools;
    }
    @Override public void run(String... args) {
        addProfile(10001L,ProfileType.TUTOR,
                "Tutor","Demo","900000001");
        addProfile(10002L,ProfileType.DRIVER,
                "Conductor","Demo","900000002");
        if (schools.findAll().stream().noneMatch(s -> "Colegio Demostración".equals(s.getName()))) {
            schools.save(new School("Colegio Demostración","Av. Demo 123","Lima"));
        }
    }
    private void addProfile(long accountId,ProfileType type,String firstName,String lastName,String phone) {
        var id=new AccountId(accountId);
        if (!profiles.existsByAccountId(id)) {
            profiles.save(new Profile(new CreateProfileCommand(id,type,firstName,lastName,phone)));
        }
    }
}
