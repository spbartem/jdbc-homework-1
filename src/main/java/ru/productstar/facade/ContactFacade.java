package ru.productstar.facade;

import org.springframework.stereotype.Service;
import ru.productstar.dao.NamedJdbcContactDao;
import ru.productstar.dao.dto.ContactDto;

import java.util.Collection;

@Service
public class ContactFacade {

    private final NamedJdbcContactDao contactDao;

    public ContactFacade(NamedJdbcContactDao contactDao) {
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
