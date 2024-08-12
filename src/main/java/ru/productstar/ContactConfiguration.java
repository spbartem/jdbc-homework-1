package ru.productstar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.productstar.common.JdbcConfig;
import ru.productstar.common.PropertiesConfiguration;
import ru.productstar.dao.ContactDao;
import ru.productstar.service.ContactReader;
import ru.productstar.service.ContactService;

@Configuration
@Import({JdbcConfig.class, PropertiesConfiguration.class})
public class ContactConfiguration {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public ContactConfiguration(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Bean
    public ContactDao contactDao() {
        return new ContactDao(namedParameterJdbcTemplate);
    }

    @Bean
    public ContactReader contactParser() {
        return new ContactReader();
    }

    @Bean
    public ContactService contactService() {
        return new ContactService(
                contactDao(),
                contactParser()
        );
    }
}
