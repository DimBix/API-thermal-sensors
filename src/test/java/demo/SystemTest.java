package demo;

import PageObjects.*;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class SystemTest extends BaseTest {

    @Autowired
    private UserService userService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private SensorService sensorService;

    private final UserRegistered user1 = new UserRegistered("John", "Doe", "JhonD", "password", "jhon@gmail.com", Role.USER, Gender.MALE, "Verona", "123 Main", "123-321-123", "902");
    private final UserRegistered user2 = new UserRegistered("Ada", "Lovelace", "AdaL", "password", "thecreator@gmail.com", Role.ADMIN, Gender.FEMALE, "Padova", "221B Baker street", "123-456-123", "100");

    private final Sensor sensor1 = new Sensor(true, "type1");
    private final Sensor sensor2 = new Sensor( false, "type2");

    private final Device device1 = new Device(new Shipment(), user1, false, true, Origin.BOUGHT, true, 30);
    private final Device device2 = new Device(new Shipment(), user2, false, true, Origin.ASSEMBLED, true, 50);

    private List<UserRegistered> users;
    private List<Device> devices;
    private List<Sensor> sensors;
    private int counterUsers;
    private int counterDevices;
    private int counterSensors;



    @BeforeEach
    public void setUp() {

        super.setUp();

        counterDevices = 0;
        counterUsers = 0;
        counterSensors = 0;

        resetDataBase();

        initListUsers();
        initListSensors();
        initListDevices();

        initUsers();
        initSensors();
        initDevices();
    }


    @Test
    public void testCalibrateDevice() {
        SignInPage signInPage = new SignInPage(driver);
        signInPage.openPage();
        assertEquals("Sign In", signInPage.getTitle());
        EmployeePage employeePage = signInPage.enterCredentialsEmployee("JhonD","password");
        assertEquals("Devices list",employeePage.getHeaderText());
        assertTrue(employeePage.isCalibrateLinkPresentInFirstRow());
        employeePage= employeePage.clickCalibrateLink();
        assertFalse(employeePage.isCalibrateLinkPresentInFirstRow());
    }

    @Test
    public void testShowedConfiguration() {
        SignInPage signInPage = new SignInPage(driver);
        signInPage.openPage();
        assertEquals("Sign In",signInPage.getTitle());
        EmployeePage employeePage = signInPage.enterCredentialsEmployee("JhonD","password");
        assertEquals("Devices list",employeePage.getHeaderText());
        ConfigDevicePage configDevicePage = employeePage.clickShowConfigFirstRow();

        Long jhonDeviceId = getDeviceId("JhonD");

        assertEquals(jhonDeviceId.toString(), configDevicePage.getDeviceId(), "Device id mismatch");
        employeePage = configDevicePage.clickOnDeviceList();
        assertEquals("Devices list",employeePage.getHeaderText());
    }

    @Test
    public void editConfigurationFirstDevice() {
        SignInPage signInPage = new SignInPage(driver);
        signInPage.openPage();
        EmployeePage employeePage = signInPage.enterCredentialsEmployee("JhonD","password");

        EditConfigDevicePage editPage = employeePage.clickEditConfigFirstRow();
        Long jhonDeviceId = getDeviceId("JhonD");
        assertEquals(jhonDeviceId.toString(), editPage.getDeviceId(), "Device id mismatch");

        int counter = deviceService.getDeviceById(device1.getId()).get().getSensor().size();
        assertEquals(counter, editPage.getSensorsCount());

        //remove first sensor
        editPage = editPage.clickConfirmRemoveDevice(sensors.getFirst().getId());
        editPage = editPage.clickSendConfiguration();

        employeePage = editPage.clickBackToList();
        assertEquals("Devices list", employeePage.getHeaderText());

        editPage = employeePage.clickEditConfigFirstRow();

        counter = deviceService.getDeviceById(device1.getId()).get().getSensor().size();
        assertEquals(counter, editPage.getSensorsCount() );
    }

    @Test
    public void deleteFirstDevice() {
        SignInPage signInPage = new SignInPage(driver);
        signInPage.openPage();
        EmployeePage employeePage = signInPage.enterCredentialsEmployee("JhonD","password");
        assertEquals("Devices list",employeePage.getHeaderText());

        assertEquals(countDevicesForUser("JhonD"), employeePage.getTotalDeviceRows());
        employeePage = employeePage.clickDeleteLink();
        assertEquals("Devices list",employeePage.getHeaderText());
        assertEquals(countDevicesForUser("JhonD"), employeePage.getTotalDeviceRows());
    }

    @Test
    public void managerPageView()
    {
        SignInPage signInPage = new SignInPage(driver);
        signInPage.openPage();
        ManagerPage managerPage = signInPage.enterCredentialManager("AdaL","password");
        assertEquals("Devices Inventory",managerPage.getTitle());

        assertEquals(device1.getId().toString(),managerPage.getFirstDeviceId());
        assertEquals(sensor1.getId().toString(),managerPage.getFirstSensorId());
    }

    private int countDevicesForUser(String username) {
        UserRegistered user = userService.getUserByUsername(username).get();
        List<Device> devices = deviceService.getDevicesByUser(user);
        return devices.size();
    }

    private Long getDeviceId(String username){
        Long id = (long) 0;

        for(Device device : devices){
            if(device.getUser().getUsername().equals(username)){
                id = device.getId();
            }
        }
        return id;
    }

    private void resetDataBase() {
        if(deviceService.countDevices() != 0){
            System.out.println("Watch out the data base is not empty! All devices will be deleted.");
            deviceService.deleteAllDevices();
            assertEquals(0,deviceService.countDevices(), "Deletion of devices failed");
        }

        if(userService.countUsers() != 0){
            System.out.println("Watch out the data base is not empty! All users will be deleted.");
            userService.deleteAllUsers();
            assertEquals(0, userService.countUsers(), "Deletion of users failed");
        }

        if(sensorService.countSensors() != 0){
            System.out.println("Watch out the data base is not empty! All sensors will be deleted.");
            sensorService.deleteAllSensors();
            assertEquals(0,sensorService.countSensors(), "Deletion of sensors failed");
        }
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
            deviceService.registerDevice(device);

            if (device.getUser().getUsername().equals("JhonD")) {
                deviceService.addSensorForDevice(sensor1, device);
            }

            counterDevices++;
        }
    }

    public void initUsers() {
        for(UserRegistered user : users) {
            userService.registerUser(user);
            counterUsers++;
        }
    }

    public void initSensors() {
        for (Sensor sensor : sensors) {
            sensorService.registerSensor(sensor);
            counterSensors++;
        }
    }
}
