package ru.productstar;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.productstar.dao.NamedJdbcContactDao;
import ru.productstar.service.ContactService;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        var applicationContext = new AnnotationConfigApplicationContext(ContactConfiguration.class);

        var contactDao = applicationContext.getBean(NamedJdbcContactDao.class);
        var contactService = applicationContext.getBean(ContactService.class);

        contactDao.deleteAllContacts();
        contactService.saveContacts(Path.of("src/main/resources/contacts.csv"));

        System.out.println(contactService.getContacts());
    }
}
