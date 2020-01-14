package de.unipassau.sep19.hafenkran.userservice.util;

import de.unipassau.sep19.hafenkran.userservice.dto.UserDTO;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(JUnit4.class)
public class JwtTokenUtilTest {

    private static final UserDTO USER_DTO = new UserDTO(UUID.fromString("00000000-0000-0000-0000-000000000001"), "Rick",
            "", false);
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();
    private String jwt;
    private JwtTokenUtil subject;

    @Before
    public void setUp() {
        this.subject = new JwtTokenUtil(18000L, 18000L,"MCgCIQCAS7IFlSvaBOPXwSBHo+7+6C4RbkvYj3fgI5+Abe4pRwIDAQAB");
        this.jwt = subject.generateAuthToken(USER_DTO);
    }

    @Test
    public void testGetUserIdFromToken_validToken_validIdReturned() {
        // Arrange

        // Act
        UUID actual = subject.getUserIdFromToken(jwt);

        // Assert
        assertEquals(USER_DTO.getId(), actual);
    }

    @Test
    public void testGetUserIdFromToken_invalidToken_exception() {
        // Arrange
        expectedEx.expect(MalformedJwtException.class);

        // Act
        UUID actual = subject.getUserIdFromToken("asd");

        // Assert - with rule
    }

    @Test
    public void testGetUserIdFromToken_emptyToken_exception() {
        // Arrange
        expectedEx.expect(IllegalArgumentException.class);

        // Act
        UUID actual = subject.getUserIdFromToken("");

        // Assert - with rule
    }

    @Test
    public void testGetUserDTOFromToken_validToken_validDTOReturned() {
        // Arrange

        // Act
        UserDTO actual = subject.getUserDTOFromToken(jwt);

        // Assert
        assertEquals(USER_DTO, actual);
    }

    @Test
    public void testValidateToken_validToken_true() {
        // Arrange

        // Act
        boolean actual = subject.validateToken(jwt);

        // Assert
        assertTrue(actual);
    }

    @Test
    public void testValidateToken_expiredToken_exception() {
        // Arrange
        expectedEx.expect(ExpiredJwtException.class);

        JwtTokenUtil subject = new JwtTokenUtil(-1000L,-1000L, "MCgCIQCAS7IFlSvaBOPXwSBHo+7+6C4RbkvYj3fgI5+Abe4pRwIDAQAB");
        jwt = subject.generateAuthToken(USER_DTO);

        // Act
        boolean actual = subject.validateToken(jwt);

        // Assert - with rule
    }

}
