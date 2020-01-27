package de.unipassau.sep19.hafenkran.userservice.service.impl;

import de.unipassau.sep19.hafenkran.userservice.config.JwtAuthentication;
import de.unipassau.sep19.hafenkran.userservice.dto.UserCreateDTO;
import de.unipassau.sep19.hafenkran.userservice.dto.UserDTO;
import de.unipassau.sep19.hafenkran.userservice.dto.UserDTOMinimal;
import de.unipassau.sep19.hafenkran.userservice.dto.UserUpdateDTO;
import de.unipassau.sep19.hafenkran.userservice.exception.ResourceNotFoundException;
import de.unipassau.sep19.hafenkran.userservice.model.User;
import de.unipassau.sep19.hafenkran.userservice.model.User.Status;
import de.unipassau.sep19.hafenkran.userservice.repository.UserRepository;
import de.unipassau.sep19.hafenkran.userservice.service.UserService;
import de.unipassau.sep19.hafenkran.userservice.util.SecurityContextUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Optional;
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
    @Value("${password-length-min}")
    private int MIN_PASSWORD_LENGTH;

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

            // Only delete the account if it isn't the account from the current user
            if (currentUser.getId() != id) {
                UserDTO deletedUserDTO = UserDTO.fromUser(deletedUser);
                userRepository.deleteById(id);
                return deletedUserDTO;
            } else {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "You are not allowed to delete your own account. Please contact another admin to do so.");
            }
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
    public UserDTO updateUser(@NonNull UUID userId,
                              @NonNull UserUpdateDTO updateUserDTO) {
        final UserDTO currentUserDTO = SecurityContextUtil.getCurrentUserDTO();
        final boolean currentUserIsAdmin = currentUserDTO.isAdmin();
        final UUID targetUserId = userId;
        final UUID currentUserId = currentUserDTO.getId();
        final Optional<Status> status = updateUserDTO.getStatus();

        if (!currentUserIsAdmin && !targetUserId.equals(currentUserDTO.getId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "You are not allowed to update users!");
        }

        User targetUser = userRepository.findById(targetUserId).orElseThrow(
                () -> new ResourceNotFoundException(User.class, "id", targetUserId.toString()));

        User currentUser = userRepository.findById(currentUserId).orElseThrow(
                () -> new ResourceNotFoundException(User.class, "id",
                        currentUserId.toString()));


        // Throw exception if the user tries to change the settings without
        // providing the old password.
        if (!currentUserIsAdmin && !updateUserDTO.getPassword().isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "You have to provide your password in order to " +
                            "modify your user settings!");
        }

        // Throw exception if the given user password is incorrect
        if (!currentUserIsAdmin && !isPasswordMatching(currentUser.getPassword(), updateUserDTO.getPassword().get())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "The given password is not correct!");
        }

        // Throw exception if the user is an admin and does not provide a
        // password to modify another admin
        if (currentUserIsAdmin && targetUser.isAdmin()
                && !updateUserDTO.getPassword().isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "You have to provide your password in order to " +
                            "modify your user settings!");
        }

        // Throw exception if the user is an admin and provides a wrong
        // password to modify another admin
        if (currentUserIsAdmin && targetUser.isAdmin()
                && !isPasswordMatching(currentUser.getPassword(), updateUserDTO.getPassword().get())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "The given password is not correct!");
        }

        // Change status if the currentUser is an admin and to change the status was selected
        if (currentUserIsAdmin
                && status.isPresent()) {
            if (targetUser.getId() == currentUserDTO.getId()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "You aren't allowed to change your own status.");
            } else {
                targetUser.setStatus(status.get());
            }
        }

        // Set admin flag of updated user only if the user that updates it is
        // an admin
        if (currentUserIsAdmin && updateUserDTO.getIsAdmin().isPresent()) {
            targetUser.setAdmin(updateUserDTO.getIsAdmin().get());
        }

        if (!currentUserIsAdmin && updateUserDTO.getIsAdmin().isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You " +
                    "are no admin and not allowed to change the admin status!");
        }

        if (updateUserDTO.getNewPassword().isPresent() && !updateUserDTO.getNewPassword().get().isEmpty()) {
            if (currentUserIsAdmin) {
                targetUser.setPassword(passwordEncoder.encode(updateUserDTO.getNewPassword().get()));
            } else if (updateUserDTO.getPassword().isPresent()) {
                if (!isPasswordMatching(targetUser.getPassword(),
                        updateUserDTO.getPassword().get())) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                            "The given password is not correct!");
                }

                hasCorrectPasswordLength(updateUserDTO.getNewPassword().get());
                targetUser.setPassword(passwordEncoder.encode(updateUserDTO.getNewPassword().get()));
            }
        }

        if (updateUserDTO.getEmail().isPresent()
                && !updateUserDTO.getEmail().get().isEmpty()) {
            targetUser.setEmail(updateUserDTO.getEmail().get());
        }

        userRepository.save(targetUser);
        return UserDTO.fromUser(targetUser);
    }

    private List<User> findUserList(List<UUID> ids) {
        if (ids.isEmpty()) {
            return (List<User>) userRepository.findAll();
        } else {
            return userRepository.findByIdIn(ids);
        }
    }

    /**
     * Checks if two passwords, one encoded, the other in plain text are
     * matching
     *
     * @param encodedPassword   the encoded password
     * @param plaintextPassword the plain text password
     * @return if passwords are matching
     */
    private boolean isPasswordMatching(@NonNull String encodedPassword, @NonNull String plaintextPassword) {
        return passwordEncoder.matches(plaintextPassword, encodedPassword);
    }

    private void hasCorrectPasswordLength(@NonNull String password) {
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "The given password was not long enough! Expected " +
                            "minimum length of: " + MIN_PASSWORD_LENGTH);
        }
    }
}