package de.unipassau.sep19.hafenkran.userservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.unipassau.sep19.hafenkran.userservice.model.User;
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
    @NotBlank
    @JsonProperty("password")
    private final String password;

    @NonNull
    @JsonProperty("newPassword")
    private final String newPassword;

    @NonNull
    @JsonProperty("email")
    private final String email;

    @NonNull
    @JsonProperty("status")
    private final User.Status status;

    @JsonProperty("isAdmin")
    private final boolean isAdmin;
}
