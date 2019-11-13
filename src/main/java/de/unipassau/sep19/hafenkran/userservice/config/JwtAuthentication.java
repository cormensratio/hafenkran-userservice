package de.unipassau.sep19.hafenkran.userservice.config;

import de.unipassau.sep19.hafenkran.userservice.dto.UserDTO;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class JwtAuthentication extends AbstractAuthenticationToken {

    JwtAuthentication(UserDTO userDTO) {
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
