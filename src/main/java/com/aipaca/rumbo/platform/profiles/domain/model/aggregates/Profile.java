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
 * No JPA or persistence annotation is present here — those concerns live
 * exclusively in {@code ProfilePersistenceEntity}.
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
     *
     * @param id          The profile id, or null for a new profile.
     * @param accountId   The account that owns the profile.
     * @param profileType The type of the profile.
     * @param name        The name of the person.
     * @param phoneNumber The contact phone number.
     */
    public Profile(Long id, AccountId accountId, ProfileType profileType, PersonName name, String phoneNumber) {
        if (id != null && id < 1) {
            throw new IllegalArgumentException("Invalid profile id");
        }
        this.id = id;
        this.accountId = Objects.requireNonNull(accountId, "Account id is required");
        this.profileType = Objects.requireNonNull(profileType, "Profile type is required");
        this.name = Objects.requireNonNull(name, "Person name is required");
        this.phoneNumber = validatePhoneNumber(phoneNumber);
    }

    /**
     * Constructor with a CreateProfileCommand.
     *
     * @param command The {@link CreateProfileCommand} instance
     */
    public Profile(CreateProfileCommand command) {
        this(
                null,
                command.accountId(),
                command.profileType(),
                new PersonName(command.firstName(), command.lastName()),
                command.phoneNumber());
    }

    /**
     * Reconstitutes an existing profile from persistence state.
     *
     * @param id          The unique identifier.
     * @param accountId   The account that owns the profile.
     * @param profileType The type of the profile.
     * @param name        The name of the person.
     * @param phoneNumber The contact phone number.
     * @return The reconstituted Profile instance.
     */
    public static Profile reconstitute(Long id, AccountId accountId, ProfileType profileType,
                                       PersonName name, String phoneNumber) {
        if (id == null || id < 1) {
            throw new IllegalArgumentException("Invalid profile id");
        }
        return new Profile(id, accountId, profileType, name, phoneNumber);
    }

    /**
     * Updates the contact phone number.
     *
     * @param phoneNumber The new contact phone number.
     */
    public void updateContact(String phoneNumber) {
        this.phoneNumber = validatePhoneNumber(phoneNumber);
    }

    /**
     * Full name getter.
     *
     * @return Full name
     */
    public String getFullName() {
        return this.name.getFullName();
    }

    private static String validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number cannot be null or blank");
        }
        return phoneNumber;
    }
}
