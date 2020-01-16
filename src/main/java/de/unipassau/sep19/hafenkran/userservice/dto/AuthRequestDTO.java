package de.unipassau.sep19.hafenkran.userservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.unipassau.sep19.hafenkran.userservice.model.User;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@RequiredArgsConstructor(onConstructor = @__(@JsonCreator))
public class AuthRequestDTO implements Serializable {

    @NonNull
    @NotBlank
    @JsonProperty("name")
    private final String name;

    @NonNull
    @NotBlank
    @JsonProperty("password")
    private final String password;

    @NonNull
    @NotBlank
    @JsonProperty("status")
    private final User.Status status;
}