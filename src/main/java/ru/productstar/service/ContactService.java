package ru.productstar.service;

import org.springframework.stereotype.Service;
import ru.productstar.dao.NamedJdbcContactDao;
import ru.productstar.model.Contact;

import java.nio.file.Path;
import java.util.List;

@Service
public class ContactService {

    private final NamedJdbcContactDao contactDao;
    private final ContactReader contactReader;

    public ContactService(NamedJdbcContactDao contactDao, ContactReader contactReader) {
        this.contactDao = contactDao;
        this.contactReader = contactReader;
    }

    public void saveContacts(Path filePath) {
        var contacts = contactReader.readFromFile(filePath);
        contactDao.saveAllContacts(contacts);
    }

    public List<Contact> getContacts() {
        return contactDao.getAllContacts();
    }
}
