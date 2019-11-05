package de.unipassau.sep19.hafenkran.userservice.service.impl;

import de.unipassau.sep19.hafenkran.userservice.dto.UserCreateDTO;
import de.unipassau.sep19.hafenkran.userservice.dto.UserDTO;
import de.unipassau.sep19.hafenkran.userservice.model.User;
import de.unipassau.sep19.hafenkran.userservice.repository.UserRepository;
import de.unipassau.sep19.hafenkran.userservice.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {

    @NonNull
    private final UserRepository userRepository;

    @NonNull
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDTO getUserDTOFromUserId(@NonNull UUID userId) {
        // TODO: custom exception
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("No user found for id " + userId));

        return new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.isAdmin());
    }

    @Override
    public UserDTO getUserDTOForCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            User user = userRepository.findByUsername(username).orElseThrow(
                    () -> new IllegalArgumentException("No user found for name " + username));
            return new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.isAdmin());
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public User registerNewUser(@NonNull @Valid UserCreateDTO userCreateDTO) {
        User user = new User(userCreateDTO.getUsername(), passwordEncoder.encode(userCreateDTO.getPassword()),
                userCreateDTO.getEmail(), userCreateDTO.isAdmin());
        return userRepository.save(user);
    }
}