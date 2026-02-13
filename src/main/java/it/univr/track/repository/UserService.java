package it.univr.track.repository;

import it.univr.track.entity.Device;
import it.univr.track.entity.UserRegistered;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final DeviceService deviceService;

    @Autowired
    public UserService(UserRepository userRepository, DeviceService deviceService) {
        this.userRepository = userRepository;
        this.deviceService = deviceService;
    }

    private void deleteWithErrorHandling(UserRegistered user) {
        try {
            List<Device> devicesToRemove = deviceService.getDevicesByUser(getUserByUsername(user.getUsername()).get());
            for (Device device : devicesToRemove) {
                deviceService.deleteDevice(device);
            }

        } catch (Exception e) {
            throw new RuntimeException("Could not delete devices for user" + user.getUsername(), e);
        }

        try {
            userRepository.delete(user);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while deleting user: " + user.getUsername(), e);
        }
    }

    public Optional<UserRegistered> getUserByUsername(String username) {
        if (username == "") {
            throw new IllegalArgumentException("Username is empty");
        }

        if (username == null) {
            throw new IllegalArgumentException("Username is null");
        }

        try {
            return userRepository.findByUsername(username);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while retrieving user: " + username, e);
        }
    }

    public Optional<UserRegistered> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<UserRegistered> getUserByEmail(String email) {
        if (email == "") {
            throw new IllegalArgumentException("Email is empty");
        }

        if (email == null) {
            throw new IllegalArgumentException("Email is null");
        }

        try {
            return userRepository.findByEmail(email);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while retrieving user with email: " + email, e);
        }
    }

    public void deleteUserByUsername(String username) {

        if (!userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("User: " + username + " not found");
        }

        deleteWithErrorHandling(getUserByUsername(username).get());
    }

    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User with id: " + id + " not found");
        }

        deleteWithErrorHandling(getUserById(id).get());
    }

    public void deleteUserByEmail(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User: " + email + " not found");
        }

        deleteWithErrorHandling(getUserByEmail(email).get());
    }


    // TODO: optimize
    public void deleteAllUsers() {
        if (!(userRepository.count() > 0)) {
            throw new RuntimeException("There are no users");
        }

        try {
            for (UserRegistered user : userRepository.findAll()) {
                deviceService.getDevicesByUser(user).forEach(deviceService::deleteDevice);
            }
            userRepository.deleteAll();
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while deleting all users", e);
        }
    }

    public UserRegistered registerUser(UserRegistered user) {

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Could not save new user, username: " + user.getUsername() + " already exists.");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Could not save new user, email: " + user.getEmail() + " already exists.");
        }

        try {
            return userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while saving user", e);
        }
    }

    public void updateUser(UserRegistered user) {
        if (!userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Could not update user, user " + user.getUsername() + " does not exist.");
        }

        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while updating user", e);
        }
    }

    public long countUsers() {
        return userRepository.count();
    }
}
