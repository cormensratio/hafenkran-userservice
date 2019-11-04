package de.unipassau.sep19.hafenkran.userservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.UUID;

@Data
@RequiredArgsConstructor(onConstructor = @__(@JsonCreator))
public class UserDTO {
    @NonNull
    @NotEmpty
    @JsonProperty("username")
    private final String username;

    @NonNull
    @NotEmpty
    @JsonProperty("userId")
    private final UUID userId;

    @NonNull
    @NotEmpty
    @JsonProperty("email")
    private final String email;

    @JsonProperty("isAdmin")
    private final boolean isAdmin;
}
