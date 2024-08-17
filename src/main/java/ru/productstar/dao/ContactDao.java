package ru.productstar.dao;

import ru.productstar.exception.EntityNotFoundException;
import ru.productstar.model.Contact;

import java.util.*;

public interface ContactDao {

    long saveContact(Contact contact);
    void saveAllContacts(Collection<Contact> contacts);

    Contact getContact(long contactId);
    List<Contact> getAllContacts();

    void deleteContact(long contactId) throws EntityNotFoundException;
    void deleteAllContacts();

    void updatePhoneNumber(long contactId, String phoneNumber);
    void updateEmail(long contactId, String email);

}
