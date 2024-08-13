package ru.productstar.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import org.springframework.stereotype.Repository;
import ru.productstar.model.Contact;

import java.util.*;

@Repository
@Primary
public class NamedJdbcContactDao implements ContactDao {

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

    public NamedJdbcContactDao(NamedParameterJdbcTemplate namedJdbcTemplate) {
        this.namedJdbcTemplate = namedJdbcTemplate;
    }

    public long saveContact(Contact contact) {
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

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public void saveAllContacts(Collection<Contact> contacts) {
        var args = contacts.stream()
                .map(contact -> new MapSqlParameterSource()
                        .addValue("name", contact.getName())
                        .addValue("surname", contact.getSurname())
                        .addValue("email", contact.getEmail())
                        .addValue("phoneNumber", contact.getPhone()))
                .toArray(MapSqlParameterSource[]::new);

        namedJdbcTemplate.batchUpdate(SAVE_CONTACT_SQL, args);
    }

    public Contact getContact(long contactId) {
        return namedJdbcTemplate.queryForObject(
                GET_CONTACT_BY_ID_SQL,
                new MapSqlParameterSource("id", contactId),
                CONTACT_ROW_MAPPER
        );
    }

    public List<Contact> getAllContacts() {
        return namedJdbcTemplate.query(GET_ALL_CONTACTS_SQL, CONTACT_ROW_MAPPER);
    }

    public void deleteContact(long contactId) {
        namedJdbcTemplate.update(
                DELETE_CONTACT_SQL,
                new MapSqlParameterSource("id", contactId)
        );
    }

    public void deleteAllContacts() {
        namedJdbcTemplate.update(DELETE_ALL_CONTACTS_SQL, EmptySqlParameterSource.INSTANCE);
    }

    public void updatePhoneNumber(long contactId, String phoneNumber) {
        namedJdbcTemplate.update(
                UPDATE_PHONE_NUMBER_SQL,
                new MapSqlParameterSource("id", contactId).addValue("phoneNumber", phoneNumber)
        );
    }

    public void updateEmail(long contactId, String email) {
        namedJdbcTemplate.update(
                UPDATE_EMAIL_SQL,
                new MapSqlParameterSource("id", contactId).addValue("email", email)
        );
    }
}
