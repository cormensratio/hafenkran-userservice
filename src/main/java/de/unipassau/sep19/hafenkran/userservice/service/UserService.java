package de.unipassau.sep19.hafenkran.userservice.service;

import de.unipassau.sep19.hafenkran.userservice.dto.UserCreateDTO;
import de.unipassau.sep19.hafenkran.userservice.dto.UserDTO;
import de.unipassau.sep19.hafenkran.userservice.dto.UserDTOMinimal;
import de.unipassau.sep19.hafenkran.userservice.dto.UserUpdateDTO;
import de.unipassau.sep19.hafenkran.userservice.model.User;
import lombok.NonNull;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.UUID;

/**
 * A {@link org.springframework.stereotype.Service} for interacting with user entities.
 */
public interface UserService extends UserDetailsService {

    /**
     * Retrieves the {@link UserDTO}s with the name, id, email and adminflag of all users or from the users with
     * the specific {@code ids}.
     *
     * @param ids The ids to get the UserDTO from.
     * @return An List of {@link UserDTO}s with all {@link UserDTO}s within.
     */
    List<UserDTO> retrieveUserInformationForAdmin(List<UUID> ids);

    /**
     * Retrieves the {@link UserDTOMinimal}s containing the name and the id of all users or from the users with
     * the specific {@code ids}.
     *
     * @param ids The ids to get the UserDTOMinimal from.
     * @return An List of {@link UserDTOMinimal}s with all {@link UserDTOMinimal}s within.
     */
    List<UserDTOMinimal> retrieveUserInformation(List<UUID> ids);

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
    UserDTO registerNewUser(@NonNull UserCreateDTO userCreateDTO);

    /**
     * Creates and saves a new user.
     *
     * @param user the user saved in the database.
     * @return the new {@link User} returned after saving to the database.
     */
    User registerNewUser(@NonNull User user);

    /**
     * Retrieve the {@link UserDTO} for the user with the given username.
     *
     * @param name the username used for the lookup of the user
     * @return the {@link UserDTO} representation of the corresponding user
     */
    UserDTO getUserDTOFromUserName(@NonNull String name);

    /**
     * Fetches the currently active user represented as DTO.
     *
     * @return the {@link UserDTO} representation of the currently active user.
     */
    UserDTO getUserDTOForCurrentUser();

    /**
     * Updates the information of the current user
     *
     * @param updateUserDTO the DTO that contains the new user info
     * @return the {@link UserDTO} of the updated user
     */
    UserDTO updateUser(@NonNull UserUpdateDTO updateUserDTO);

}
