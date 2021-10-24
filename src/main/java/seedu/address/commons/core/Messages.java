package seedu.address.commons.core;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format! \n%1$s";
    public static final String MESSAGE_INVALID_GUEST_PASSPORT_NUMBER =
            "The passport number of the guest is invalid!";
    public static final String MESSAGE_INVALID_VENDORID =
            "The vendor id of the vendor is invalid!";
    public static final String MESSAGE_INVALID_MULTIPLE_UNIQUE_IDENTIFIER =
            "Only one unique identifier should be provided!";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$d persons listed!";
    public static final String MESSAGE_MISSING_ARGUMENTS =
            "You are missing arguments in your command. Please follow the command format!";
    public static final String MESSAGE_TOO_MANY_ARGUMENTS = "Please only specify one argument";
}
