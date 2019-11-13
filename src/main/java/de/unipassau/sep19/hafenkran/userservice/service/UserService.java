package de.unipassau.sep19.hafenkran.userservice.service;

import de.unipassau.sep19.hafenkran.userservice.dto.UserCreateDTO;
import de.unipassau.sep19.hafenkran.userservice.dto.UserDTO;
import de.unipassau.sep19.hafenkran.userservice.model.User;
import lombok.NonNull;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.validation.Valid;
import java.util.UUID;

/**
 * A {@link org.springframework.stereotype.Service} for interacting with user entities.
 */
public interface UserService extends UserDetailsService {
    /**
     * Retrieve the {@link UserDTO} for the user with the given userId.
     *
     * @param userId the userId used for the lookup of the user
     * @return the {@link UserDTO} representation of the corresponding user
     */
    UserDTO getUserDTOFromUserId(@NonNull UUID userId);

    /**
     * Creates and saves a new user.
     *
     * @param userCreateDTO the DTO used for creating the user.
     * @return the new {@link User} returned after saving to the database.
     */
    User registerNewUser(@NonNull @Valid UserCreateDTO userCreateDTO);

    /**
     * Retrieve the {@link UserDTO} for the user with the given username.
     *
     * @param username used for the lookup of the user
     * @return the {@link UserDTO} representation of the corresponding user
     */
    UserDTO getUserDTOFromUserName(@NonNull String username);

    /**
     * Fetches the currently active user represented as DTO.
     *
     * @return the {@link UserDTO} representation of the currently active user.
     */
    UserDTO getUserDTOForCurrentUser();

}
