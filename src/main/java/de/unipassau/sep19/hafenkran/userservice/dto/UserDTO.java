package de.unipassau.sep19.hafenkran.userservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.unipassau.sep19.hafenkran.userservice.model.User;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor(onConstructor = @__(@JsonCreator))
public class UserDTO {

    @NonNull
    @JsonProperty("id")
    private final UUID id;

    @NonNull
    @NotBlank
    @JsonProperty("name")
    private final String name;

    @NonNull
    @JsonProperty("email")
    private final String email;

    @NonNull
    @JsonProperty("status")
    private final User.Status status;

    @Getter(onMethod = @__(@JsonProperty("isAdmin")))
    private final boolean isAdmin;

    public static UserDTO fromUser(@NonNull User user) {
        return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getStatus(), user.isAdmin());
    }

    public static List<UserDTO> convertUserListToDTOList(@NonNull List<User> userList) {
        return userList.stream()
                .map(UserDTO::fromUser).collect(Collectors.toList());
    }
}
