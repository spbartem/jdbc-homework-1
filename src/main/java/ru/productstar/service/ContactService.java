package ru.productstar.service;

import ru.productstar.dao.ContactDao;
import ru.productstar.model.Contact;

import java.nio.file.Path;
import java.util.List;

public class ContactService {

    private final ContactDao contactDao;
    private final ContactReader contactReader;

    public ContactService(ContactDao contactDao, ContactReader contactReader) {
        this.contactDao = contactDao;
        this.contactReader = contactReader;
    }

    public void saveContacts(Path filePath) {
        var contacts = contactReader.readFromFile(filePath);
        contactDao.saveAll(contacts);
    }

    public List<Contact> getContacts() {
        return contactDao.getAllContacts();
    }
}
