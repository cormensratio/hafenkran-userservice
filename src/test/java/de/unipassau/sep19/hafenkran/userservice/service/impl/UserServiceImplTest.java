package de.unipassau.sep19.hafenkran.userservice.service.impl;

/*import org.junit.runner.RunWith;
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
/*

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
    private User testUser2;
    private User testUser3;
    private User testAdmin;

    @Before
    public void setUp() {
        this.subject = new UserServiceImpl(userRepository, passwordEncoder);
        this.testUser = new User("testUser", "testPassword", "testMail", false);
        this.testUser2 = new User("testUser", "testPassword", "testMail", false);
        this.testUser3 = new User("testUser", "testPassword", "testMail", false);
        this.testAdmin = new User("testAdmin", "testPassword", "testMail", true);
        this.testUser.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        this.testUser2.setId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        this.testUser3.setId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
        this.testAdmin.setId(UUID.fromString("00000000-0000-0000-0000-000000000004"));
        this.testUser.setStatus(User.Status.ACTIVE);
        this.testUser2.setStatus(User.Status.ACTIVE);
        this.testUser3.setStatus(User.Status.INACTIVE);
        this.testAdmin.setStatus(User.Status.ACTIVE);
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
    public void testUpdateUser_existingUser_userSuccessfullyUpdated() {
        // Arrange
        UserUpdateDTO newUserInfo = new UserUpdateDTO(
                testUser.getId(),
                testUser.getPassword(),
                "newpassword",
                "newmail",
                User.Status.ACTIVE,
                true
        );

        UserDTO userDTO = UserDTO.fromUser(testUser);
        SecurityContext mockContext = mock(SecurityContext.class);
        JwtAuthentication auth = new JwtAuthentication(userDTO);
        SecurityContextHolder.setContext(mockContext);

        when(mockContext.getAuthentication()).thenReturn(auth);
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(passwordEncoder.matches(eq(newUserInfo.getPassword()), any(String.class))).thenReturn(true);
        when(passwordEncoder.encode(newUserInfo.getNewPassword())).thenReturn("encodedNewPassword");

        // Act
        UserDTO updatedUser = subject.updateUser(newUserInfo);

        // Assert
        assertEquals(updatedUser.getId(), testUser.getId());
        assertEquals(updatedUser.getEmail(), newUserInfo.getEmail());
        assertFalse(updatedUser.isAdmin());
        verify(mockContext, times(1)).getAuthentication();
        verify(userRepository, times(1)).findById(testUser.getId());
        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(mockContext, userRepository);
    }


    @Test
    public void testUpdateUser_nonExistingUser_throwsException() {
        // Arrange
        expectedEx.expect(ResourceNotFoundException.class);
        UserUpdateDTO newUserInfo = new UserUpdateDTO(
                UUID.fromString("00000000-0000-0000-0000-000000000009"),
                "password",
                "newpassword",
                "newmail",
                User.Status.ACTIVE,
                true
        );

        UserDTO userDTO = UserDTO.fromUser(testUser);
        SecurityContext mockContext = mock(SecurityContext.class);
        JwtAuthentication auth = new JwtAuthentication(userDTO);

        SecurityContextHolder.setContext(mockContext);

        // Act
        subject.updateUser(newUserInfo);

        // Assert - with rule
    }

    @Test
    public void testUpdateUser_updateOtherUserAsNonAdmin() {
        // Arrange
        UserUpdateDTO newUserInfo = new UserUpdateDTO(
                testUser2.getId(),
                testUser2.getPassword(),
                "newpassword",
                "newmail",
                User.Status.INACTIVE,
                false
        );
        expectedEx.expect(ResponseStatusException.class);

        UserDTO userDTO = UserDTO.fromUser(testUser);
        SecurityContext mockContext = mock(SecurityContext.class);
        JwtAuthentication auth = new JwtAuthentication(userDTO);
        SecurityContextHolder.setContext(mockContext);

        // Act
        subject.updateUser(newUserInfo);

        // Assert - with rule
    }

    @Test
    public void testUpdateUser_changedStatusAndAdminSetsStatus_statusSuccessfullyChanged() {

        // Arrange
        UserUpdateDTO newUserInfo = new UserUpdateDTO(
                testUser3.getId(),
                testUser3.getPassword(),
                "testPassword",
                "testMail",
                User.Status.ACTIVE,
                false
        );
        //testUser3.setId(testUser.getId());

        UserDTO userDTO = UserDTO.fromUser(testAdmin);
        SecurityContext mockContext = mock(SecurityContext.class);
        JwtAuthentication auth = new JwtAuthentication(userDTO);
        SecurityContextHolder.setContext(mockContext);

        when(mockContext.getAuthentication()).thenReturn(auth);
        when(userRepository.findById(testUser3.getId())).thenReturn(Optional.of(testUser3));
        when(userRepository.save(testUser3)).thenReturn(testUser3);
        when(passwordEncoder.encode(newUserInfo.getNewPassword())).thenReturn(newUserInfo.getNewPassword());

        // Act
        UserDTO actual = subject.updateUser(newUserInfo);

        // Assert
        assertEquals(actual.getStatus(), testUser3.getStatus());
        assertTrue(userDTO.isAdmin());
        verify(userRepository, times(1)).findById(testUser3.getId());
        verify(userRepository, times(1)).save(testUser3);
    }

    @Test
    public void testUpdateUser_changedAccountIsCurrentAccount_throwsException() {

        // Arrange
        expectedEx.expect(ResponseStatusException.class);
        expectedEx.expectMessage("You aren't allowed to change your own status.");

        UserUpdateDTO newUserInfo = new UserUpdateDTO(
                testAdmin.getId(),
                testAdmin.getPassword(),
                "testPassword",
                "testMail",
                User.Status.INACTIVE,
                true
        );

        UserDTO userDTO = UserDTO.fromUser(testAdmin);
        SecurityContext mockContext = mock(SecurityContext.class);
        JwtAuthentication auth = new JwtAuthentication(userDTO);
        SecurityContextHolder.setContext(mockContext);

        when(mockContext.getAuthentication()).thenReturn(auth);
        when(userRepository.findById(testAdmin.getId())).thenReturn(Optional.of(testAdmin));

        // Act
        UserDTO actual = subject.updateUser(newUserInfo);

        // Assert - with rule

    }

    @Test
    public void testUpdateUser_changedStatusAndUserSetsStatus_throwsException() {

        // Arrange
        expectedEx.expect(ResponseStatusException.class);
        expectedEx.expectMessage("Only admins are allowed to update other users!");

        UserUpdateDTO newUserInfo = new UserUpdateDTO(
                testUser3.getId(),
                testUser3.getPassword(),
                "testPassword",
                "testMail",
                User.Status.ACTIVE,
                false
        );

        UserDTO userDTO = UserDTO.fromUser(testUser2);
        SecurityContext mockContext = mock(SecurityContext.class);
        JwtAuthentication auth = new JwtAuthentication(userDTO);
        SecurityContextHolder.setContext(mockContext);

        when(mockContext.getAuthentication()).thenReturn(auth);
        when(userRepository.findById(testUser3.getId())).thenReturn(Optional.of(testUser3));

        // Act
        UserDTO actual = subject.updateUser(newUserInfo);

        // Assert - with rule

    }

    @Test
    public void testUpdateUser_userWantsToChangeDataAndIncorrectOldPassword_throwsException() {

        // Arrange
        expectedEx.expect(ResponseStatusException.class);
        expectedEx.expectMessage("The given user password is not correct!");

        UserUpdateDTO newUserInfo = new UserUpdateDTO(
                testUser.getId(),
                "password",
                "newPassword",
                "testMail",
                User.Status.ACTIVE,
                false
        );

        UserDTO userDTO = UserDTO.fromUser(testUser);
        SecurityContext mockContext = mock(SecurityContext.class);
        JwtAuthentication auth = new JwtAuthentication(userDTO);
        SecurityContextHolder.setContext(mockContext);

        when(mockContext.getAuthentication()).thenReturn(auth);
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(eq(newUserInfo.getPassword()), any(String.class))).thenReturn(false);

        // Act
        UserDTO actual = subject.updateUser(newUserInfo);

        // Assert - with rule

    }
    
    @Test
    public void testDeleteUser_existingUserAndAdminIsTrue_userSuccessfullyDeleted() {
        
        // Arrange
        UserDTO userDTO = UserDTO.fromUser(testAdmin);
        SecurityContext mockContext = mock(SecurityContext.class);
        JwtAuthentication auth = new JwtAuthentication(userDTO);
        SecurityContextHolder.setContext(mockContext);

        when(mockContext.getAuthentication()).thenReturn(auth);
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        
        // Act
        UserDTO actual = subject.deleteUser(testUser.getId());

        // Assert
        assertEquals(actual.getId(), testUser.getId());
        assertTrue(userDTO.isAdmin());
        verify(userRepository, times(1)).findById(testUser.getId());
        verify(userRepository, times(1)).deleteById(testUser.getId());
    }

    @Test
    public void testDeleteUser_nonExistingUserAndAdminIsTrue_throwsException() {
        // Arrange
        expectedEx.expect(ResourceNotFoundException.class);

        UserDTO userDTO = UserDTO.fromUser(testAdmin);
        SecurityContext mockContext = mock(SecurityContext.class);
        JwtAuthentication auth = new JwtAuthentication(userDTO);
        SecurityContextHolder.setContext(mockContext);

        when(mockContext.getAuthentication()).thenReturn(auth);

        // Act
        UserDTO actual = subject.deleteUser(UUID.fromString("00000000-0000-0000-0000-000000000009"));

        // Assert - with rule
    }

    @Test
    public void testDeleteUser_deleteExistingUserAsNonAdmin_throwsException() {
        // Arrange
        expectedEx.expect(ResponseStatusException.class);
        expectedEx.expectMessage("Only admins are allowed to delete users.");
        
        UserDTO userDTO = UserDTO.fromUser(testUser);
        SecurityContext mockContext = mock(SecurityContext.class);
        JwtAuthentication auth = new JwtAuthentication(userDTO);
        SecurityContextHolder.setContext(mockContext);

        when(mockContext.getAuthentication()).thenReturn(auth);

        // Act
        UserDTO actual = subject.deleteUser(testUser2.getId());

        // Assert - with rule
    }

    @Test
    public void testDeleteUser_deleteOwnAccountAsAdmin_throwsException() {
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
        UserDTO actual = subject.deleteUser(testAdmin.getId());

        // Assert - with rule

    }
}

 */


