package dev.themartian.keycloak.action;

import dev.themartian.keycloak.action.ActivationCodeGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static java.lang.Character.*;
import static org.assertj.core.api.Assertions.assertThat;

class ActivationCodeGeneratorTest {
    /**
     * Method under test: {@link ActivationCodeGenerator#generate()}
     */
    @Test
    void generate() {
        // given
        int iterations = 10000000;
        Set<String> codes = new HashSet<>(iterations);

        // when
        ActivationCodeGenerator activationCodeGenerator = new ActivationCodeGenerator();
        for (int i = 0; i < iterations; i++) {
            String code = activationCodeGenerator.generate();
            assertThat(code).hasSize(ActivationCodeGenerator.CODE_LENGTH);
            for(char c : code.toCharArray()) {
                assertThat(isDigit(c) || (isLetter(c) && isUpperCase(c))).isTrue();
            }
            codes.add(code);
        }
        assertThat(codes).hasSize(iterations);

    }
}
