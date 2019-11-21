package de.unipassau.sep19.hafenkran.userservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("username")
    private final String username;

    @NonNull
    @NotBlank
    @JsonProperty("password")
    private final String password;
}