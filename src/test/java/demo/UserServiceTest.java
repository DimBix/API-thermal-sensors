package demo;

import it.univr.track.SmartTrackApplication;
import it.univr.track.entity.Device;
import it.univr.track.entity.Shipment;
import it.univr.track.entity.UserRegistered;
import it.univr.track.entity.enumeration.Gender;
import it.univr.track.entity.enumeration.Origin;
import it.univr.track.entity.enumeration.Role;
import it.univr.track.repository.DeviceService;
import it.univr.track.repository.SensorService;
import it.univr.track.repository.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SmartTrackApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserServiceTest extends BaseServiceTest {

    @BeforeEach
    public void init(){
        setUp();
    }

    @Test
    public void testCreateUser() {
        UserRegistered user = new UserRegistered("John", "Doe", "JhonD", "password", "jhon@gmail.com", Role.USER, Gender.MALE, "Verona", "123 Main", "123-321-123", "902");
        assertNotNull(user, "There was a problem while creating a new user, it should not be null");
    }

    @Test
    public void testRegisterUser() {
        assertEquals(counterUsers, userService.countUsers(), "During registration of users, Users count should be " + counterUsers + " while it is: " + userService.countUsers());
        UserRegistered user = new UserRegistered("Eddie", "Miller", "edd", "password", "eddie@gmail.com", Role.USER, Gender.MALE, "Verona", "123 Main", "123-321-123", "902");
        assertNotNull(userService.registerUser(user), "Something went wrong, registration of user: " + user.getUsername() + " returned NULL");

        //same email
        user = new UserRegistered("Eddie", "Miller", "carlo", "password", "eddie@gmail.com", Role.USER, Gender.MALE, "Verona", "123 Main", "123-321-123", "902");
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(users.getFirst()), "No exception has been thrown while trying to register a user already with the same email");

        //same username
        user = new UserRegistered("Eddie", "Miller", "eddie", "password", "ecarl@gmail.com", Role.USER, Gender.MALE, "Verona", "123 Main", "123-321-123", "902");
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(users.getFirst()), "No exception has been thrown while trying to register a user already with the same username");

        //NULL
        assertThrows(RuntimeException.class, () -> userService.registerUser(null), "No exception has been thrown while trying to register a user null");
    }

    @Test
    public void testCountUsers() {
        assertEquals(counterDevices, deviceService.countDevices(), "Incorrect number of devices, countDevices method is incorrect: " + deviceService.countDevices() + " while it should be: " + counterDevices);
    }

    @Test
    public void testCheckUsersParameters() {
        UserRegistered userTested;
        for (UserRegistered user : users) {
            userTested = userService.getUserById(user.getId()).get();

            assertEquals(user.getUsername(), userTested.getUsername(), "Username should be " + user.getUsername() + " while it is: " + userTested.getUsername());
            assertEquals(user.getFirstname(), userTested.getFirstname(), "Firstname should be " + user.getFirstname() + " while it is: " + userTested.getFirstname());
            assertEquals(user.getLastname(), userTested.getLastname(), "Lastname should be " + user.getLastname() + " while it is: " + userTested.getLastname());
            assertEquals(user.getRole(), userTested.getRole(), "Role should be " + user.getRole() + " while it is: " + userTested.getRole());
            assertEquals(user.getGender(), userTested.getGender(), "Gender should be " + user.getGender() + " while it is: " + userTested.getGender());
            assertEquals(user.getCity(), userTested.getCity(), "City should be " + user.getCity() + " while it is: " + userTested.getCity());
            assertEquals(user.getAddress(), userTested.getAddress(), "Address should be " + user.getAddress() + " while it is: " + userTested.getAddress());
            assertEquals(user.getTelephoneNumber(), userTested.getTelephoneNumber(), "Phone number should be " + user.getTelephoneNumber() + " while it is: " + userTested.getTelephoneNumber());
            assertEquals(user.getTaxIdentificationNumber(), userTested.getTaxIdentificationNumber(), "Postal code should be " + user.getTaxIdentificationNumber() + " while it is: " + userTested.getTaxIdentificationNumber());
        }
    }


    @Test
    public void testGetUserById() {
        String email;
        for (UserRegistered user : users) {
            email = userService.getUserById(user.getId()).get().getEmail();
            assertEquals(user.getEmail(), email, "The value returned by getUserById is: " + email + " while it should be: " + user.getEmail());
        }

        //NULL
        assertThrows(RuntimeException.class, () -> userService.getUserById(null), "No exception has been thrown while trying to retrieve a user with ID null");
    }

    @Test
    public void testGetUserByEmail() {
        Long id;
        for (UserRegistered user : users) {
            id = userService.getUserByEmail(user.getEmail()).get().getId();
            assertEquals(user.getId(), id, "The value returned by getUserByEmail is: " + id + " while it should be: " + user.getId());
        }

        //empty string
        assertThrows(IllegalArgumentException.class, () -> userService.getUserByEmail(""), "No exception has been thrown while trying to retrieve a user with email empty");

        //NULL
        assertThrows(IllegalArgumentException.class, () -> userService.getUserByEmail(null), "No exception has been thrown while trying to retrieve a user with email null");
    }

    @Test
    public void testGetUserByUsername() {
        Long id;
        for (UserRegistered user : users) {
            id = userService.getUserByUsername(user.getUsername()).get().getId();
            assertEquals(user.getId(), id, "The value returned by getUserByUsername is: " + id + " while it should be: " + user.getId());
        }

        //empty string
        assertThrows(IllegalArgumentException.class, () -> userService.getUserByUsername(""), "No exception has been thrown while trying to retrieve a user with username empty");

        //NULL
        assertThrows(IllegalArgumentException.class, () -> userService.getUserByUsername(null), "No exception has been thrown while trying to retrieve a user with username null");
    }

    @Test
    public void testUpdateUser() {
        for (UserRegistered user : users) {
            String newFirstname = "Diablo";
            user.setFirstname(newFirstname);
            userService.updateUser(user);
            assertEquals("Diablo", user.getFirstname(), "After update, firstname should be " + newFirstname + " while it is: " + user.getFirstname());
        }

        //user is null
        assertThrows(RuntimeException.class, () -> userService.updateUser(null), "No exception has been thrown while trying to update a user null");

    }

    @Test
    public void testDeleteUserByEmail() {
        for (UserRegistered user : users) {
            assertUserExists(user);
            userService.deleteUserByEmail(user.getEmail());
            assertUserNotExists(user);
        }

        //email does not exist
        assertThrows(IllegalArgumentException.class, () -> userService.deleteUserByEmail("obama"), "No exception has been thrown while trying to delete a user with email non existing");

        //NULL
        assertThrows(Exception.class, () -> userService.deleteUserByEmail(null), "No exception has been thrown while trying to retrieve a user with email null");
    }

    @Test
    public void testDeleteUserByUsername() {
        for (UserRegistered user : users) {
            assertUserExists(user);
            userService.deleteUserByUsername(user.getUsername());
            assertUserNotExists(user);
        }

        //username does not exist
        assertThrows(IllegalArgumentException.class, () -> userService.deleteUserByUsername("testUsername@gmail.com351"), "No exception has been thrown while trying to delete a user with username non existing");

        //NULL
        assertThrows(Exception.class, () -> userService.deleteUserByUsername(null), "No exception has been thrown while trying to retrieve a user with username null");
    }

    @Test
    public void testDeleteUserById() {
        for (UserRegistered user : users) {
            assertUserExists(user);
            userService.deleteUserById(user.getId());
            assertUserNotExists(user);
        }

        //id does not exist
        Long id = (long) 1000000;
        assertThrows(IllegalArgumentException.class, () -> userService.deleteUserById(id), "No exception has been thrown while trying to delete a user with id non existing");

        //NULL
        assertThrows(Exception.class, () -> userService.deleteUserById(null), "No exception has been thrown while trying to retrieve a user with id null");
    }

    @Test
    public void testDeleteAllUsers() {
        for (UserRegistered user : users) {
            assertUserExists(user);
        }

        userService.deleteAllUsers();

        for (UserRegistered user : users) {
            assertUserNotExists(user);
        }

        assertThrows(RuntimeException.class, () -> userService.deleteAllUsers(), "No exception has been thrown while trying to delete all users while none are registered");
    }
}
