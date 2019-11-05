package de.unipassau.sep19.hafenkran.userservice.service;

import de.unipassau.sep19.hafenkran.userservice.dto.UserDTO;
import lombok.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.UUID;

public interface CustomUserDetailsService extends UserDetailsService {
    /**
     * Retrieves the {@link UserDetails} of a user based on their username.
     *
     * @param username the name of the user
     * @return the corresponding {@link UserDetails}
     * @throws UsernameNotFoundException when no user with the given username could be found
     */
    UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException;

    /**
     * Retrieves a {@link UserDTO} for a user with the given userId.
     *
     * @param userId the userId used for lookup
     * @return the {@link UserDTO}
     */
    UserDTO getUserDTOFromUserId(@NonNull UUID userId);

    /**
     * Retrieves a {@link UserDTO} for a user with the given userName.
     *
     * @param userName the userName used for lookup
     * @return the {@link UserDTO}
     */
    UserDTO getUserDTOFromUserName(@NonNull String userName);
}
