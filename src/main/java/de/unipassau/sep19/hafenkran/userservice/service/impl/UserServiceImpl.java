package de.unipassau.sep19.hafenkran.userservice.service.impl;

import de.unipassau.sep19.hafenkran.userservice.config.JwtAuthentication;
import de.unipassau.sep19.hafenkran.userservice.dto.UserCreateDTO;
import de.unipassau.sep19.hafenkran.userservice.dto.UserDTO;
import de.unipassau.sep19.hafenkran.userservice.dto.UserUpdateDTO;
import de.unipassau.sep19.hafenkran.userservice.exception.ResourceNotFoundException;
import de.unipassau.sep19.hafenkran.userservice.model.User;
import de.unipassau.sep19.hafenkran.userservice.repository.UserRepository;
import de.unipassau.sep19.hafenkran.userservice.service.UserService;
import de.unipassau.sep19.hafenkran.userservice.util.SecurityContextUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.UUID;

/**
 * {@inheritDoc}
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {

    @NonNull
    private final UserRepository userRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDetails loadUserByUsername(@NonNull String name) throws UsernameNotFoundException {
        final User user = userRepository.findByName(name).orElseThrow(
                () -> new ResourceNotFoundException(User.class, "name", name));
        return org.springframework.security.core.userdetails.User.withUsername(name).password(
                user.getPassword()).roles().build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDTO getUserDTOFromUserId(@NonNull UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException(User.class, "id", userId.toString()));
        return UserDTO.fromUser(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User registerNewUser(@NonNull @Valid UserCreateDTO userCreateDTO) {
        User user = User.fromUserCreateDTO(userCreateDTO);
        return registerNewUser(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User registerNewUser(@NonNull User user) {
        return userRepository.save(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDTO getUserDTOFromUserName(@NonNull String name) {
        User user = userRepository.findByName(name).orElseThrow(
                () -> new ResourceNotFoundException(User.class, "name", name));
        return UserDTO.fromUser(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDTO getUserDTOForCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthentication) {
            UUID userId = ((UserDTO) authentication.getDetails()).getId();
            return this.getUserDTOFromUserId(userId);
        } else {
            throw new RuntimeException("The user is not authenticated correctly!");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public UserDTO updateUser(@NonNull UserUpdateDTO updateUserDTO) {
        UUID id = updateUserDTO.getId();
        User userToUpdate = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(User.class, "id", id.toString()));

        boolean currentUserIsAdmin = SecurityContextUtil.getCurrentUserDTO().isAdmin();

        if (!id.equals(SecurityContextUtil.getCurrentUserDTO().getId()) && !currentUserIsAdmin) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Only admins are allowed update other users!");
        }

        // set admin flag of updated user only if the user that updates it is an admin
        boolean isAdmin = userToUpdate.isAdmin();
        if (currentUserIsAdmin) {
            isAdmin = updateUserDTO.isAdmin();
        }

        String password = userToUpdate.getPassword();
        if (!updateUserDTO.getPassword().equals("")) {
            password = updateUserDTO.getPassword();
        }

        User updatedUser = new User(
                id,
                userToUpdate.getName(),
                password,
                updateUserDTO.getEmail(),
                isAdmin
        );
        return UserDTO.fromUser(updatedUser);
    }
}