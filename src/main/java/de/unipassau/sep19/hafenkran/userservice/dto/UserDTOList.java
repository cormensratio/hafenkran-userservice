package de.unipassau.sep19.hafenkran.userservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.unipassau.sep19.hafenkran.userservice.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotEmpty;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The Data Transfer Object (DTO) representation of a list of {@link User}s.
 */
@Data
@AllArgsConstructor(onConstructor = @__(@JsonCreator))
public class UserDTOList {

    @NonNull
    @JsonProperty("numberOfUsers")
    private final int numberOfUsers;

    @NonNull
    @JsonProperty("users")
    private final List<UserDTO> users;

    @JsonCreator
    public UserDTOList(@NonNull List<User> userList) {
        if (userList.isEmpty()) {
            this.numberOfUsers = 0;
            this.users = Collections.emptyList();
        } else {
            this.numberOfUsers = userList.size();
            this.users = convertUserListToDTOList(userList);
        }
    }

    /**
     * Converts a list of {@link User}s into a {@link UserDTOList}.
     *
     * @param userList The list of {@link User}s that is going to be converted.
     * @return The converted {@link UserDTOList}.
     */
    public static List<UserDTO> convertUserListToDTOList(
            @NonNull @NotEmpty List<User> userList) {

        return userList.stream()
                .map(UserDTO::fromUser).collect(Collectors.toList());
    }

}
