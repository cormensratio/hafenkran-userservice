package de.unipassau.sep19.hafenkran.userservice.service.impl;

import de.unipassau.sep19.hafenkran.userservice.config.JwtAuthentication;
import de.unipassau.sep19.hafenkran.userservice.dto.UserCreateDTO;
import de.unipassau.sep19.hafenkran.userservice.dto.UserDTO;
import de.unipassau.sep19.hafenkran.userservice.exception.ResourceNotFoundException;
import de.unipassau.sep19.hafenkran.userservice.model.User;
import de.unipassau.sep19.hafenkran.userservice.repository.UserRepository;
import de.unipassau.sep19.hafenkran.userservice.service.UserService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    private UserService subject;
    private User testUser;

    @Before
    public void setUp() {
        this.subject = new UserServiceImpl(userRepository, passwordEncoder);
        this.testUser = new User("testUser", "testPassword", "testMail", false);
        this.testUser.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    }

    @Test
    public void testLoadUserByUsername_validName_validUserDetailsReturned() {
        // Arrange
        UserDetails expectedUserDetails = org.springframework.security.core.userdetails.User.withUsername(
                testUser.getName()).password(
                testUser.getPassword()).roles().build();

        when(userRepository.findByName(testUser.getName())).thenReturn(Optional.of(testUser));

        // Act
        UserDetails actual = subject.loadUserByUsername(testUser.getName());

        // Assert
        verify(userRepository, times(1)).findByName(testUser.getName());
        assertEquals(expectedUserDetails, actual);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void testLoadUserByUsername_noUserExistsWithName_exception() {
        // Arrange
        String testUserName = "nonExistingUsername";
        expectedEx.expect(ResourceNotFoundException.class);
        when(userRepository.findByName(testUserName)).thenReturn(Optional.empty());

        // Act
        subject.loadUserByUsername(testUserName);

        // Assert - with rule
    }

    @Test
    public void testGetUserDTOFromUserId_validUserId_validUserDTOReturned() {
        // Arrange
        UserDTO expectedDTO = UserDTO.fromUser(testUser);
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        // Act
        UserDTO actual = subject.getUserDTOFromUserId(testUser.getId());

        // Assert
        verify(userRepository, times(1)).findById(testUser.getId());
        assertEquals(expectedDTO, actual);
        verifyNoMoreInteractions(userRepository);
    }


    @Test
    public void testGetUserDTOFromUserId_noUserExistsWithId_exception() {
        // Arrange
        UUID testId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        expectedEx.expect(ResourceNotFoundException.class);
        when(userRepository.findById(testId)).thenReturn(Optional.empty());

        // Act
        subject.getUserDTOFromUserId(testId);

        // Assert - with rule
    }

    @Test
    public void testGetUserDTOFromUsername_validUsername_validUserDTOReturned() {
        // Arrange
        UserDTO expectedDTO = UserDTO.fromUser(testUser);
        when(userRepository.findByName(testUser.getName())).thenReturn(Optional.of(testUser));

        // Act
        UserDTO actual = subject.getUserDTOFromUserName(testUser.getName());

        // Assert
        verify(userRepository, times(1)).findByName(testUser.getName());
        assertEquals(expectedDTO, actual);
        verifyNoMoreInteractions(userRepository);
    }


    @Test
    public void testGetUserDTOFromUsername_noUserExistsWithUsername_exception() {
        // Arrange
        String testUserName = "nonExistingUsername";
        expectedEx.expect(ResourceNotFoundException.class);
        when(userRepository.findByName(testUserName)).thenReturn(Optional.empty());

        // Act
        subject.getUserDTOFromUserName(testUserName);

        // Assert - with rule
    }

    @Test
    public void testGetUserDTOForCurrentUser_validSession_validDTOReturned() {
        // Arrange
        UserDTO userDTO = UserDTO.fromUser(testUser);
        SecurityContext mockContext = mock(SecurityContext.class);
        JwtAuthentication auth = new JwtAuthentication(userDTO);

        SecurityContextHolder.setContext(mockContext);
        when(mockContext.getAuthentication()).thenReturn(auth);
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        // Act
        UserDTO actual = subject.getUserDTOForCurrentUser();

        // Assert
        assertEquals(userDTO, actual);
        verify(mockContext, times(1)).getAuthentication();
        verify(userRepository, times(1)).findById(testUser.getId());
        verifyNoMoreInteractions(mockContext, userRepository);
    }
}
