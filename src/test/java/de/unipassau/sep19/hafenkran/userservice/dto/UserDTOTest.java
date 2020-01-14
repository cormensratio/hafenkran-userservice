package de.unipassau.sep19.hafenkran.userservice.dto;

import de.unipassau.sep19.hafenkran.userservice.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class UserDTOTest {

    @Test
    public void testFromUser_validUser_validDTOReturned() {
        // Arrange
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        User user = new User("testUser", "pw", "testMail", false);
        user.setId(userId);
        UserDTO expected = new UserDTO(userId, "testUser", "testMail", User.Status.ACTIVE, false);

        // Act
        UserDTO actual = UserDTO.fromUser(user);

        // Assert
        assertEquals(expected, actual);
    }
}
