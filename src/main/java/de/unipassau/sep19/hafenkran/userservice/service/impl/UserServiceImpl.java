package de.unipassau.sep19.hafenkran.userservice.service.impl;

import de.unipassau.sep19.hafenkran.userservice.config.JwtAuthentication;
import de.unipassau.sep19.hafenkran.userservice.dto.UserCreateDTO;
import de.unipassau.sep19.hafenkran.userservice.dto.UserDTO;
import de.unipassau.sep19.hafenkran.userservice.dto.UserDTOMinimal;
import de.unipassau.sep19.hafenkran.userservice.dto.UserUpdateDTO;
import de.unipassau.sep19.hafenkran.userservice.exception.ResourceNotFoundException;
import de.unipassau.sep19.hafenkran.userservice.model.User;
import de.unipassau.sep19.hafenkran.userservice.repository.UserRepository;
import de.unipassau.sep19.hafenkran.userservice.service.UserService;
import de.unipassau.sep19.hafenkran.userservice.util.SecurityContextUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * {@inheritDoc}
 */
@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired, @Lazy}))
public class UserServiceImpl implements UserService {

    @NonNull
    private final UserRepository userRepository;

    @NonNull
    private final PasswordEncoder passwordEncoder;

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
    public List<UserDTO> retrieveUserInformationForAdmin(List<UUID> ids) {
        return UserDTO.convertUserListToDTOList(findUserList(ids));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserDTOMinimal> retrieveUserInformation(List<UUID> ids) {
        return UserDTOMinimal.convertMinimalUserListToDTOList(findUserList(ids));
    }

    private List<User> findUserList(List<UUID> ids) {
        if (ids.isEmpty()) {
            return (List<User>) userRepository.findAll();
        } else {
            return userRepository.findByIdIn(ids);
        }
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
    public UserDTO deleteUser(@NonNull UUID id) {
        UserDTO currentUser = SecurityContextUtil.getCurrentUserDTO();
        if (currentUser.isAdmin()) {
            User deletedUser = userRepository.findById(id).orElseThrow(
                    () -> new ResourceNotFoundException(User.class, "id", id.toString()));
            UserDTO deletedUserDTO = UserDTO.fromUser(deletedUser);

            userRepository.deleteById(id);
            return deletedUserDTO;
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only admins are allowed to delete users.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDTO registerNewUser(@NonNull @Valid UserCreateDTO userCreateDTO) {
        User user = registerNewUser(
                User.fromUserCreateDTO(userCreateDTO, passwordEncoder.encode(userCreateDTO.getPassword())));
        return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getStatus(), user.isAdmin());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User registerNewUser(@NonNull User user) {
        if (userRepository.findByName(user.getName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A user with the name " + user.getName() + " already exists");
        }
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
    public UserDTO updateUser(@NonNull UserUpdateDTO updateUserDTO, boolean changeStatus) {
        UUID id = updateUserDTO.getId();
        User targetUser = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(User.class, "id", id.toString()));

        UserDTO currentUserDTO = SecurityContextUtil.getCurrentUserDTO();
        boolean currentUserIsAdmin = currentUserDTO.isAdmin();

        if (!id.equals(currentUserDTO.getId()) && !currentUserIsAdmin) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Only admins are allowed to update other users!");
        }

        if (!currentUserIsAdmin) {
            if (!isPasswordMatching(targetUser.getPassword(), updateUserDTO.getPassword())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "The given user password is not correct!");
            }
        }

        // Change status if the currentUser is an admin and to change the status was selected
        if (changeStatus && currentUserIsAdmin) {
            setUserStatus(targetUser);
        } else if (changeStatus) { // Statuschange was selected, but it wasn't an admin
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "You are not allowed to change the status of the user.");
        }

        // set admin flag of updated user only if the user that updates it is an admin
        boolean isAdmin = targetUser.isAdmin();
        if (currentUserIsAdmin) {
            isAdmin = updateUserDTO.isAdmin();
        }

        String password = targetUser.getPassword();
        if (!updateUserDTO.getNewPassword().equals("")) {
            password = passwordEncoder.encode(updateUserDTO.getNewPassword());
        }

        targetUser.setPassword(password);
        targetUser.setEmail(updateUserDTO.getEmail());
        targetUser.setAdmin(isAdmin);

        userRepository.save(targetUser);
        return UserDTO.fromUser(targetUser);
    }


    /**
     * Updates the status of the {@code user}.
     * If it was {@link User.Status}.INACTIVE the status will now be {@link User.Status}.ACTIVE.
     * If it was {@link User.Status}.ACTIVE the status will now be {@link User.Status}.INACTIVE.
     *
     * @param user The user which status should be changed.
     */
    private void setUserStatus(@NonNull User user) {
        switch (user.getStatus()) {
            case ACTIVE:
                user.setStatus(User.Status.INACTIVE);
                break;
            case INACTIVE:
                user.setStatus(User.Status.ACTIVE);
            default:
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "There was no status found.");

        }
    }

    /**
     * Checks if two passwords, one encoded, the other in plain text are matching
     *
     * @param encodedPassword   the encoded password
     * @param plaintextPassword the plain text password
     * @return if passwords are matching
     */
    private boolean isPasswordMatching(@NonNull String encodedPassword, @NonNull String plaintextPassword) {
        return passwordEncoder.matches(plaintextPassword, encodedPassword);
    }
}