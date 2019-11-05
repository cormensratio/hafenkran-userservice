package de.unipassau.sep19.hafenkran.userservice.service.impl;

import de.unipassau.sep19.hafenkran.userservice.dto.UserDTO;
import de.unipassau.sep19.hafenkran.userservice.exception.ResourceNotFoundException;
import de.unipassau.sep19.hafenkran.userservice.model.User;
import de.unipassau.sep19.hafenkran.userservice.repository.UserRepository;
import de.unipassau.sep19.hafenkran.userservice.service.CustomUserDetailsService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {

    @NonNull
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        final User user = userRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException(User.class, "name", username));
        return org.springframework.security.core.userdetails.User.withUsername(username).password(
                user.getPassword()).roles().build();
    }

    @Override
    public UserDTO getUserDTOFromUserId(@NonNull UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException(User.class, "id", userId.toString()));
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.isAdmin());
    }

    @Override
    public UserDTO getUserDTOFromUserName(@NonNull String username) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException(User.class, "name", username));
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.isAdmin());
    }
}