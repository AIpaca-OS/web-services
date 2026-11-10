package com.aipaca.rumbo.platform.profiles.domain.model.aggregates;

import com.aipaca.rumbo.platform.profiles.domain.model.commands.CreateProfileCommand;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.AccountId;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.PersonName;
import com.aipaca.rumbo.platform.profiles.domain.model.valueobjects.ProfileType;
import com.aipaca.rumbo.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

import lombok.Getter;
import java.util.Objects;

/**
 * Profile aggregate root.
 *
 * <p>
 * Represents the personal profile linked to an account of the platform.
 * A profile is defined by its account, its type, the person name and a contact phone number.
 * </p>
 */
@Getter
public class Profile extends AbstractDomainAggregateRoot<Profile> {
    /**
     * The unique identifier for the profile.
     */
    private Long id;

    /**
     * The account that owns this profile.
     */
    private AccountId accountId;

    /**
     * The type of the profile.
     */
    private ProfileType profileType;

    /**
     * The name of the person.
     */
    private PersonName name;

    /**
     * The contact phone number.
     */
    private String phoneNumber;

    /**
     * Creates a profile from the provided domain values.
     */
    public Profile(
            Long id,
            AccountId accountId,
            ProfileType profileType,
            PersonName name,
            String phoneNumber
    ) {
        if (id != null && id < 1) {
            throw new IllegalArgumentException("Invalid profile id");
        }

        this.id = id;
        this.accountId = Objects.requireNonNull(
                accountId, "Account id is required");

        this.profileType = Objects.requireNonNull(
                profileType, "Profile type is required");

        this.name = Objects.requireNonNull(
                name, "Person name is required");

        this.phoneNumber = validatePhoneNumber(phoneNumber);
    }

    public Profile(CreateProfileCommand command) {
        this(
                null,
                command.accountId(),
                command.profileType(),
                new PersonName(
                        command.firstName(),
                        command.lastName()
                ),
                command.phoneNumber()
        );
    }

    public static Profile reconstitute(
            Long id,
            AccountId accountId,
            ProfileType profileType,
            PersonName name,
            String phoneNumber
    ) {
        if (id == null || id < 1) {
            throw new IllegalArgumentException("Invalid profile id");
        }

        return new Profile(
                id,
                accountId,
                profileType,
                name,
                phoneNumber
        );
    }

    public void updateContact(String phoneNumber) {
        this.phoneNumber = validatePhoneNumber(phoneNumber);
    }

    public String getFullName() {
        return this.name.getFullName();
    }

    private static String validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Phone number cannot be null or blank"
            );
        }

        return phoneNumber;
    }
}
