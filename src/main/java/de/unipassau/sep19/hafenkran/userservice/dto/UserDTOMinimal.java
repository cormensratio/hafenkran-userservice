package de.unipassau.sep19.hafenkran.userservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.unipassau.sep19.hafenkran.userservice.model.User;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor(onConstructor = @__(@JsonCreator))
public class UserDTOMinimal {

    @NonNull
    @JsonProperty("id")
    private final UUID id;

    @NonNull
    @NotBlank
    @JsonProperty("name")
    private final String name;

    public static UserDTOMinimal fromMinimalUser(@NonNull User user) {
        return new UserDTOMinimal(user.getId(), user.getName());
    }

    public static List<UserDTOMinimal> convertMinimalUserListToDTOList(@NonNull List<User> userList) {
        return userList.stream()
                .map(UserDTOMinimal::fromMinimalUser).collect(Collectors.toList());
    }

}
