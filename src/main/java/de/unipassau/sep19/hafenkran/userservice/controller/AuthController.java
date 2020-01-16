package de.unipassau.sep19.hafenkran.userservice.controller;

import de.unipassau.sep19.hafenkran.userservice.dto.AuthRequestDTO;
import de.unipassau.sep19.hafenkran.userservice.dto.AuthResponseDTO;
import de.unipassau.sep19.hafenkran.userservice.dto.UserDTO;
import de.unipassau.sep19.hafenkran.userservice.model.User;
import de.unipassau.sep19.hafenkran.userservice.service.UserService;
import de.unipassau.sep19.hafenkran.userservice.util.JwtTokenUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * A {@link RestController} for requesting a JWT for a user session.
 */
@RestController
@CrossOrigin
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthController {

    @NonNull
    private final AuthenticationManager authenticationManager;

    @NonNull
    private final JwtTokenUtil jwtTokenUtil;

    @NonNull
    private final UserService userService;

    /**
     * A POST-Endpoint requiring a {@link AuthRequestDTO} for generating the JWT refresh token used for requesting a new auth token.
     *
     * @param authenticationRequest the users {@link AuthRequestDTO}
     * @return a {@link AuthResponseDTO} including the newly generated token
     */
    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody @Valid AuthRequestDTO authenticationRequest) {
        authenticate(authenticationRequest.getName(), authenticationRequest.getPassword(), authenticationRequest.getStatus());
        UserDTO userDto = userService.getUserDTOFromUserName(authenticationRequest.getName());
        final String token = jwtTokenUtil.generateAuthToken(userDto);
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }

    private void authenticate(@NonNull String username, @NonNull String password, @NonNull User.Status status) {
        try {
            if (status.equals(User.Status.ACTIVE)) {
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            } else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You have no permission from an admin to use the system.");
            }
        } catch (DisabledException e) {
            throw new RuntimeException("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new RuntimeException("INVALID_CREDENTIALS", e);

        }
    }

    /**
     * A GET-Endpoint for generating the JWT auth token sent with every request.
     *
     * @return a {@link AuthResponseDTO} including the newly generated token
     */
    @GetMapping("/refresh")
    public ResponseEntity<?> refreshAuthToken() {
        final String token = jwtTokenUtil.generateRefreshToken();
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }

}