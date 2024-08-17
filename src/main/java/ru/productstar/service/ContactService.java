package ru.productstar.service;

import org.springframework.stereotype.Service;
import ru.productstar.dao.NamedJdbcContactDao;
import ru.productstar.model.Contact;

import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ContactService {

    private final Logger logger = LoggerFactory.getLogger(ContactService.class);

    private final NamedJdbcContactDao contactDao;
    private final ContactReader contactReader;

    public ContactService(NamedJdbcContactDao contactDao, ContactReader contactReader) {
        this.contactDao = contactDao;
        this.contactReader = contactReader;
    }

    public void saveContacts(Path filePath) {
        logger.info("Importing contacts from {}", filePath);
        var contacts = contactReader.readFromFile(filePath);
        logger.info("Contacts has been imported successfully, count: {}", contacts.size());
        contactDao.saveAllContacts(contacts);
    }

    public List<Contact> getContacts() {
        return contactDao.getAllContacts();
    }
}
