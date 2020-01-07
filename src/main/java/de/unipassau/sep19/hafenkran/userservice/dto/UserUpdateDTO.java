package de.unipassau.sep19.hafenkran.userservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Data
@RequiredArgsConstructor(onConstructor = @__(@JsonCreator))
public class UserUpdateDTO {

    @NonNull
    @JsonProperty("id")
    private final UUID id;

    @NonNull
    @JsonProperty("password")
    private final String password;

    @NonNull
    @NotBlank
    @JsonProperty("newPassword")
    private final String newPassword;

    @NonNull
    @NotBlank
    @JsonProperty("email")
    private final String email;

    @JsonProperty("isAdmin")
    private final boolean isAdmin;
}
