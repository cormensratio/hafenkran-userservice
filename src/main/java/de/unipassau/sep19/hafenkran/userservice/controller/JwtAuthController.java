package de.unipassau.sep19.hafenkran.userservice.controller;

import de.unipassau.sep19.hafenkran.userservice.config.JwtUserDetailsService;
import de.unipassau.sep19.hafenkran.userservice.dto.JwtRequestDTO;
import de.unipassau.sep19.hafenkran.userservice.dto.JwtResponseDTO;
import de.unipassau.sep19.hafenkran.userservice.util.JwtTokenUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@CrossOrigin
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JwtAuthController {

    @NotNull
    @NonNull
    private final AuthenticationManager authenticationManager;

    @NotNull
    @NonNull
    private final JwtTokenUtil jwtTokenUtil;

    @NotNull
    @NonNull
    private final JwtUserDetailsService userDetailsService;

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequestDTO authenticationRequest) throws Exception {
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());

        final String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponseDTO(token));
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);

        }
    }

}