package dev.themartian.keycloak.activation;

import lombok.Builder;
import lombok.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record ActivationCode(@NonNull String code,
                             @NonNull String codeGroup,
                             @NonNull String grantedRole,
                             @NonNull LocalDateTime created,
                             @NonNull LocalDate validUntil,
                             LocalDateTime redemption,
                             String redemptionBy) {
}
