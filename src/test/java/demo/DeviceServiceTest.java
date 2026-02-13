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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SmartTrackApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DeviceServiceTest extends BaseServiceTest {


    @BeforeEach
    public void init() {
        setUp();
    }

    @Test
    public void testCreateBaseDevice() {
        Device device = new Device(new Shipment(), user2, false, true, Origin.ASSEMBLED, true, 50);
        assertNotNull(device, "There was a problem while creating a new basic device (no sensor needed), it should not be null");
    }

    @Test
    public void testCreateDeviceWithSensor() {
        Sensor sensor = new Sensor(false, "type2");
        Device device = new Device(new Shipment(), user2, sensor, false, true, Origin.ASSEMBLED, true, 50);
        assertNotNull(device, "There was a problem while creating a new device (only one sensor needed), it should not be null");
    }

    @Test
    public void testCreateDeviceWithMultipleSensors() {
        Sensor sensor1 = new Sensor(false, "type2");
        Sensor sensor2 = new Sensor(false, "type2");
        List<Sensor> sensors = new ArrayList<>();
        sensors.add(sensor1);
        sensors.add(sensor2);
        Device device = new Device(new Shipment(), user2, sensors, false, true, Origin.ASSEMBLED, true, 50);
        assertNotNull(device, "There was a problem while creating a new device (multiple sensors needed), it should not be null");
    }

    @Test
    public void testCountDevices() {
        assertEquals(counterDevices, deviceService.countDevices(), "Incorrect number of devices, countDevices method is incorrect: " + deviceService.countDevices() + " while it should be: " + counterDevices);
    }

    @Test
    public void testGetDeviceById() {
        Device deviceTested;
        for (Device device : devices) {
            //normal case
            deviceTested = deviceService.getDeviceById(device.getId()).get();
            assertEquals(device.getId(), deviceTested.getId(), "Device should be the same but the ids differ: " + deviceTested.getId() + " while it should be: " + device.getId());
        }

        //negative case
        Long negativeId = (long) -1;
        assertThrows(IllegalArgumentException.class, () -> deviceService.getDeviceById(negativeId), "No exception has been thrown with input negativeId: " + negativeId);

        //NULL case
        assertThrows(Exception.class, () -> deviceService.getDeviceById(null), "No exception has been thrown with input null");

    }

    @Test
    public void testGetDevicesByUser() {
        List<Device> deviceTested;
        for (UserRegistered user : users) {
            deviceTested = deviceService.getDevicesByUser(user);
            assertNotNull(deviceTested, "There was a problem while getting devices by user, it should not be null");

            for (Device dev : deviceTested) {
                assertEquals(user.getUsername(), dev.getUser().getUsername(), "The device returned has as a owner the user: " + dev.getUser().getUsername() + " while it should be: " + user.getUsername());
            }
        }

        assertThrows(IllegalArgumentException.class, () -> deviceService.getDevicesByUser(null), "No exception has been thrown with input null");
    }


    @Test
    public void testCheckDevicesParameters() {
        Device deviceTest;
        for (Device device : devices) {
            deviceTest = deviceService.getDeviceById(device.getId()).get();

            assertEquals(device.getUser().getUsername(), deviceTest.getUser().getUsername(), "User should be " + device.getUser().getUsername() + " while it is: " + deviceTest.getUser().getUsername());
            assertEquals(device.getPrice(), deviceTest.getPrice(), "Price should be " + device.getPrice() + " while it is: " + deviceTest.getPrice());
            assertEquals(device.getIsCalibrated(), deviceTest.getIsCalibrated(), "IsCalibrated should be set to " + device.getIsCalibrated() + " while it is: " + deviceTest.getIsCalibrated());
            assertEquals(device.getState(), deviceTest.getState(), "State should be " + device.getState() + " while it is: " + deviceTest.getState());
            assertEquals(device.getIsConnected(), deviceTest.getIsConnected(), "IsConnected should be " + device.getIsConnected() + " while it is: " + deviceTest.getIsConnected());
            assertEquals(device.getOrigin(), deviceTest.getOrigin(), "Origin should be " + device.getOrigin() + " while it is: " + deviceTest.getOrigin());
        }
    }

    @Test
    public void testDeleteDeviceById() {
        for (Device device : devices) {
            //Normal case
            assertDeviceExists(device);
            deviceService.deleteDeviceById(device.getId());
            assertDeviceNotExists(device);
        }

        //Non existing ID
        Long nonExistentId = (long) 1000000;
        assertThrows(RuntimeException.class, () -> deviceService.deleteDeviceById(nonExistentId), "No exception has been thrown with input ID nonExistentId: " + nonExistentId);

        //NULL value
        assertThrows(Exception.class, () -> deviceService.deleteDeviceById(null), "No exception has been thrown with input ID null");

    }

    @Test
    public void testDeleteDevice() {
        for (Device device : devices) {
            assertDeviceExists(device);
            deviceService.deleteDevice(device);
            assertDeviceNotExists(device);
        }

        //Non existing ID
        Device deviceTest = new Device(new Shipment(), user1, false, true, Origin.BOUGHT, true, 30);
        assertThrows(RuntimeException.class, () -> deviceService.deleteDevice(deviceTest), "No exception has been thrown with input a device with ID non existing");

        //NULL value
        assertThrows(Exception.class, () -> deviceService.deleteDevice(null), "No exception has been thrown with device null");
    }

    @Test
    public void testDeleteAllDevices() {
        for (Device device : devices) {
            assertDeviceExists(device);
        }

        deviceService.deleteAllDevices();

        for (Device device : devices) {
            assertDeviceNotExists(device);
        }

        assertThrows(Exception.class, () -> deviceService.deleteAllDevices(), "No exception has been thrown with no devices to delete");
    }

    @Test
    public void testRegisterDevice() {
        Device device = new Device(new Shipment(), user2, false, true, Origin.ASSEMBLED, true, 50);
        assertNotNull(deviceService.registerDevice(device), "Device has not been registered correctly");

        assertThrows(IllegalArgumentException.class, () -> deviceService.registerDevice(devices.getFirst()), "No exception has been thrown while trying to register a device with an ID");

        assertThrows(Exception.class, () -> deviceService.registerDevice(null), "No exception has been thrown while trying to register a device null");
    }

    @Test
    public void testGetSensorsByDevice() {
        List<Sensor> listOfSensors;
        for (Device device : devices) {
            deviceService.addSensorForDevice(sensors, device);
            listOfSensors = deviceService.getSensorsByDevice(device);
            assertNotNull(listOfSensors, "The sensor should not be null");
        }

        Device deviceTest = new Device(new Shipment(), user1, false, true, Origin.BOUGHT, true, 30);
        assertThrows(RuntimeException.class, () -> deviceService.getSensorsByDevice(deviceTest), "No exception has been thrown while trying to recover a the sensors of a device without ID");

        assertThrows(Exception.class, () -> deviceService.getSensorsByDevice(null), "No exception has been thrown while trying to recover the sensors of a device null");
    }

    @Test
    public void testAddSensorForDevice() {
        int i;
        for (Device device : devices) {
            i = devices.indexOf(device);
            deviceService.addSensorForDevice(sensors.get(i), device);
            List<Sensor> sensors = deviceService.getDeviceById(device.getId()).get().getSensor();
            assertNotNull(sensors, "The sensor should not be null");
            for (Sensor sensor : sensors) {
                assertEquals(sensor.getDevice().getId(), device.getId(), "A sensor has the wrong device");
            }
        }

        //Device id null
        Sensor sensor = new Sensor(false, "type2");
        Device device = new Device(new Shipment(), user1, false, true, Origin.BOUGHT, true, 30);
        assertThrows(RuntimeException.class, () -> deviceService.addSensorForDevice(sensor, device), "No exception has been thrown while trying to add a sensor to a device not registered");

        //sensor id null
        deviceService.registerDevice(device);
        assertThrows(RuntimeException.class, () -> deviceService.addSensorForDevice(sensor, device), "No exception has been thrown while trying to add a sensor not registered to a device");
    }

    @Test
    public void testAddSensorsToDevice() {
        for (Device device : devices) {
            deviceService.addSensorForDevice(sensors, device);
            List<Sensor> sensors = deviceService.getDeviceById(device.getId()).get().getSensor();
            assertNotNull(sensors, "The sensors should not be null");
            for (Sensor sensor : sensors) {
                assertEquals(sensor.getDevice().getId(), device.getId(), "A sensor has the wrong device");
            }
        }

        //Device id null
        Sensor sensor = new Sensor(false, "type2");
        Device device = new Device(new Shipment(), user1, false, true, Origin.BOUGHT, true, 30);
        assertThrows(RuntimeException.class, () -> deviceService.addSensorForDevice(sensor, device), "No exception has been thrown while trying to add a sensor to a device not registered");

        //sensor id null
        deviceService.registerDevice(device);
        assertThrows(RuntimeException.class, () -> deviceService.addSensorForDevice(sensor, device), "No exception has been thrown while trying to add a sensor not registered to a device");
    }

    @Test
    public void testRemoveSensorsFromDevice() {
        int i;

        List<Long> idsToRemove = new ArrayList<>();
        for (Sensor sensor : sensors) {
            idsToRemove.add(sensor.getId());
        }

        for (Device device : devices) {
            i = devices.indexOf(device);
            deviceService.addSensorForDevice(sensors.get(i), device);
            deviceService.removeSensorsFromDevice(device.getId(), idsToRemove);
            assertTrue(deviceService.getDeviceById(device.getId()).get().getSensor().isEmpty(), "The sensor should have been removed, while it is not empty");
        }

        assertThrows(RuntimeException.class, () -> deviceService.removeSensorsFromDevice(null, null), "No exception has been thrown while trying to remove a sensor with ID NUll");
    }

    @Test
    public void testCalibrateDeviceAndSensor() {
        for (Device device : devices) {
            deviceService.calibrateDeviceAndSensors(device.getId());
            assertTrue(deviceService.getDeviceById(device.getId()).get().getIsCalibrated(), "The device has not been calibrated");

            List<Sensor> sensors = device.getSensor();
            for (Sensor sensor : sensors) {
                assertTrue(sensorService.getSensorById(sensor.getId()).get().getIsCalibrated(), "The sensor has not been calibrated");
            }
        }

        //device ID NULL
        assertThrows(RuntimeException.class, () -> deviceService.calibrateDeviceAndSensors(null), "No exception has been thrown while trying to calibrate a device with ID null");
    }


    @Test
    public void testGetAllDevices() {
        Iterable<Device> devices = deviceService.getAllDevices();
        int count = 0;
        for (Device device : devices) {
            assertNotNull(device, "The device should not be null");
            count++;
        }
        assertEquals(counterDevices, count, "Incorrect number of devices returned");
    }

}
