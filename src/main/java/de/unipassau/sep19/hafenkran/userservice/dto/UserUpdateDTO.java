package de.unipassau.sep19.hafenkran.userservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.unipassau.sep19.hafenkran.userservice.model.User;
import de.unipassau.sep19.hafenkran.userservice.model.User.Status;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Optional;
import java.util.UUID;

@Data
@RequiredArgsConstructor(onConstructor = @__(@JsonCreator))
public class UserUpdateDTO {

    @NonNull
    @JsonProperty("password")
    private final Optional<String> password;

    @NonNull
    @JsonProperty("newPassword")
    private final Optional<String> newPassword;

    @NonNull
    @JsonProperty("email")
    private final Optional<String> email;

    @NonNull
    @JsonProperty("status")
    private final Optional<Status> status;

    @JsonProperty("isAdmin")
    private final Optional<Boolean> isAdmin;
}
