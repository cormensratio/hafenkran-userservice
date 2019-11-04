package de.unipassau.sep19.hafenkran.userservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
@RequiredArgsConstructor(onConstructor = @__(@JsonCreator))
public class AuthResponseDTO implements Serializable {
    @NonNull
    @NotEmpty
    @JsonProperty("jwtToken")
    private final String jwtToken;
}