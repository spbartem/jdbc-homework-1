package ru.productstar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.productstar.dao.dto.ContactDto;
import ru.productstar.facade.ContactFacade;

import java.util.Collection;

@RestController
@RequestMapping("/contacts")
public class ContactController {

    private final ContactFacade contactFacade;

    @Autowired
    public ContactController(ContactFacade contactFacade) {
        this.contactFacade = contactFacade;
    }

    @GetMapping("/{contactId}")
    public ContactDto getContact(
            @PathVariable long contactId
    ) {
        return contactFacade.getContact(contactId);
    }

    @GetMapping("/")
    public Collection<ContactDto> getAllContact() {
        return contactFacade.getAllContacts();
    }
}
