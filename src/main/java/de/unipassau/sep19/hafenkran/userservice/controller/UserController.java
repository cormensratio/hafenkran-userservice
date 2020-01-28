package de.unipassau.sep19.hafenkran.userservice.controller;

import de.unipassau.sep19.hafenkran.userservice.dto.UserCreateDTO;
import de.unipassau.sep19.hafenkran.userservice.dto.UserDTO;
import de.unipassau.sep19.hafenkran.userservice.dto.UserDTOMinimal;
import de.unipassau.sep19.hafenkran.userservice.dto.UserUpdateDTO;
import de.unipassau.sep19.hafenkran.userservice.model.User;
import de.unipassau.sep19.hafenkran.userservice.service.UserService;
import de.unipassau.sep19.hafenkran.userservice.util.SecurityContextUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * A {@link RestController} for retrieving basic user information.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {

    @NonNull
    private final UserService userService;

    /**
     * GET-Endpoint for retrieving the {@link UserDTO} for the currently active user session.
     *
     * @return a {@link UserDTO} containing the details of a user
     */
    @GetMapping("/me")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public UserDTO me() {
        return userService.getUserDTOForCurrentUser();
    }

    /**
     * POST-Endpoint for creating a new {@link User}.
     *
     * @param userCreateDTO The DTO used to create the new {@link User}.
     * @return The newly created {@link User}.
     */
    @PostMapping("/create")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public UserDTO createNewUser(@NonNull @RequestBody
                                 @Valid UserCreateDTO userCreateDTO) {
        return userService.registerNewUser(userCreateDTO);
    }

    /**
     * GET-Endpoint for receiving all {@link UserDTO}s of all users in the network or of all users with the {@code ids}
     * if you are an admin, or receiving all {@link UserDTOMinimal}s if you are not an admin.
     *
     * @return A list of {@link UserDTO}s or {@link UserDTOMinimal}s with all users or all requested users.
     */
    @GetMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<?> retrieveAllUsers(@RequestParam(name = "ids", required = false) List<UUID> ids) {
        UserDTO currentUser = SecurityContextUtil.getCurrentUserDTO();
        if (ids == null) {
            ids = Collections.emptyList();
        }
        if (currentUser.isAdmin()) {
            return userService.retrieveUserInformationForAdmin(ids);
        } else {
            return userService.retrieveUserInformation(ids);
        }
    }

    /**
     * Updates the given user.
     * The params, that can be updated, are the password, the email, the status and the adminFlag.
     *
     * @param newUserInfo the DTO that holds the new user informations.
     * @return A {@link UserDTO} containing the details of the updated user.
     */
    @PostMapping("/update")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public UserDTO updateUser(@Valid @RequestBody UserUpdateDTO newUserInfo) {
        return userService.updateUser(newUserInfo);
    }

    /**
     * POST-Endpoint to delete one user. This endpoint is only available for admins.
     * If {@code deleteEverything} is set true, everything from the user will be deleted, also if it is shared.
     *
     * @param id The id of the user to be deleted.
     * @param deleteEverything The chosen deletion method.
     * @return A {@link UserDTO} of the user that was deleted.
     */
    @PostMapping("/delete/{id}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public UserDTO deleteUser(@PathVariable UUID id, @RequestParam (defaultValue = "false") boolean deleteEverything) {
        return userService.deleteUser(id, deleteEverything);
    }
}
