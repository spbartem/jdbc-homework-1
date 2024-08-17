package ru.productstar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.productstar.dao.NamedJdbcContactDao;
import ru.productstar.exception.EntityNotFoundException;
import ru.productstar.model.Contact;
import ru.productstar.service.ContactService;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link NamedJdbcContactDao}.
 * <p>
 * Аннотация @Sql подтягивает SQL-скрипт contact.sql, который будет применен к базе перед выполнением теста.
 * Contact.sql создает таблицу CONTACT с полями (ID, NAME, SURNAME, EMAIL, PHONE_NUMBER) и вставляет в нее 2 записи.
 * <p>
 * Тесты проверяют корректность реализации ContactDao.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ContactConfiguration.class)
// @Sql("classpath:contact.sql")
public record ContactDaoTests(@Autowired NamedJdbcContactDao contactDao,
                              @Autowired ContactService contactService) {

    private static final Contact IVAN = new Contact(
            1000L, "Ivan", "Ivanov", "iivanov@gmail.com", "1234567"
    );

    private static final Contact MARIA = new Contact(
            2000L, "Maria", "Ivanova", "mivanova@gmail.com", "7654321"
    );

    private static final Contact PETR = new Contact(
            1L,"Пётр", "Петров", "ppetrov@gmail.com", "+70987654321"
    );

    private static final Contact JOHN = new Contact(
            2L, "Джон", "Смидт", "jsmith@gmail.com", "+11234567890"
    );

    private static final Contact SEMION = new Contact(
            3L,"Семён", "Семёнов", "ssemenov@gmail.com", "+71234567890"
    );

    private static final Contact NOT_FOUND = new Contact(
            1L, "Not found", "Not found", "Not found", "Not found");

    /**
     * There are two contacts inserted in the database in contact.sql.
     * There are three contacts inserted in the database from contacts.csv.
     */
    private static final List<Contact> PERSISTED_CONTACTS = List.of(PETR, JOHN, SEMION);

    @BeforeEach
    void setUp() {
        contactDao.deleteAllContacts();
        contactDao.setInitialValueForSequence("contact_id_seq", 1);
    }

    @Test
    void saveContacts() {
        contactService.saveContacts(Path.of("src/main/resources/contacts.csv"));
        var contacts = contactDao.getAllContacts();
        assertThat(contacts).containsAll(PERSISTED_CONTACTS);
    }

    @Test
    void addContact() {
        var contact = new Contact("Jackie", "Chan", "jchan@gmail.com", "1234567890");
        var contactId = contactDao.saveContact(contact);
        contact.setId(contactId);

        var contactInDb = contactDao.getContact(contactId);

        assertThat(contactInDb).isEqualTo(contact);
    }

    @Test
    void getContact() {
        contactService.saveContacts(Path.of("src/main/resources/contacts.csv"));
        var contact = contactDao.getContact(PETR.getId());
        assertThat(contact).isEqualTo(PETR);
    }

    @Test
    void getAllContacts() {
        contactService.saveContacts(Path.of("src/main/resources/contacts.csv"));
        var contacts = contactDao.getAllContacts();

        assertThat(contacts).containsAll(PERSISTED_CONTACTS);
    }

    @Test
    void updatePhoneNumber() {
        var contact = new Contact("Jekyll", "Hide", "jhide@gmail.com", "");
        var contactId = contactDao.saveContact(contact);

        var newPhone = "777-77-77";
        contactDao.updatePhoneNumber(contactId, newPhone);

        var updatedContact = contactDao.getContact(contactId);
        assertThat(updatedContact.getPhone()).isEqualTo(newPhone);
    }

    @Test
    void updateEmail() {
        var contact = new Contact("Captain", "America", "", "");
        var contactId = contactDao.saveContact(contact);

        var newEmail = "cap@gmail.com";
        contactDao.updateEmail(contactId, newEmail);

        var updatedContact = contactDao.getContact(contactId);
        assertThat(updatedContact.getEmail()).isEqualTo(newEmail);
    }

    @Test
    void deleteContact() throws EntityNotFoundException {
        var contact = new Contact("To be", "Deleted", "", "");
        var contactId = contactDao.saveContact(contact);

        contactDao.deleteContact(contactId);

        var deletedContact = contactDao.getContact(contactId);
        assertThat(deletedContact).isEqualTo(NOT_FOUND);

//        assertThatThrownBy(() -> contactDao.getContact(contactId))
//                .isInstanceOf(EmptyResultDataAccessException.class);
    }
}
