package de.unipassau.sep19.hafenkran.userservice.util;

import de.unipassau.sep19.hafenkran.userservice.dto.UserDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Component
public class JwtTokenUtil implements Serializable {

    @Value("${jwt.validity}")
    private Long JWT_TOKEN_VALIDITY;

    @Value("${jwt.secret}")
    private String secret;

    public String getUserIdFromToken(@NonNull @NotEmpty String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    private Date getExpirationDateFromToken(@NonNull @NotEmpty String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private <T> T getClaimFromToken(@NonNull @NotEmpty String token, @NonNull Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(@NonNull @NotEmpty String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();

    }

    private Boolean isTokenExpired(@NonNull @NotEmpty String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Generate a new token for the current user including the users information in the token.
     *
     * @param userDTO the {@link UserDTO} with the information of the current user
     * @return a String with the newly generated JWT
     */
    public String generateToken(@NonNull @Valid UserDTO userDTO) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user", userDTO);
        return doGenerateToken(claims, userDTO.getUserId().toString());
    }

    private String doGenerateToken(Map<String, Object> claims, @NonNull @NotEmpty String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    /**
     * Validates the JWT whether it is expired
     *
     * @param token   the JWT
     * @param userDto the {@link UserDTO} of the user which it should be validated against.
     * @return {@code true} if token is valid
     */
    public Boolean validateToken(@NonNull @NotEmpty String token, @NonNull @Valid UserDTO userDto) {
        final UUID userIdFromToken = UUID.fromString(getUserIdFromToken(token));
        return (userIdFromToken.equals(userDto.getUserId()) && !isTokenExpired(token));
    }

}