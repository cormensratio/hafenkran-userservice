package de.unipassau.sep19.hafenkran.userservice.util;

import de.unipassau.sep19.hafenkran.userservice.config.JwtAuthentication;
import de.unipassau.sep19.hafenkran.userservice.dto.UserDTO;
import de.unipassau.sep19.hafenkran.userservice.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * A utility method for extracting information about the current user stored in the {@link org.springframework.security.core.context.SecurityContext}.
 */
public class SecurityContextUtil {

    /**
     * Retrieves the {@link UserDTO} for the current user.
     *
     * @return {@link UserDTO} with information about the current user.
     */
    public static UserDTO getCurrentUserDTO() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthentication) {
            JwtAuthentication authToken = (JwtAuthentication) auth;
            final UserDTO currentUser = (UserDTO) authToken.getDetails();
            if (currentUser.getStatus() == User.Status.ACTIVE) {
                return currentUser;
            } else {
                throw new RuntimeException("You are not allowed to log in cause you aren't approved by an admin.");
            }
        } else {
            throw new RuntimeException("Invalid user session");
        }
    }
}
