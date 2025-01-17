package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Guest;
import seedu.address.model.person.PassportNumber;
import seedu.address.model.person.Person;
import seedu.address.model.person.Staff;
import seedu.address.model.person.StaffId;
import seedu.address.model.person.UniqueIdentifier;

/**
 * Deletes a person identified using it's displayed index from the address book.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the person identified by the passport number or staff id used in the displayed person list.\n"
            + "Parameters: Passport Number or Staff ID\n"
            + "Example: " + COMMAND_WORD + " sid/211 or pn/A021231B";
    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted Person: %1$s";

    private final UniqueIdentifier uniqueIdentifier;

    public DeleteCommand(UniqueIdentifier uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        Person personToDelete;

        if (uniqueIdentifier instanceof StaffId) {
            personToDelete = lastShownList
                    .stream()
                    .filter(p -> p instanceof Staff && ((Staff) p).getStaffId().equals(uniqueIdentifier))
                    .findAny()
                    .orElse(null);
        } else if (uniqueIdentifier instanceof PassportNumber) {
            personToDelete = lastShownList
                    .stream()
                    .filter(g -> g instanceof Guest && ((Guest) g).getPassportNumber().equals(uniqueIdentifier))
                    .findAny()
                    .orElse(null);
        } else {
            personToDelete = null;
        }

        if (personToDelete == null) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_UNIQUE_IDENTIFIER);
        }

        model.deletePerson(personToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, personToDelete));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DeleteCommand // instanceof handles nulls
                && uniqueIdentifier.equals(((DeleteCommand) other).uniqueIdentifier)); // state check
    }
}
