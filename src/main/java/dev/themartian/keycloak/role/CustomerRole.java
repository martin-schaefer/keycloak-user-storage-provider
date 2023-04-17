package dev.themartian.keycloak.role;/*
My File Header
*/

import lombok.Builder;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.Set;
import java.util.regex.Pattern;

@Builder
public record CustomerRole(@NonNull String name, @NonNull Set<String> customerNumbers, LocalDate validUntil) {

    private static final Pattern ROLE_PATTERN = Pattern.compile("([A-Z,_]{3,}):([A-Z]\\d{7},)*([A-Z]\\d{7})(:\\d{4}.\\d{2}.\\d{2})?");

    /**
     * @return
     */
    @Override
    public String toString() {
        String formatted = name + ":" +String.join(",", customerNumbers);
        if(validUntil != null) {
            formatted += ":" + validUntil;
        }
        return formatted;
    }

    public static boolean isValid(@NonNull String role) {
        return ROLE_PATTERN.matcher(role).matches();
    }
}
