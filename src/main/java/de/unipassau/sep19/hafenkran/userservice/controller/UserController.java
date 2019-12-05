package de.unipassau.sep19.hafenkran.userservice.controller;

import de.unipassau.sep19.hafenkran.userservice.dto.UserDTO;
import de.unipassau.sep19.hafenkran.userservice.dto.UserUpdateDTO;
import de.unipassau.sep19.hafenkran.userservice.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
     * Retrieves the {@link UserDTO} for the currently active user session.
     *
     * @return a {@link UserDTO} containing the details of a user
     */
    @GetMapping("/me")
    @ResponseBody
    public UserDTO me() {
        return userService.getUserDTOForCurrentUser();
    }

    /**
     * Updates the given user
     *
     * @param newUserInfo the DTO that holds the new user info
     * @return a {@link UserDTO} containing the details of the updated user
     */
    @PostMapping("/update")
    @ResponseBody
    public UserDTO updateUser(@Valid @RequestBody UserUpdateDTO newUserInfo) {
        return userService.updateUser(newUserInfo);
    }
}
