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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SmartTrackApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class SensorServiceTest extends BaseServiceTest {

    @BeforeEach
    public void init(){
        setUp();
    }

    @Test
    public void deleteSensor() {
        List<Sensor> sensors = iterableToList(sensorService.getAllSensors());
        assertEquals(counterSensors, sensors.size());
        Sensor testSensor = sensors.get(0);

        sensorService.deleteSensor(testSensor);
        counterSensors--;

        List<Sensor> updatedSensors = iterableToList(sensorService.getAllSensors());
        assertFalse(updatedSensors.contains(testSensor));
        assertEquals(counterSensors, updatedSensors.size());
    }

    @Test
    public void deleteNonExistingSensor() {
        Sensor sensor = new Sensor(true, "test");
        assertThrows(RuntimeException.class, () -> sensorService.deleteSensor(sensor));
    }

    @Test
    public void deleteAlreadyDeletedSensor() {
        Sensor sensor = iterableToList(sensorService.getAllSensors()).getFirst();
        sensorService.deleteSensor(sensor);
        assertThrows(RuntimeException.class, () -> sensorService.deleteSensor(sensor));
    }

    @Test
    public void deleteSensorById() {
        Sensor sensor = iterableToList(sensorService.getAllSensors()).getFirst();
        assertEquals(counterSensors, iterableToList(sensorService.getAllSensors()).size());

        sensorService.deleteSensorById(sensor.getId());
        counterSensors--;

        assertEquals(counterSensors, sensorService.countSensors());
        assertTrue(sensorService.getSensorById(sensor.getId()).isEmpty());
    }

    @Test
    public void deleteNonExistingSensorById() {
        Sensor sensor = iterableToList(sensorService.getAllSensors()).getFirst();
        sensorService.deleteSensorById(sensor.getId());
        assertThrows(RuntimeException.class, () -> sensorService.deleteSensorById(sensor.getId()));
    }

    @Test
    public void getDeviceBySensor() {
        Sensor sensor = iterableToList(sensorService.getAllSensors()).getFirst();
        //adding device to sensor
        sensor.setDevice(device1);
        sensorService.updateSensor(sensor);

        Device connectedDevice = sensorService.getDeviceBySensor(sensor);
        assertNotNull(connectedDevice);
        assertTrue(connectedDevice.getSensor().contains(sensor));
    }

    @Test
    public void getNonExistingDeviceBySensor() {
        Sensor sensor = sensorService.registerSensor(new Sensor(true, "test"));
        assertEquals(sensor, iterableToList(sensorService.getAllSensors()).getLast());
        assertThrows(RuntimeException.class, () -> sensorService.getDeviceBySensor(sensor));
    }

    @Test
    public void getDeviceByNonExistingSensor() {
        Sensor sensor = new Sensor(true, "test");
        assertThrows(RuntimeException.class, () -> sensorService.getDeviceBySensor(sensor));
    }

    @Test
    public void getAllSensorsByDevice() {
        Device device = deviceService.getAllDevices().iterator().next();
        List<Sensor> sensors = sensorService.getAllSensorsByDevice(device);
        assertTrue(sensors.stream().allMatch(sensor -> sensor.getDevice().equals(device)));
    }

    @Test
    public void removeAllSensors() {
        List<Sensor> sensors = iterableToList(sensorService.getAllSensors());
        assertFalse(sensors.isEmpty());
        sensorService.deleteAllSensors();
        sensors = iterableToList(sensorService.getAllSensors());
        assertTrue(sensors.isEmpty());
    }

    @Test
    public void removeAllSensorsFromEmptyRepository() {
        List<Sensor> sensors = iterableToList(sensorService.getAllSensors());
        assertFalse(sensors.isEmpty());
        sensorService.deleteAllSensors();
        assertThrows(RuntimeException.class, () -> sensorService.deleteAllSensors());
    }

    @Test
    public void registerNewSensor() {
        assertEquals(counterSensors, sensorService.countSensors());

        Sensor sensor = sensorService.registerSensor(new Sensor(true, "test"));
        counterSensors++;

        assertEquals(counterSensors, sensorService.countSensors());
        assertEquals(sensor, iterableToList(sensorService.getAllSensors()).getLast());
        assertEquals("test", iterableToList(sensorService.getAllSensors()).getLast().getParametersTypes());
    }

    @Test
    public void registerExistingSensor() {
        Sensor sensor = iterableToList(sensorService.getAllSensors()).get(0);
        assertThrows(RuntimeException.class, () -> sensorService.registerSensor(sensor));
    }

    @Test
    public void updateSensor() {
        int position = 0;

        Sensor sensor = iterableToList(sensorService.getAllSensors()).get(position);
        assertEquals(sensors.get(position).getIsCalibrated(), sensor.getIsCalibrated());
        sensor.setIsCalibrated(true);
        assertEquals(true, sensor.getIsCalibrated());
        sensorService.updateSensor(sensor);
        assertEquals(true, iterableToList(sensorService.getAllSensors()).get(position).getIsCalibrated());
    }

    @Test
    public void updateNonExistingSensor() {
        int position = 0;

        Sensor sensor = iterableToList(sensorService.getAllSensors()).get(position);
        sensorService.deleteSensor(sensor);
        counterSensors--;

        assertTrue(sensorService.getSensorById(sensor.getId()).isEmpty());
        sensor.setIsCalibrated(true);
        assertThrows(RuntimeException.class, () -> sensorService.updateSensor(sensor));
    }

    @Test
    public void getSensorsOfEmptyDevice() {
        Device device = new Device();
        ReflectionTestUtils.setField(device, "id", 1L);
        assertThrows(IllegalArgumentException.class, () -> sensorService.getAllSensorsByDevice(device));
    }

    @Test
    public void countSensors() {
        assertEquals(counterSensors, sensorService.countSensors());

        sensorService.registerSensor(new Sensor(true, "test"));
        counterSensors++;

        assertEquals(counterSensors, sensorService.countSensors());
    }

    private List<Sensor> iterableToList(Iterable<Sensor> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
    }

}
