package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PASSPORT_NUMBER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ROOM_NUMBER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STAFF_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.Messages;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Guest;
import seedu.address.model.person.Name;
import seedu.address.model.person.PassportNumber;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.RoomNumber;
import seedu.address.model.person.Staff;
import seedu.address.model.person.StaffId;
import seedu.address.model.person.UniqueIdentifier;
import seedu.address.model.tag.Tag;

/**
 * Edits the details of an existing person in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the person identified "
            + "by the unique identifier of the person. "
            + "Existing values will be overwritten by the input values.\n"
            + "Format for Staff: "
            + "Parameters:  "
            + "[" + PREFIX_STAFF_ID + "STAFF_ID"
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " sid/123 "
            + PREFIX_EMAIL + "johndoe@example.com"
            + PREFIX_PHONE + "91234567 "
            + "Format for Guest: "
            + "Parameters:  "
            + "[" + PREFIX_PASSPORT_NUMBER + "PASSPORT_NUMBER"
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ROOM_NUMBER + "ROOM_NUMBER] "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " pn/A12345678 "
            + PREFIX_EMAIL + "johndoe@example.com"
            + PREFIX_ROOM_NUMBER + "1233"
            + PREFIX_TAG + "VIP";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited Person: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";

    // this will be a passport number/ staff id, depending on identity of person.
    private final UniqueIdentifier uniqueIdentifier;
    private final EditPersonDescriptor editPersonDescriptor;

    /**
     * @param uniqueIdentifier     of the person in the filtered person list to edit
     * @param editPersonDescriptor details to edit the person with
     */
    public EditCommand(UniqueIdentifier uniqueIdentifier, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(uniqueIdentifier);
        requireNonNull(editPersonDescriptor);

        this.uniqueIdentifier = uniqueIdentifier;
        this.editPersonDescriptor = editPersonDescriptor;
    }

    /**
     * Execute the edit command.
     *
     * @param model {@code Model} which the command should operate on.
     * @return CommandResult
     * @throws CommandException
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        Person personToEdit;

        // logic to decide whether this a staff or guest
        // will result in assertion failure later if person does not exist on list
        if (uniqueIdentifier instanceof StaffId) {
            personToEdit = lastShownList
                    .stream()
                    .filter(p -> p instanceof Staff && ((Staff) p).getStaffId().equals(uniqueIdentifier))
                    .findAny()
                    .orElse(null);
        } else {
            personToEdit = lastShownList
                    .stream()
                    .filter(p -> p instanceof Guest && ((Guest) p).getPassportNumber().equals(uniqueIdentifier))
                    .findAny()
                    .orElse(null);
        }

        if (personToEdit == null) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_UNIQUE_IDENTIFIER);
        }

        Person editedPerson = createEditedPerson(personToEdit, editPersonDescriptor);

        if (!personToEdit.isSamePerson(editedPerson) && model.hasPerson(editedPerson)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS, editedPerson));
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Person createEditedPerson(Person personToEdit, EditPersonDescriptor editPersonDescriptor) {
        assert personToEdit != null;

        Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        Email updatedEmail = editPersonDescriptor.getEmail().orElse(personToEdit.getEmail());
        Set<Tag> updatedTags = editPersonDescriptor.getTags().orElse(personToEdit.getTags());
        Phone updatedPhone;
        Address updatedAddress;
        RoomNumber updatedRoomNumber;
        StaffId updatedStaffId;
        PassportNumber updatedPassportNumber;

        if (personToEdit instanceof Staff) {
            Staff staff = (Staff) personToEdit;
            EditStaffDescriptor editStaffDescriptor = (EditStaffDescriptor) editPersonDescriptor;
            updatedPhone = editStaffDescriptor.getPhone().orElse(staff.getPhone());
            updatedAddress = editStaffDescriptor.getAddress().orElse(staff.getAddress());
            updatedStaffId = editStaffDescriptor.getStaffId().orElse(staff.getStaffId());
            return new Staff(updatedName, updatedEmail, updatedTags, updatedAddress, updatedStaffId, updatedPhone);
        } else {
            Guest guest = (Guest) personToEdit;
            EditGuestDescriptor editGuestDescriptor = (EditGuestDescriptor) editPersonDescriptor;
            updatedRoomNumber = editGuestDescriptor.getRoomNumber().orElse(guest.getRoomNumber());
            updatedPassportNumber = editGuestDescriptor.getPassportNumber().orElse(guest.getPassportNumber());
            return new Guest(updatedName, updatedEmail, updatedTags, updatedRoomNumber, updatedPassportNumber);
        }
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand)) {
            return false;
        }

        // state check
        EditCommand e = (EditCommand) other;
        return uniqueIdentifier.equals(e.uniqueIdentifier)
                && editPersonDescriptor.equals(e.editPersonDescriptor);
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public abstract static class EditPersonDescriptor {
        private Name name;
        private Email email;
        private Set<Tag> tags;

        public EditPersonDescriptor() {
        }

        /**
         * Creates a edit person descriptor instance.
         *
         * @param toCopy
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setEmail(toCopy.email);
            setTags(toCopy.tags);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, email, tags);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor)) {
                return false;
            }

            // state check
            EditPersonDescriptor e = (EditPersonDescriptor) other;

            return getName().equals(e.getName())
                    && getEmail().equals(e.getEmail())
                    && getTags().equals(e.getTags());
        }
    }

    /**
     * Stores the details to edit the guest with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public static class EditGuestDescriptor extends EditPersonDescriptor {
        private PassportNumber passportNumber;
        private RoomNumber roomNumber;

        public EditGuestDescriptor() {
            super();
        }

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditGuestDescriptor(EditGuestDescriptor toCopy) {
            super(toCopy);
            setPassportNumber(toCopy.passportNumber);
            setRoomNumber(toCopy.roomNumber);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(getName(), getEmail(), getTags(), roomNumber, passportNumber);
        }

        public void setPassportNumber(PassportNumber passportNumber) {
            this.passportNumber = passportNumber;
        }

        public Optional<PassportNumber> getPassportNumber() {
            return Optional.ofNullable(passportNumber);
        }

        public void setRoomNumber(RoomNumber roomNumber) {
            this.roomNumber = roomNumber;
        }

        public Optional<RoomNumber> getRoomNumber() {
            return Optional.ofNullable(roomNumber);
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditGuestDescriptor)) {
                return false;
            }

            // state check
            EditGuestDescriptor e = (EditGuestDescriptor) other;

            return super.equals(e)
                    && getPassportNumber().equals(e.getPassportNumber())
                    && getRoomNumber().equals(e.getRoomNumber());
        }
    }

    /**
     * Stores the details to edit the staff with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public static class EditStaffDescriptor extends EditPersonDescriptor {
        private Phone phone;
        private Address address;
        private StaffId staffId;

        public EditStaffDescriptor() {
            super();
        }

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditStaffDescriptor(EditStaffDescriptor toCopy) {
            super(toCopy);
            setPhone(toCopy.phone);
            setAddress(toCopy.address);
            setStaffId(toCopy.staffId);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(getName(), phone, getEmail(), address, getTags(), staffId);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        public void setStaffId(StaffId staffId) {
            this.staffId = staffId;
        }

        public Optional<StaffId> getStaffId() {
            return Optional.ofNullable(staffId);
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditStaffDescriptor)) {
                return false;
            }

            // state check
            EditStaffDescriptor e = (EditStaffDescriptor) other;

            return super.equals(e)
                    && getAddress().equals(e.getAddress())
                    && getPhone().equals(e.getPhone())
                    && getStaffId().equals(e.getStaffId());
        }
    }


}
