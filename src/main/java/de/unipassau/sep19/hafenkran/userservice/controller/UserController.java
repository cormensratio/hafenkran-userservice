package de.unipassau.sep19.hafenkran.userservice.controller;

import de.unipassau.sep19.hafenkran.userservice.dto.UserCreateDTO;
import de.unipassau.sep19.hafenkran.userservice.dto.UserDTO;
import de.unipassau.sep19.hafenkran.userservice.model.User;
import de.unipassau.sep19.hafenkran.userservice.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/create")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public User createNewUser(@NonNull @RequestBody
                                  @Valid UserCreateDTO userCreateDTO){
        return userService.registerNewUser(userCreateDTO);
    }
}
