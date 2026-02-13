package demo;

import it.univr.track.entity.UserRegistered;
import it.univr.track.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceMockTest {
    @Mock
    private DeviceRepository deviceRepository;
    @Mock
    private SensorRepository sensorRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private DeviceService deviceService;
    @InjectMocks
    private UserService userService;

    @Test
    public void getUserByUsername_UnexpectedError() {
        doThrow(new RuntimeException("Test message")).when(userRepository).findByUsername(any());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.getUserByUsername("Username"));

        assertEquals("An error occurred while retrieving user: Username", exception.getMessage());
        assertEquals("Test message", exception.getCause().getMessage());
    }

    @Test
    public void getUserByEmail_ExpectedError() {
        doThrow(new RuntimeException("Test message")).when(userRepository).findByEmail(any());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.getUserByEmail("Email"));

        assertEquals("An error occurred while retrieving user with email: Email", exception.getMessage());
        assertEquals("Test message", exception.getCause().getMessage());
    }

    @Test
    public void deleteWithErrorHandling_UnexpectedError() {
        Long userId = 1L;
        String username = "Test";
        UserRegistered user = mock(UserRegistered.class);

        when(user.getUsername()).thenReturn(username);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        doThrow(new RuntimeException("Test message")).when(deviceService).getDevicesByUser(any());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.deleteUserById(userId));

        assertEquals("Could not delete devices for user" + username, exception.getMessage());
        assertEquals("Test message", exception.getCause().getMessage());
    }

    @Test
    public void deleteWithErrorHandling_UnexpectedError2() {
        Long userId = 1L;
        String username = "Test";
        UserRegistered user = mock(UserRegistered.class);

        when(user.getUsername()).thenReturn(username);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(deviceService.getDevicesByUser(user)).thenReturn(java.util.List.of());

        doThrow(new RuntimeException("Test message")).when(userRepository).delete(any());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.deleteUserById(userId));

        assertEquals("An error occurred while deleting user: " + username, exception.getMessage());
        assertEquals("Test message", exception.getCause().getMessage());
    }

    @Test
    public void deleteAllUsers_UnexpectedError() {
        when(userRepository.count()).thenReturn(5L);
        doThrow(new RuntimeException("Test message")).when(userRepository).deleteAll();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.deleteAllUsers());

        assertEquals("An error occurred while deleting all users", exception.getMessage());
        assertEquals("Test message", exception.getCause().getMessage());
    }

    @Test
    public void registerUser_UnexpectedError() {
        UserRegistered userRegistered = mock(UserRegistered.class);
        when(userRegistered.getUsername()).thenReturn("Username");
        when(userRegistered.getEmail()).thenReturn("Email");
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        doThrow(new RuntimeException("Test message")).when(userRepository).save(any());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.registerUser(userRegistered));

        assertEquals("An error occurred while saving user", exception.getMessage());
        assertEquals("Test message", exception.getCause().getMessage());
    }

    @Test
    public void updateUser_UnexpectedError() {
        UserRegistered userRegistered = mock(UserRegistered.class);
        when(userRegistered.getUsername()).thenReturn("Username");
        when(userRepository.existsByUsername(any())).thenReturn(true);
        doThrow(new RuntimeException("Test message")).when(userRepository).save(any());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.updateUser(userRegistered));

        assertEquals("An error occurred while updating user", exception.getMessage());
        assertEquals("Test message", exception.getCause().getMessage());
    }
}
