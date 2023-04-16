package dev.themartian.keycloak.activation;/*
My File Header
*/

import lombok.NonNull;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.PreparedBatch;

import javax.sql.DataSource;

import static java.util.Arrays.stream;

public class ActivationCodeDao {

    @NonNull
    private final Jdbi jdbi;

    private static final String INSERT_ACTIVATION_CODE = "insert into activation_code " +
                                                         "(code, codeGroup, grantedRole, created, validUntil, redemption, redemptionBy) " +
                                                         "values(:code, :codeGroup, :grantedRole, :created, :validUntil, :redemption, :redemptionBy)";

    public ActivationCodeDao(@NonNull DataSource dataSource) {
        jdbi = Jdbi.create(dataSource);
    }
    public int insert(Iterable<ActivationCode> activationCodes) {
        try (Handle handle = jdbi.open()) {
            PreparedBatch batch = handle.prepareBatch(INSERT_ACTIVATION_CODE);
            activationCodes.forEach(a -> batch.bindMethods(a).add());
            return stream(batch.execute()).sum();
        }
    }
}
