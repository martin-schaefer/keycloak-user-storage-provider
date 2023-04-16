package dev.themartian.keycloak.activation;

import static dev.themartian.keycloak.activation.ActivationCode.builder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.themartian.keycloak.storage.ConnectionProperties;
import org.junit.jupiter.api.*;

import org.mockito.Mockito;
import org.postgresql.ds.PGPoolingDataSource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ActivationCodeDaoTest {

    @Container
    private static final JdbcDatabaseContainer CONTAINER = new PostgreSQLContainer().withInitScript("init.sql");

    private static ActivationCodeDao activationCodeDao;

    @BeforeAll
    static void setup() {
        String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", CONTAINER.getHost(), CONTAINER.getFirstMappedPort(), CONTAINER.getDatabaseName());
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(CONTAINER.getUsername());
        config.setPassword(CONTAINER.getPassword());
        activationCodeDao = new ActivationCodeDao(new HikariDataSource(config));
    }

    /**
     * Method under test: {@link ActivationCodeDao#ActivationCodeDao(DataSource)}
     */
    @Test
    @Order(0)
    void insert() {
        // given
        int size = 100;
        ActivationCodeGenerator activationCodeGenerator = new ActivationCodeGenerator();
        List<ActivationCode> activationCodes = new ArrayList<>(size);
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < size; i++) {
            activationCodes.add(
                    builder()
                            .codeGroup("ABC-2023").recipient("Q700" + (i % 10)).code(activationCodeGenerator.generate()).grantedRole("SOME_ROLE:ID1234567:2023-12-31")
                            .created(now).validUntil(LocalDate.of(2023, 02, 28)).build());
        }

        // when
        int count = activationCodeDao.insert(activationCodes);

        // then
        assertThat(count).isEqualTo(size);
    }

    @Test
    @Order(1)
    void findRecipients() {
        // given
        String codeGroup = "ABC-2023";

        // when
        List<String> recipients = activationCodeDao.findRecipients(codeGroup);

        // then
        assertThat(recipients).hasSize(10);
    }

}
