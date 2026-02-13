package demo;

import it.univr.track.entity.Device;
import it.univr.track.entity.Sensor;
import it.univr.track.entity.UserRegistered;
import it.univr.track.repository.DeviceRepository;
import it.univr.track.repository.DeviceService;
import it.univr.track.repository.SensorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeviceServiceMockTest {
    @Mock
    private DeviceRepository deviceRepository;
    @Mock
    private SensorRepository sensorRepository;
    @InjectMocks
    private DeviceService deviceService;

    @Test
    public void deleteDevice_DatabaseError() {
        Device device = mock(Device.class);
        when(device.getId()).thenReturn(1L);
        when(deviceRepository.existsById(any())).thenReturn(true);
        doThrow(new RuntimeException("Test message")).when(deviceRepository).delete(any());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> deviceService.deleteDevice(device));

        assertEquals("An error occurred while deleting device: 1", exception.getMessage());
        assertEquals("Test message", exception.getCause().getMessage());
    }

    @Test
    public void getDeviceById_UnexpectedError() {
        doThrow(new RuntimeException("Test message")).when(deviceRepository).findById(any());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> deviceService.getDeviceById(1L));

        assertEquals("An error occurred while retrieving the device for sensor:1", exception.getMessage());
        assertEquals("Test message", exception.getCause().getMessage());
    }

    @Test
    public void getDevicesByUser_UnexpectedError() {
        UserRegistered user =  new UserRegistered();
        ReflectionTestUtils.setField(user, "username", "test");
        doThrow(new RuntimeException("Test message")).when(deviceRepository).findByUser(any());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> deviceService.getDevicesByUser(user));

        assertEquals("An error occurred while retrieving the device for user: test", exception.getMessage());
        assertEquals("Test message", exception.getCause().getMessage());
    }

    @Test
    public void deleteAllDevices_UnexpectedError() {
        when(deviceRepository.count()).thenReturn(1L);
        doThrow(new RuntimeException("Test message")).when(deviceRepository).deleteAll();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> deviceService.deleteAllDevices());

        assertEquals("An error occurred while deleting all devices", exception.getMessage());
        assertEquals("Test message", exception.getCause().getMessage());
    }

    @Test
    public void registerDevice_UnexpectedError() {
        doThrow(new RuntimeException("Test message")).when(deviceRepository).save(any());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> deviceService.registerDevice(new Device()));

        assertEquals("An unexpected error occurred while saving the device.", exception.getMessage());
        assertEquals("Test message", exception.getCause().getMessage());
    }

    @Test
    public void updateDevice_UnexpectedError() {
        when(deviceRepository.existsById(any())).thenReturn(true);
        doThrow(new RuntimeException("Test message")).when(deviceRepository).save(any());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> deviceService.updateDevice(new Device()));

        assertEquals("An unexpected error occurred while updating the device.", exception.getMessage());
        assertEquals("Test message", exception.getCause().getMessage());
    }

    @Test
    public void updateDevice_DatabaseError() {
        when(deviceRepository.existsById(any())).thenReturn(true);
        doThrow(new DataRetrievalFailureException("Test message")).when(deviceRepository).save(any());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> deviceService.updateDevice(new Device()));

        assertEquals("Failed to update device due to data access issue.", exception.getMessage());
        assertEquals("Test message", exception.getCause().getMessage());
    }

    @Test
    public void getSensorsByDevice_UnexpectedError() {
        Device device = mock(Device.class);
        ReflectionTestUtils.setField(device, "id", 1L);
        when(deviceRepository.existsById(any())).thenReturn(true);
        doThrow(new RuntimeException("Test message")).when(device).getSensor();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> deviceService.getSensorsByDevice(device));

        assertEquals("Something went wrong while retrieving the list of sensors", exception.getMessage());
        // assertEquals("Test message", exception.getCause().getMessage());
    }

    @Test
    public void addSensorForDevice_UnexpectedError() {
        Device device = mock(Device.class);
        Sensor sensor = mock(Sensor.class);
        ReflectionTestUtils.setField(device, "id", 1L);
        ReflectionTestUtils.setField(sensor, "id", 2L);
        when(deviceRepository.existsById(any())).thenReturn(true);
        when(sensorRepository.existsById(any())).thenReturn(true);
        when(device.getId()).thenReturn(1L);
        when(sensor.getId()).thenReturn(2L);
        doThrow(new RuntimeException("Test message")).when(deviceRepository).save(device);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> deviceService.addSensorForDevice(sensor, device));

        assertEquals("An error occurred while setting sensor: 2 for device: 1", exception.getMessage());
        assertEquals("Test message", exception.getCause().getMessage());
    }

    @Test
    public void addSensorForDevice_UnexpectedError2() {
        Device device = mock(Device.class);
        Sensor sensor = mock(Sensor.class);
        List<Sensor> sensors = new ArrayList<>();
        ReflectionTestUtils.setField(device, "id", 1L);
        ReflectionTestUtils.setField(sensor, "id", 2L);
        when(device.getId()).thenReturn(1L);
        when(sensor.getId()).thenReturn(2L);
        when(deviceRepository.existsById(any())).thenReturn(true);
        when(sensorRepository.existsById(any())).thenReturn(true);
        sensors.add(sensor);
        doThrow(new RuntimeException("Test message")).when(deviceRepository).save(any());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> deviceService.addSensorForDevice(sensors, device));

        assertEquals("An error occurred while adding the list of sensors to the device: 1", exception.getMessage());
        assertEquals("Test message", exception.getCause().getMessage());
    }

    @Test
    public void addSensorForDevice_UnexpectedError3() {
        Device device = mock(Device.class);
        Sensor sensor = mock(Sensor.class);
        List<Sensor> sensors = new ArrayList<>();
        ReflectionTestUtils.setField(device, "id", 1L);
        ReflectionTestUtils.setField(sensor, "id", 2L);
        when(device.getId()).thenReturn(1L);
        when(sensor.getId()).thenReturn(2L);
        when(deviceRepository.existsById(any())).thenReturn(true);
        when(sensorRepository.existsById(any())).thenReturn(true);
        sensors.add(sensor);
        doThrow(new RuntimeException("Test message")).when(sensorRepository).save(any());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> deviceService.addSensorForDevice(sensors, device));

        assertEquals("An error occurred while setting sensor: 2 for device: 1", exception.getMessage());
        assertEquals("Test message", exception.getCause().getMessage());
    }

    @Test
    public void getAllDevices_UnexpectedError() {
        doThrow(new RuntimeException("Test message")).when(deviceRepository).findAll();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> deviceService.getAllDevices());

        assertEquals("An unexpected error occurred while retrieving all devices.", exception.getMessage());
        assertEquals("Test message", exception.getCause().getMessage());
    }

    @Test
    public void calibrateDeviceAndSensors_unexpectedError() {
        when(deviceRepository.findById(any())).thenReturn(Optional.of(new Device()));
        doThrow(new RuntimeException("Test message")).when(sensorRepository).findByDevice(any());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> deviceService.calibrateDeviceAndSensors(1L));

        assertEquals("An error occurred while calibrating a sensor/device",  exception.getMessage());
        assertEquals("Test message", exception.getCause().getMessage());

    }
}
