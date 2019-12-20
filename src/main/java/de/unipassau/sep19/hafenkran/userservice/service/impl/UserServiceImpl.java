package de.unipassau.sep19.hafenkran.userservice.service.impl;

import de.unipassau.sep19.hafenkran.userservice.config.JwtAuthentication;
import de.unipassau.sep19.hafenkran.userservice.dto.UserCreateDTO;
import de.unipassau.sep19.hafenkran.userservice.dto.UserDTO;
import de.unipassau.sep19.hafenkran.userservice.dto.UserDTOMinimal;
import de.unipassau.sep19.hafenkran.userservice.exception.ResourceNotFoundException;
import de.unipassau.sep19.hafenkran.userservice.model.User;
import de.unipassau.sep19.hafenkran.userservice.repository.UserRepository;
import de.unipassau.sep19.hafenkran.userservice.service.UserService;
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
    public UserDTO registerNewUser(@NonNull @Valid UserCreateDTO userCreateDTO) {
        User user = registerNewUser(
                User.fromUserCreateDTO(userCreateDTO, passwordEncoder.encode(userCreateDTO.getPassword())));
        return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.isAdmin());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User registerNewUser(@NonNull User user) {
        if(userRepository.findByName(user.getName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A user with the name " + user.getName() + " already exists");
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

}