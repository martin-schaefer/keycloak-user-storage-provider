package dev.themartian.keycloak.activation;/*
My File Header
*/

import java.util.Random;

public class ActivationCodeGenerator {

    public static final int CODE_LENGTH = 12;

    private static final char[] CHARS = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z'
    };
    public final Random random = new Random();

    public String generate() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARS[random.nextInt(CHARS.length)]);
        }
        return code.toString();
    }
}
