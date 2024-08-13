package ru.productstar.dao;

import ru.productstar.model.Contact;

import java.util.Collection;
import java.util.List;

public interface ContactDao {

    long saveContact(Contact contact);
    void saveAllContacts(Collection<Contact> contacts);

    Contact getContact(long contactId);
    List<Contact> getAllContacts();

    void deleteContact(long contactId);
    void deleteAllContacts();

    void updatePhoneNumber(long contactId, String phoneNumber);
    void updateEmail(long contactId, String email);

}
