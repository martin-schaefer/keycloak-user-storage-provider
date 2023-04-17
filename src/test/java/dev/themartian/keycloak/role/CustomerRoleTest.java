package dev.themartian.keycloak.role;

import static dev.themartian.keycloak.role.CustomerRole.builder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class CustomerRoleTest {

    /**
     * Method under test: {@link CustomerRole#isValid(String)}
     */
    @ParameterizedTest(name = "Role {0} is valid")
    @ValueSource(strings = {
            "CUSTOMER:G1234567",
            "CUSTOMER_BUSINESS:G1234567,G1244887",
            "CUSTOMER_PRIVATE:G1234567,G1244887,K4444887",
            "CUSTOMER_PRIVATE:B1235467:2023-12-31",
            "APPLICATION_ISSUER:B1235467,G1234567,G1244887,K4444887:2022-03-06"
    })
    void isValid(String role) {
        // given: role

        // when
        boolean valid = CustomerRole.isValid(role);

        // then
        assertThat(valid).isTrue();
    }

    /**
     * Method under test: {@link CustomerRole#isValid(String)}
     */
    @ParameterizedTest(name = "Role {0} is invalid")
    @ValueSource(strings = {
            "CUSTOMER",
            "CUSTOMeR_BUSINESS:G1234567",
            "CUSTOMER_PRIVATE:G1234567,",
            "CUSTOMER_PRIVATE:B1235467:2023-12-1",
            "CUSTOMER_PRIVATE:2023-12-1",
            "APPLICATION_ISSUER:BA235467",
            "APPLICATION_ISSUER:B235467",
            "APPLICATION_ISSUER:B23546789",
            "APPLICATION_ISSUER:a1235467",
    })
    void isNotValid(String role) {
        // given: role

        // when
        boolean valid = CustomerRole.isValid(role);

        // then
        assertThat(valid).isFalse();
    }

    /**
     * Method under test: {@link CustomerRole#isValid(String)}
     */
    /**
     * Method under test: {@link CustomerRole#toString()}
     */
    @ParameterizedTest(name = "String representation of role with name: {0}, customerNumbers: {1}, validUntil: {2} must be {3}")
    @MethodSource("roles")
    void toStringRepresentation(String name, Set<String> customerNumbers, LocalDate validUntil, String expectedRole) {
        // given
        CustomerRole customerRole = builder().name(name).customerNumbers(customerNumbers).validUntil(validUntil).build();

        // when
        String role = customerRole.toString();

        // then
        assertThat(role).isEqualTo(expectedRole);
    }

    static Stream<Arguments> roles() {
        return Stream.of(
                Arguments.of("CUSTOMER_PRIVATE", Set.of("G1234567"), null, "CUSTOMER_PRIVATE:G1234567"),
                Arguments.of("CUSTOMER_PRIVATE", Set.of("B2224567"), LocalDate.of(2023,12,31), "CUSTOMER_PRIVATE:B2224567:2023-12-31")
        );
    }
}
