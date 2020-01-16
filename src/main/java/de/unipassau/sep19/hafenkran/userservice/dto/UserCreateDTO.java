package de.unipassau.sep19.hafenkran.userservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@RequiredArgsConstructor(onConstructor = @__(@JsonCreator))
public class UserCreateDTO {

    @NonNull
    @NotBlank
    @Pattern(regexp = "[a-z0-9]([-a-z0-9]*[a-z0-9])?")
    @JsonProperty("name")
    private final String name;

    @NonNull
    @NotBlank
    @Size(min = 8)
    @JsonProperty("password")
    private final String password;

    @NonNull
    @JsonProperty("email")
    private final String email;
}
