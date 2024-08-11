package ru.productstar.facade;

import org.springframework.stereotype.Service;
import ru.productstar.dao.ContactDao;
import ru.productstar.dao.dto.ContactDto;
import ru.productstar.model.Contact;

import java.util.Collection;

@Service
public class ContactFacade {

    private final ContactDao contactDao;

    public ContactFacade(ContactDao contactDao) {
        this.contactDao = contactDao;
    }

    public ContactDto getContact(long contactId) {

        var contact = contactDao.getContact(contactId);
        return new ContactDto(contact);
    }

    public Collection<ContactDto> getAllContacts() {

        return contactDao.getAllContacts().stream()
                .map(ContactDto::new)
                .toList();
    }
}
