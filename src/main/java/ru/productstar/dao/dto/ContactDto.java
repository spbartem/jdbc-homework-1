package ru.productstar.dao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.productstar.model.Contact;

public class ContactDto {
    @JsonProperty("contactId")
    private final long contactId;
    @JsonProperty("firstName")
    private final String firstName;
    @JsonProperty("lastName")
    private final String lastName;
    @JsonProperty("phoneNumber")
    private final String phoneNumber;
    @JsonProperty("email")
    private final String email;

    public ContactDto(Contact contact) {
        this.contactId = contact.getId();
        this.firstName = contact.getName();
        this.lastName = contact.getSurname();
        this.phoneNumber = contact.getPhone();
        this.email = contact.getEmail();
    }

    public long getContactId() {
        return contactId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }
}
