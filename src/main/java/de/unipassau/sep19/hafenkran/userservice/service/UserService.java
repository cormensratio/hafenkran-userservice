package de.unipassau.sep19.hafenkran.userservice.service;

import de.unipassau.sep19.hafenkran.userservice.dto.UserCreateDTO;
import de.unipassau.sep19.hafenkran.userservice.dto.UserDTO;
import de.unipassau.sep19.hafenkran.userservice.model.User;

import java.util.UUID;

public interface UserService {
    UserDTO getUserDTOFromUserId(UUID userId);

    UserDTO getUserDTOForCurrentUser();

    User registerNewUser(UserCreateDTO userCreateDTO);
}
