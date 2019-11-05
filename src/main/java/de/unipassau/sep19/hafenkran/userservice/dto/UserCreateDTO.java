package de.unipassau.sep19.hafenkran.userservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@RequiredArgsConstructor(onConstructor = @__(@JsonCreator))
public class UserCreateDTO {
    @NonNull
    @NotEmpty
    @JsonProperty("username")
    private final String username;

    @NonNull
    @NotEmpty
    @JsonProperty("password")
    private final String password;

    @NonNull
    @JsonProperty("email")
    private final String email;

    @JsonProperty("isAdmin")
    private final boolean isAdmin;
}
