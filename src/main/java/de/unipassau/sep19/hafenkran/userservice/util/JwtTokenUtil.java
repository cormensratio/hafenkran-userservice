package de.unipassau.sep19.hafenkran.userservice.util;

import de.unipassau.sep19.hafenkran.userservice.dto.UserDTO;
import de.unipassau.sep19.hafenkran.userservice.exception.InvalidJwtException;
import de.unipassau.sep19.hafenkran.userservice.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.RequiredTypeException;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtTokenUtil implements Serializable {

    private final String jwtSecret;

    private Long jwtAuthTokenValidity;

    private Long jwtRefreshTokenValidity;

    public JwtTokenUtil(@Value("${jwt.auth-validity}")
                                Long jwtAuthTokenValidity, @Value("${jwt.refresh-validity}")
                                Long jwtRefreshTokenValidity, @Value("${jwt.secret}") String jwtSecret) {
        this.jwtSecret = jwtSecret;
        this.jwtAuthTokenValidity = jwtAuthTokenValidity;
        this.jwtRefreshTokenValidity = jwtRefreshTokenValidity;
    }

    /**
     * Reads the {@link UUID} expected in the subject of the given JWT.
     *
     * @param token the JWT from where to read the id of the user.
     * @return the {@link UUID} in the subject.
     */
    public UUID getUserIdFromToken(@NonNull String token) {
        return UUID.fromString(getClaimFromToken(token, Claims::getSubject));
    }

    /**
     * Retrieves the {@link UserDTO} from the given JWT.
     * Throws a {@link InvalidJwtException} if the token does not contain the UserDTO.
     *
     * @param token the JWT from where to read the {@link UserDTO}
     * @return the submitted {@link UserDTO}
     */
    public UserDTO getUserDTOFromToken(@NonNull String token) {
        final Claims claims = getAllClaimsFromToken(token);
        final UserDTO userDTO;
        try {
            LinkedHashMap userInformation = claims.get("user", LinkedHashMap.class);
            userDTO = new UserDTO(
                    UUID.fromString(userInformation.get("id").toString()),
                    userInformation.get("name").toString(),
                    userInformation.get("email").toString(),
                    User.Status.ACTIVE,
                    userInformation.get("isAdmin").toString().equals("true")
            );
        } catch (RequiredTypeException | IllegalArgumentException e) {
            throw new InvalidJwtException(UserDTO.class, "user", e);
        }

        return userDTO;
    }

    private Date getExpirationDateFromToken(@NonNull String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private <T> T getClaimFromToken(@NonNull String token, @NonNull Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(@NonNull String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(@NonNull String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Generate a new token for the current user including the users information in the token.
     *
     * @param userDTO the {@link UserDTO} with the information of the current user
     * @return a String with the newly generated JWT
     */
    private String generateToken(@NonNull @Valid UserDTO userDTO, @NonNull @NotEmpty Long tokenValiditySeconds) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user", userDTO);
        return doGenerateToken(claims, userDTO.getId().toString(), tokenValiditySeconds);
    }

    private String doGenerateToken(Map<String, Object> claims, @NonNull @NotEmpty String subject, @NonNull @NotEmpty Long tokenValiditySeconds) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenValiditySeconds * 1000))
                .signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
    }

    /**
     * Validates the JWT whether it is expired
     *
     * @param token the JWT
     * @return {@code true} if token is valid
     */
    public Boolean validateToken(@NonNull @NotEmpty String token) {
        return !isTokenExpired(token);
    }

    public String generateAuthToken(UserDTO userDto) {
        return generateToken(userDto, jwtAuthTokenValidity);
    }

    public String generateRefreshToken() {
        return generateToken(SecurityContextUtil.getCurrentUserDTO(), jwtRefreshTokenValidity);
    }
}