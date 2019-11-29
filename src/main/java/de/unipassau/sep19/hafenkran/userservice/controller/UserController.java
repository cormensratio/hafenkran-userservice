package de.unipassau.sep19.hafenkran.userservice.controller;

import de.unipassau.sep19.hafenkran.userservice.dto.UserDTO;
import de.unipassau.sep19.hafenkran.userservice.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/update")
    @ResponseBody
    public UserDTO updateUser(@RequestParam("user") UserDTO newUserInfo,
                              @RequestParam(value = "newPassword", required = false) String newPassword) {
        return userService.updateUser(newUserInfo, newPassword);
    }
}
