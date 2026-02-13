package demo;

import it.univr.track.SmartTrackApplication;
import it.univr.track.entity.Device;
import it.univr.track.entity.Sensor;
import it.univr.track.entity.Shipment;
import it.univr.track.entity.UserRegistered;
import it.univr.track.entity.enumeration.Gender;
import it.univr.track.entity.enumeration.Origin;
import it.univr.track.entity.enumeration.Role;
import it.univr.track.repository.DeviceService;
import it.univr.track.repository.SensorService;
import it.univr.track.repository.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SmartTrackApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class BaseServiceTest {

    @Autowired
    protected UserService userService;

    @Autowired
    protected DeviceService deviceService;

    @Autowired
    protected SensorService sensorService;

    // IF YOU ADD A USER/SENSOR/DEVICE MAKE SURE TO INSERT IT IN THE LIST --> method initListUsers()
    protected final UserRegistered user1 = new UserRegistered("John", "Doe", "JhonD", "password", "jhon@gmail.com", Role.USER, Gender.MALE, "Verona", "123 Main", "123-321-123", "902");
    protected final UserRegistered user2 = new UserRegistered("Ada", "Lovelace", "AdaL", "password", "thecreator@gmail.com", Role.ADMIN, Gender.FEMALE, "Padova", "221B Baker street", "123-456-123", "100");

    protected final Sensor sensor1 = new Sensor(true, "type1");
    protected final Sensor sensor2 = new Sensor(false, "type2");

    protected final Device device1 = new Device(new Shipment(), user1, false, true, Origin.BOUGHT, true, 30);
    protected final Device device2 = new Device(new Shipment(), user2, false, true, Origin.ASSEMBLED, true, 50);


    protected List<UserRegistered> users;
    protected List<Device> devices;
    protected List<Sensor> sensors;
    protected int counterUsers;
    protected int counterDevices;
    protected int counterSensors;

    protected void setUp(){
        counterDevices = 0;
        counterUsers = 0;
        counterSensors = 0;

        resetDataBase();

        initListUsers();
        initListDevices();
        initListSensors();

        initUsers();
        initDevices();
        initSensors();
    }


    private void initListUsers() {
        users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
    }

    private void initListSensors() {
        sensors = new ArrayList<>();
        sensors.add(sensor1);
        sensors.add(sensor2);
    }

    private void initListDevices() {
        devices = new ArrayList<>();
        devices.add(device1);
        devices.add(device2);
    }

    private void initDevices() {
        for (Device device : devices) {
            assertEquals(counterDevices, deviceService.countDevices(), "Devices in the database should be " + counterDevices + " while it is: " + deviceService.countDevices());
            assertNotNull(device, "Device should not be null");
            assertNotNull(deviceService.registerDevice(device), "Something went wrong while creating the type Device");
            counterDevices++;
        }
    }

    private void initUsers() {
        for (UserRegistered user : users) {
            assertEquals(counterUsers, userService.countUsers(), "During registration of users, Users count should be " + counterUsers + " while it is: " + userService.countUsers());
            assertNotNull(user, "Something went wrong while creating the type UserRegistered");
            assertNotNull(userService.registerUser(user), "Something went wrong, registration of user: " + user.getUsername() + " returned NULL");
            counterUsers++;
        }
    }

    private void initSensors() {
        for (Sensor sensor : sensors) {
            assertEquals(counterSensors, sensorService.countSensors(), "During registration of sensors, Sensor count should be " + counterSensors + " while it is: " + sensorService.countSensors());
            assertNotNull(sensor, "Something went wrong while creating the type Sensor");
            assertNotNull(sensorService.registerSensor(sensor), "Something went wrong, registration of sensor returned NULL");
            counterSensors++;
        }
    }

    private void resetDataBase() {
        if (deviceService.countDevices() != 0) {
            System.out.println("Watch out the data base is not empty! All devices will be deleted.");
            deviceService.deleteAllDevices();
            assertEquals(0, deviceService.countDevices(), "Deletion of devices failed");
        }

        if (userService.countUsers() != 0) {
            System.out.println("Watch out the data base is not empty! All users will be deleted.");
            userService.deleteAllUsers();
            assertEquals(0, userService.countUsers(), "Deletion of users failed");
        }

        if (sensorService.countSensors() != 0) {
            System.out.println("Watch out the data base is not empty! All sensors will be deleted.");
            sensorService.deleteAllSensors();
            assertEquals(0, sensorService.countSensors(), "Deletion of sensors failed");
        }
    }

    protected void assertDeviceExists(Device device) {
        assertNotNull(deviceService.getDeviceById(device.getId()), "Device should exist before deletion");
    }

    protected void assertDeviceNotExists(Device device) {
        assertFalse(deviceService.getDeviceById(device.getId()).isPresent(), "Device should have been deleted");
    }

    protected void assertUserExists(UserRegistered user) {
        assertNotNull(userService.getUserByEmail(user.getEmail()), "User should exist before deletion: " + user.getUsername());
    }

    protected void assertUserNotExists(UserRegistered user) {
        assertFalse(userService.getUserByEmail(user.getEmail()).isPresent(), "User should have been deleted: " + user.getUsername());
    }
}
