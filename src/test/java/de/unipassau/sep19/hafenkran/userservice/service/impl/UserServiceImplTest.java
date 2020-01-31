package de.unipassau.sep19.hafenkran.userservice.service.impl;

import de.unipassau.sep19.hafenkran.userservice.config.JwtAuthentication;
import de.unipassau.sep19.hafenkran.userservice.dto.UserDTO;
import de.unipassau.sep19.hafenkran.userservice.dto.UserUpdateDTO;
import de.unipassau.sep19.hafenkran.userservice.exception.ResourceNotFoundException;
import de.unipassau.sep19.hafenkran.userservice.model.User;
import de.unipassau.sep19.hafenkran.userservice.repository.UserRepository;
import de.unipassau.sep19.hafenkran.userservice.service.UserService;
import de.unipassau.sep19.hafenkran.userservice.serviceclient.ClusterServiceClient;
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
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ClusterServiceClient clusterServiceClient;

    private UserService subject;
    private User testUser;
    private User testUser2;
    private User testUser3;
    private User testAdmin;
    private User testAdmin2;

    private final int MIN_PASSWORD_LENGTH = 8;

    @Before
    public void setUp() {
        this.subject = new UserServiceImpl(userRepository, passwordEncoder, clusterServiceClient);
        this.testUser = new User("testUser", "testPassword", "testMail", false);
        this.testUser2 = new User("testUser", "testPassword", "testMail", false);
        this.testUser3 = new User("testUser", "testPassword", "testMail", false);
        this.testAdmin = new User("testAdmin", "testPassword", "testMail", true);
        this.testAdmin2 = new User("testAdmin", "testPassword", "testMail", true);
        this.testUser.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        this.testUser2.setId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        this.testUser3.setId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
        this.testAdmin.setId(UUID.fromString("00000000-0000-0000-0000-000000000004"));
        this.testAdmin2.setId(UUID.fromString("00000000-0000-0000-0000-000000000005"));
        this.testUser.setStatus(User.Status.ACTIVE);
        this.testUser2.setStatus(User.Status.ACTIVE);
        this.testUser3.setStatus(User.Status.DELETED);
        this.testAdmin.setStatus(User.Status.ACTIVE);
        this.testAdmin2.setStatus(User.Status.DELETED);
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

    @Test
    public void testDeleteUser_adminDeletesUser_userSuccessfullyDeleted() {
        // Arrange
        UserDTO userDTO = UserDTO.fromUser(testAdmin);
        SecurityContext mockContext = mock(SecurityContext.class);
        JwtAuthentication auth = new JwtAuthentication(userDTO);
        SecurityContextHolder.setContext(mockContext);

        when(mockContext.getAuthentication()).thenReturn(auth);
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser3);

        // Act
        UserDTO deletedUser = subject.deleteUser(testUser.getId());

        // Assert
        assertEquals(deletedUser.getId(), testUser.getId());
        assertTrue(userDTO.isAdmin());
        verify(mockContext, times(1)).getAuthentication();
        verify(userRepository, times(1)).findById(testUser.getId());
        verify(userRepository, times(1)).save(testUser);
        verifyNoMoreInteractions(mockContext, userRepository);
    }

    @Test
    public void testDeleteUser_adminDeletesHimself_throwsException() {
        // Arrange
        expectedEx.expect(ResponseStatusException.class);
        expectedEx.expectMessage("You are not allowed to delete your own account. " +
                "Please contact another admin to do so.");

        UserDTO userDTO = UserDTO.fromUser(testAdmin);
        SecurityContext mockContext = mock(SecurityContext.class);
        JwtAuthentication auth = new JwtAuthentication(userDTO);
        SecurityContextHolder.setContext(mockContext);

        when(mockContext.getAuthentication()).thenReturn(auth);
        when(userRepository.findById(testAdmin.getId())).thenReturn(Optional.of(testAdmin));

        // Act
        UserDTO deletedUser = subject.deleteUser(testAdmin.getId());

        // Assert - with rule

    }

    @Test
    public void testDeleteUser_userDeletesAnotherUser_throwsException() {
        // Arrange
        expectedEx.expect(ResponseStatusException.class);
        expectedEx.expectMessage("Only admins are allowed to delete users.");

        UserDTO userDTO = UserDTO.fromUser(testUser);
        SecurityContext mockContext = mock(SecurityContext.class);
        JwtAuthentication auth = new JwtAuthentication(userDTO);
        SecurityContextHolder.setContext(mockContext);

        when(mockContext.getAuthentication()).thenReturn(auth);

        // Act
        UserDTO deletedUser = subject.deleteUser(testUser2.getId());

        // Assert - with rule

    }

    @Test
    public void testUpdateUser_adminWantsToChangeExistingUser_userSuccessfullyUpdated() {
        // Arrange
        UserUpdateDTO newUserInfo = new UserUpdateDTO(
                Optional.of(testUser.getPassword()),
                Optional.of("newpassword"),
                Optional.of("newmail"),
                Optional.of(User.Status.ACTIVE),
                Optional.of(true)
        );

        UserDTO userDTO = UserDTO.fromUser(testAdmin);
        SecurityContext mockContext = mock(SecurityContext.class);
        JwtAuthentication auth = new JwtAuthentication(userDTO);
        SecurityContextHolder.setContext(mockContext);

        when(mockContext.getAuthentication()).thenReturn(auth);
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(userRepository.findById(testAdmin.getId())).thenReturn(Optional.of(testAdmin));
        when(userRepository.save(any(User.class))).thenReturn(testAdmin);
        when(passwordEncoder.encode(newUserInfo.getNewPassword().get())).thenReturn("encodedNewPassword");

        // Act
        UserDTO updatedUser = subject.updateUser(testUser.getId(), newUserInfo);

        // Assert
        assertEquals(updatedUser.getId(), testUser.getId());
        assertEquals(updatedUser.getEmail(), newUserInfo.getEmail().get());
        assertTrue(updatedUser.isAdmin());
        verify(mockContext, times(1)).getAuthentication();
        verify(userRepository, times(1)).findById(testUser.getId());
        verify(userRepository, times(1)).findById(testAdmin.getId());
        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(mockContext, userRepository);
    }

    @Test
    public void testUpdateUser_userWantsToChangeHimselfAndCorrectPassword_userSuccessfullyUpdated() {
        // Arrange
        UserUpdateDTO newUserInfo = new UserUpdateDTO(
                Optional.of(testUser.getPassword()),
                Optional.of("newpassword"),
                Optional.of("newmail"),
                Optional.of(User.Status.ACTIVE),
                Optional.empty()
        );

        UserDTO userDTO = UserDTO.fromUser(testUser);
        SecurityContext mockContext = mock(SecurityContext.class);
        JwtAuthentication auth = new JwtAuthentication(userDTO);
        SecurityContextHolder.setContext(mockContext);

        when(mockContext.getAuthentication()).thenReturn(auth);
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(passwordEncoder.matches(eq(newUserInfo.getPassword().get()), any(String.class))).thenReturn(true);
        when(passwordEncoder.encode(newUserInfo.getNewPassword().get())).thenReturn("encodedNewPassword");

        // Act
        UserDTO updatedUser = subject.updateUser(testUser.getId(), newUserInfo);

        // Assert
        assertEquals(updatedUser.getId(), testUser.getId());
        assertEquals(updatedUser.getEmail(), newUserInfo.getEmail().get());
        assertFalse(testUser.isAdmin());
        verify(mockContext, times(1)).getAuthentication();
        verify(userRepository, times(2)).findById(testUser.getId());
        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(mockContext, userRepository);
    }

    @Test
    public void testUpdateUser_userWantsToChangeHimselfAndNoPassword_throwsException() {
        // Arrange
        expectedEx.expect(ResponseStatusException.class);
        expectedEx.expectMessage("You have to provide your password in order to modify your user settings!");

        UserUpdateDTO newUserInfo = new UserUpdateDTO(
                Optional.empty(),
                Optional.of("newpassword"),
                Optional.of("newmail"),
                Optional.of(User.Status.ACTIVE),
                Optional.empty()
        );

        UserDTO userDTO = UserDTO.fromUser(testUser);
        SecurityContext mockContext = mock(SecurityContext.class);
        JwtAuthentication auth = new JwtAuthentication(userDTO);
        SecurityContextHolder.setContext(mockContext);

        when(mockContext.getAuthentication()).thenReturn(auth);
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        // Act
        UserDTO updatedUser = subject.updateUser(testUser.getId(), newUserInfo);

        // Assert - with rule
    }

    @Test
    public void testUpdateUser_userWantsToChangeHimselfAndIncorrectPassword_throwsException() {
        // Arrange
        expectedEx.expect(ResponseStatusException.class);
        expectedEx.expectMessage("The given password is not correct!");

        UserUpdateDTO newUserInfo = new UserUpdateDTO(
                Optional.of("falsePassword"),
                Optional.of("newpassword"),
                Optional.of("newmail"),
                Optional.of(User.Status.ACTIVE),
                Optional.empty()
        );

        UserDTO userDTO = UserDTO.fromUser(testUser);
        SecurityContext mockContext = mock(SecurityContext.class);
        JwtAuthentication auth = new JwtAuthentication(userDTO);
        SecurityContextHolder.setContext(mockContext);

        when(mockContext.getAuthentication()).thenReturn(auth);
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(eq(newUserInfo.getPassword().get()), any(String.class))).thenReturn(false);

        // Act
        UserDTO updatedUser = subject.updateUser(testUser.getId(), newUserInfo);

        // Assert - with rule
    }

    @Test
    public void testUpdateUser_otherUserWantsToChanceExistingUserAndNoAdmin_throwsException() {
        // Arrange
        expectedEx.expect(ResponseStatusException.class);
        expectedEx.expectMessage("You are not allowed to update users!");

        UserUpdateDTO newUserInfo = new UserUpdateDTO(
                Optional.of(testUser.getPassword()),
                Optional.of("newpassword"),
                Optional.of("newmail"),
                Optional.of(User.Status.ACTIVE),
                Optional.empty()
        );

        UserDTO userDTO = UserDTO.fromUser(testUser2);
        SecurityContext mockContext = mock(SecurityContext.class);
        JwtAuthentication auth = new JwtAuthentication(userDTO);
        SecurityContextHolder.setContext(mockContext);

        when(mockContext.getAuthentication()).thenReturn(auth);

        // Act
        UserDTO updatedUser = subject.updateUser(testUser.getId(), newUserInfo);

        // Assert - with rule

    }

    @Test
    public void testUpdateUser_existingUserAndNoAdminAndAdminStatusChange_throwsException() {
        // Arrange
        expectedEx.expect(ResponseStatusException.class);
        expectedEx.expectMessage("You are no admin and not allowed to change the admin status!");

        UserUpdateDTO newUserInfo = new UserUpdateDTO(
                Optional.of(testUser.getPassword()),
                Optional.of("newpassword"),
                Optional.of("newmail"),
                Optional.of(User.Status.ACTIVE),
                Optional.of(true)
        );

        UserDTO userDTO = UserDTO.fromUser(testUser);
        SecurityContext mockContext = mock(SecurityContext.class);
        JwtAuthentication auth = new JwtAuthentication(userDTO);
        SecurityContextHolder.setContext(mockContext);

        when(mockContext.getAuthentication()).thenReturn(auth);
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(eq(newUserInfo.getPassword().get()), any(String.class))).thenReturn(true);

        // Act
        UserDTO updatedUser = subject.updateUser(testUser.getId(), newUserInfo);

        // Assert - with rule

    }

    @Test
    public void testUpdateUser_adminWantsToChangeHisOwnStatus_throwsException() {
        // Arrange
        expectedEx.expect(ResponseStatusException.class);
        expectedEx.expectMessage("You aren't allowed to change your own status.");

        UserUpdateDTO newUserInfo = new UserUpdateDTO(
                Optional.of(testAdmin.getPassword()),
                Optional.of("newpassword"),
                Optional.of("newmail"),
                Optional.of(User.Status.INACTIVE),
                Optional.empty()
        );

        UserDTO userDTO = UserDTO.fromUser(testAdmin);
        SecurityContext mockContext = mock(SecurityContext.class);
        JwtAuthentication auth = new JwtAuthentication(userDTO);
        SecurityContextHolder.setContext(mockContext);

        when(mockContext.getAuthentication()).thenReturn(auth);
        when(userRepository.findById(testAdmin.getId())).thenReturn(Optional.of(testAdmin));

        // Act
        UserDTO updatedUser = subject.updateUser(testAdmin.getId(), newUserInfo);

        // Assert - with rule
    }

    @Test
    public void testUpdateUser_userWantsToChangeHisPasswordAndIncorrectOldPassword_throwsException() {
        // Arrange
        expectedEx.expect(ResponseStatusException.class);
        expectedEx.expectMessage("The given password is not correct!");

        UserUpdateDTO newUserInfo = new UserUpdateDTO(
                Optional.of("falsePassword"),
                Optional.of("newpassword"),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );

        UserDTO userDTO = UserDTO.fromUser(testUser);
        SecurityContext mockContext = mock(SecurityContext.class);
        JwtAuthentication auth = new JwtAuthentication(userDTO);
        SecurityContextHolder.setContext(mockContext);

        when(mockContext.getAuthentication()).thenReturn(auth);
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(eq(newUserInfo.getPassword().get()), eq(testUser2.getPassword()))).thenReturn(false);

        // Act
        UserDTO updatedUser = subject.updateUser(testUser.getId(), newUserInfo);

        // Assert - with rule
    }

}