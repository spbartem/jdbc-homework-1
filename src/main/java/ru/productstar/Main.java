package ru.productstar;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.productstar.dao.NamedJdbcContactDao;
import ru.productstar.model.Contact;
import ru.productstar.service.ContactService;


import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        var applicationContext = new AnnotationConfigApplicationContext(ContactConfiguration.class);

        var contactDao = applicationContext.getBean(NamedJdbcContactDao.class);
        var contactService = applicationContext.getBean(ContactService.class);


        contactDao.setInitialValueForSequence("contact_id_seq", 1);
        contactDao.deleteAllContacts();

        contactService.saveContacts(Path.of("src/main/resources/contacts.csv"));

        contactDao.updatePhoneNumber(1, "+79119110011");
        contactDao.updateEmail(1, "petr_petrov@gmail.com");

        contactDao.saveContact(new Contact("Steven", "King", "kingofhorror@gmail.com", "+155512345666"));
    }
}
