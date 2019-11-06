package de.unipassau.sep19.hafenkran.userservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.unipassau.sep19.hafenkran.userservice.model.User;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.UUID;

@Data
@RequiredArgsConstructor(onConstructor = @__(@JsonCreator))
public class UserDTO {
    @NonNull
    @NotEmpty
    @JsonProperty("userId")
    private final UUID userId;

    @NonNull
    @NotEmpty
    @JsonProperty("username")
    private final String username;

    @NonNull
    @NotEmpty
    @JsonProperty("email")
    private final String email;

    @Getter(onMethod = @__(@JsonProperty("isAdmin")))
    private final boolean isAdmin;

    public static UserDTO fromUser(User user) {
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.isAdmin());
    }
}
