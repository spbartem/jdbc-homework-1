package ru.productstar.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import org.springframework.stereotype.Repository;
import ru.productstar.exception.EntityNotFoundException;
import ru.productstar.model.Contact;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Repository
@Primary
public class NamedJdbcContactDao implements ContactDao {

    private static final String RESTART_SEQUENCE_CONTACT_ID_SQL = "ALTER SEQUENCE %s RESTART WITH %d";

    private static final String GET_ALL_CONTACTS_SQL = "SELECT ID, NAME, SURNAME, EMAIL, PHONE_NUMBER FROM CONTACT";

    private static final String GET_CONTACT_BY_ID_SQL = GET_ALL_CONTACTS_SQL + " WHERE ID = :id";

    private static final String SAVE_CONTACT_SQL = "INSERT INTO CONTACT (NAME, SURNAME, EMAIL, PHONE_NUMBER) " +
            "VALUES (:name, :surname, :email, :phoneNumber)";

    private static final String UPDATE_EMAIL_SQL = "UPDATE CONTACT SET EMAIL = :email WHERE ID = :id";

    private static final String UPDATE_PHONE_NUMBER_SQL = "UPDATE CONTACT SET PHONE_NUMBER = :phoneNumber " +
            "WHERE ID = :id";

    private static final String DELETE_CONTACT_SQL = "DELETE FROM CONTACT WHERE ID = :id";

    private static final String DELETE_ALL_CONTACTS_SQL = "DELETE FROM CONTACT WHERE ID between 1 " +
            "and (select max(id) from CONTACT)";

    private static final RowMapper<Contact> CONTACT_ROW_MAPPER =
            (rs, i) -> new Contact(
                    rs.getLong("ID"),
                    rs.getString("NAME"),
                    rs.getString("SURNAME"),
                    rs.getString("EMAIL"),
                    rs.getString("PHONE_NUMBER")
            );

    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(NamedJdbcContactDao.class);

    public NamedJdbcContactDao(NamedParameterJdbcTemplate namedJdbcTemplate) {
        this.namedJdbcTemplate = namedJdbcTemplate;
    }

    public void setInitialValueForSequence(String sequenceName, long initialValue) {
        logger.info("Setting initial value for sequence {}: {}", sequenceName, initialValue);
        namedJdbcTemplate.getJdbcTemplate().execute(String.format(RESTART_SEQUENCE_CONTACT_ID_SQL, sequenceName, initialValue));
    }

    public long saveContact(Contact contact) {
        logger.info("Saving contact: {}", contact);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        var args = new MapSqlParameterSource()
                .addValue("name", contact.getName())
                .addValue("surname", contact.getSurname())
                .addValue("email", contact.getEmail())
                .addValue("phoneNumber", contact.getPhone());

        namedJdbcTemplate.update(
                SAVE_CONTACT_SQL,
                args,
                keyHolder,
                new String[] { "id" }
        );

        long savedId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        logger.info("Contact saved with ID: {}", savedId);
        return savedId;
    }

    public void saveAllContacts(Collection<Contact> contacts) {
        logger.info("Saving all contacts: {}", contacts);
        var args = contacts.stream()
                .map(contact -> new MapSqlParameterSource()
                        .addValue("name", contact.getName())
                        .addValue("surname", contact.getSurname())
                        .addValue("email", contact.getEmail())
                        .addValue("phoneNumber", contact.getPhone()))
                .toArray(MapSqlParameterSource[]::new);

        namedJdbcTemplate.batchUpdate(SAVE_CONTACT_SQL, args);
        logger.info("All contacts ({}) have been saved successfully", contacts.size());
    }

    public Contact getContact(long contactId) {
        try {
            return namedJdbcTemplate.queryForObject(
                    GET_CONTACT_BY_ID_SQL,
                    new MapSqlParameterSource("id", contactId),
                    CONTACT_ROW_MAPPER);
        } catch (EmptyResultDataAccessException ignored) {
            return new Contact(contactId, "Not found", "Not found", "Not found", "Not found");
        }
    }

    public List<Contact> getAllContacts() {
        return namedJdbcTemplate.query(GET_ALL_CONTACTS_SQL, CONTACT_ROW_MAPPER);
    }

    public void deleteContact(long contactId) throws EntityNotFoundException {
        logger.info("Deleting contact with ID: {}", contactId);
        int rowAffected = namedJdbcTemplate.update(
                DELETE_CONTACT_SQL,
                new MapSqlParameterSource("id", contactId)
        );

        if (rowAffected == 0) {
            logger.warn("Contact with ID {} not found", contactId);
            throw new EntityNotFoundException("Contact with ID " + contactId + " not found");
        }

        logger.info("Contact with ID {} has been deleted", contactId);
    }

    public void deleteAllContacts() {
        namedJdbcTemplate.update(DELETE_ALL_CONTACTS_SQL, EmptySqlParameterSource.INSTANCE);
        logger.info("All contacts from the CONTACTS table have been deleted.");
    }

    public void updatePhoneNumber(long contactId, String phoneNumber) {
        String oldPhone = getContact(contactId).getPhone();
        namedJdbcTemplate.update(
                UPDATE_PHONE_NUMBER_SQL,
                new MapSqlParameterSource("id", contactId).addValue("phoneNumber", phoneNumber)
        );
        logger.info("For the contact with the Id {}, the phone number has been updated from {} to {}", contactId, oldPhone, phoneNumber);
    }

    public void updateEmail(long contactId, String email) {
        String oldEmail = getContact(contactId).getEmail();
        namedJdbcTemplate.update(
                UPDATE_EMAIL_SQL,
                new MapSqlParameterSource("id", contactId).addValue("email", email)
        );
        logger.info("For the contact with the Id {}, the email has been updated from {} to {}", contactId, oldEmail, email);
    }
}
