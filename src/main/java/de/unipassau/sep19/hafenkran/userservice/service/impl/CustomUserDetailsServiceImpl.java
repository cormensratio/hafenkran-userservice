package de.unipassau.sep19.hafenkran.userservice.service.impl;

import de.unipassau.sep19.hafenkran.userservice.dto.UserDTO;
import de.unipassau.sep19.hafenkran.userservice.model.User;
import de.unipassau.sep19.hafenkran.userservice.repository.UserRepository;
import de.unipassau.sep19.hafenkran.userservice.service.CustomUserDetailsService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {

    @NonNull
    @NotNull
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found with name: " + username));

        return org.springframework.security.core.userdetails.User.withUsername(username).password(
                user.getPassword()).roles().build();
    }

    @Override
    public UserDTO getUserDTOFromUserId(UUID userId) {
        // TODO: custom exception
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("No user found for id " + userId));

        return new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.isAdmin());
    }

    @Override
    public UserDTO getUserDTOFromUserName(String userName) {
        User user = userRepository.findByUsername(userName).orElseThrow(
                () -> new IllegalArgumentException("No user found for name " + userName));

        return new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.isAdmin());
    }
}