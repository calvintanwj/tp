package seedu.address.model.person;

import java.util.Objects;
import java.util.Set;

import seedu.address.model.tag.Tag;

public class Staff extends Person {

    private final Address address;
    private final StaffId staffId;
    private final Phone phone;

    /**
     * Every field must be present and not null.
     *
     * @param name
     * @param email
     * @param tags
     */
    public Staff(Name name, Email email, Set<Tag> tags, Address address, StaffId staffId, Phone phone) {
        super(name, email, tags);
        this.address = address;
        this.staffId = staffId;
        this.phone = phone;
    }

    public Phone getPhone() {
        return phone;
    }

    public StaffId getStaffId() {
        return staffId;
    }

    public Address getAddress() {
        return address;
    }

    @Override
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        if (otherPerson instanceof Staff) {
            Staff otherStaff = (Staff) otherPerson;
            return otherStaff.getStaffId().equals(getStaffId());
        }

        return false;
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Staff)) {
            return false;
        }

        Staff otherPerson = (Staff) other;
        return otherPerson.getName().equals(getName())
                && otherPerson.getEmail().equals(getEmail())
                && otherPerson.getAddress().equals(getAddress())
                && otherPerson.getPhone().equals(getPhone())
                && otherPerson.getStaffId().equals(getStaffId())
                && otherPerson.getTags().equals(getTags());
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(getName(), getEmail(), getTags(), address, staffId, phone);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getName())
                .append("; Email: ")
                .append(getEmail())
                .append("; Staff ID: ")
                .append(getStaffId())
                .append("; Phone: ")
                .append(getPhone())
                .append("; Address: ")
                .append(getAddress());

        Set<Tag> tags = getTags();
        if (!tags.isEmpty()) {
            builder.append("; Tags: ");
            tags.forEach(builder::append);
        }
        return builder.toString();
    }

}
