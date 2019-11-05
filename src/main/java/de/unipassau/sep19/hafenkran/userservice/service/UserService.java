package de.unipassau.sep19.hafenkran.userservice.service;

import de.unipassau.sep19.hafenkran.userservice.dto.UserCreateDTO;
import de.unipassau.sep19.hafenkran.userservice.dto.UserDTO;
import de.unipassau.sep19.hafenkran.userservice.model.User;
import lombok.NonNull;

import javax.validation.Valid;
import java.util.UUID;

public interface UserService {
    /**
     *
     */
    UserDTO getUserDTOFromUserId(@NonNull UUID userId);

    /**
     * Fetches the currently active user represented as DTO.
     *
     * @return the {@link UserDTO} representation of the currently active user.
     */
    UserDTO getUserDTOForCurrentUser();

    /**
     * Creates and saves a new user.
     *
     * @param userCreateDTO the DTO used for creating the user.
     * @return the new {@link User} returned after saving to the database.
     */
    User registerNewUser(@NonNull @Valid UserCreateDTO userCreateDTO);
}
