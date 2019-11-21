package de.unipassau.sep19.hafenkran.userservice.config;

import de.unipassau.sep19.hafenkran.userservice.dto.UserDTO;
import lombok.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

/**
 * A class for persisting the {@link UserDTO} of the current user in a thread local {@link org.springframework.security.core.context.SecurityContextHolder}
 */
@Validated
public class JwtAuthentication extends AbstractAuthenticationToken {

    public JwtAuthentication(@NonNull @Valid UserDTO userDTO) {
        super(null);
        super.setAuthenticated(true);
        super.setDetails(userDTO);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}
