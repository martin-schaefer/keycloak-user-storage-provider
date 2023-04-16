package dev.themartian.keycloak.activation;/*
My File Header
*/

import lombok.NonNull;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.PreparedBatch;

import javax.sql.DataSource;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.stream;

public class ActivationCodeDao {

    @NonNull
    private final Jdbi jdbi;

    private static final String INSERT_ACTIVATION_CODE = "insert into activation_code " +
                                                         "(code, codeGroup, recipient, grantedRole, created, validUntil, redemption, redemptionBy) " +
                                                         "values(:code, :codeGroup, :recipient, :grantedRole, :created, :validUntil, :redemption, :redemptionBy)";

    private static final String FIND_RECIPIENTS_BY_CODE_GROUP = "select distinct recipient from activation_code " +
                                                                "where codeGroup = ?";

    public ActivationCodeDao(@NonNull DataSource dataSource) {
        jdbi = Jdbi.create(dataSource);
    }

    public int insert(@NonNull Iterable<ActivationCode> activationCodes) {
        try (Handle handle = jdbi.open()) {
            PreparedBatch batch = handle.prepareBatch(INSERT_ACTIVATION_CODE);
            activationCodes.forEach(a -> batch.bindMethods(a).add());
            return stream(batch.execute()).sum();
        }
    }

    public List<String> findRecipients(@NonNull String codeGroup) {
        try (Handle handle = jdbi.open()) {
            return handle.select(FIND_RECIPIENTS_BY_CODE_GROUP, codeGroup).mapTo(String.class).stream().toList();
        }
    }
}
