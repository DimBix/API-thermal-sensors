package demo;

import it.univr.track.entity.Device;
import it.univr.track.entity.Sensor;
import it.univr.track.repository.DeviceRepository;
import it.univr.track.repository.SensorRepository;
import it.univr.track.repository.SensorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SensorServiceMockTest {
    @Mock
    private DeviceRepository deviceRepository;
    @Mock
    private SensorRepository sensorRepository;
    @InjectMocks
    private SensorService sensorService;

    @Test
    public void deleteSensor_DatabaseError() {
        Device device = new Device();
        Sensor sensor = new Sensor(device, true, "test");
        ReflectionTestUtils.setField(sensor, "id", 1L);
        when(sensorRepository.existsById(1L)).thenReturn(true);
        doThrow(new DataRetrievalFailureException("Test message")).when(sensorRepository).delete(sensor);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> sensorService.deleteSensor(sensor));
        assertEquals("Could not delete sensor: 1", exception.getMessage());
    }

    @Test
    public void deleteSensor_SensorNotFound() {
        Device device = new Device();
        Sensor sensor = new Sensor(device, true, "test");
        ReflectionTestUtils.setField(sensor, "id", 999L);
        when(sensorRepository.existsById(999L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> sensorService.deleteSensor(sensor));
        assertEquals("Sensor with id: 999 not found", exception.getMessage());
    }

    @Test
    public void deleteSensor_UnexpectedError() {
        Device mockDevice = mock(Device.class);
        Sensor sensor = new Sensor(mockDevice, true, "test");
        ReflectionTestUtils.setField(sensor, "id", 999L);

        when(sensorRepository.existsById(999L)).thenReturn(true);
        when(mockDevice.getSensor()).thenThrow(new RuntimeException("Generic Error"));

        // In order to throw a RuntimeException, a non-repository method must be tested
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                sensorService.deleteSensor(sensor)
        );

        assertEquals("An unexpected error occurred while deleting sensor: 999", exception.getMessage());
    }

    @Test
    public void deleteAllSensors_UnexpectedError() {
        when(sensorRepository.count()).thenReturn(5L);
        when(deviceRepository.findAll()).thenThrow(new RuntimeException("Test message"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            sensorService.deleteAllSensors();
        });

        assertEquals("An error occurred while deleting all sensors", exception.getMessage());
        assertEquals("Test message", exception.getCause().getMessage());
    }

    @Test
    public void deleteAllSensors_DatabaseError() {
        when(sensorRepository.count()).thenReturn(2L);
        doThrow(new DataRetrievalFailureException("Test message")).when(sensorRepository).deleteAll();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> sensorService.deleteAllSensors());

        assertEquals("Could not delete all sensors", exception.getMessage());
    }

    @Test
    public void getDeviceBySensor_DatabaseError() {
        Device device = new Device();
        Sensor sensor = new Sensor(device, true, "test");
        ReflectionTestUtils.setField(sensor, "id", 1L);
        doThrow(new DataRetrievalFailureException("Test message")).when(sensorRepository).findById(1L);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> sensorService.getDeviceBySensor(sensor));

        assertEquals("Failed to retrieve the device for sensor ID: 1", exception.getMessage());
    }

    @Test
    public void registerSensor_DatabaseError() {
        Device device = new Device();
        Sensor sensor = new Sensor(device, true, "test");
        doThrow(new DataRetrievalFailureException("Test message")).when(sensorRepository).save(sensor);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> sensorService.registerSensor(sensor));

        assertEquals("Could not save sensor", exception.getMessage());
        assertEquals("Test message", exception.getCause().getMessage());
    }

    @Test
    public void registerSensor_UnexpectedError() {
        Device device = new Device();
        Sensor sensor = new Sensor(device, true, "test");
        when(sensorRepository.save(any(Sensor.class))).thenThrow(new RuntimeException("Test message"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> sensorService.registerSensor(sensor));

        assertEquals("An error occurred while saving sensor", exception.getMessage());
        assertEquals("Test message", exception.getCause().getMessage());
    }

    @Test
    public void updateSensor_DatabaseError() {
        Device device = new Device();
        Sensor sensor = new Sensor(device, true, "test");
        when(sensorRepository.existsById(any())).thenReturn(true);
        doThrow(new DataRetrievalFailureException("Test message")).when(sensorRepository).save(sensor);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> sensorService.updateSensor(sensor));

        assertEquals("Could not update sensor", exception.getMessage());
        assertEquals("Test message", exception.getCause().getMessage());
    }

    @Test
    public void updateSensor_UnexpectedError() {
        Device device = new Device();
        Sensor sensor = new Sensor(device, true, "test");
        ReflectionTestUtils.setField(sensor, "id", 1L);
        when(sensorRepository.existsById(1L)).thenReturn(true);
        when(sensorRepository.save(any(Sensor.class))).thenThrow(new RuntimeException("Test message"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> sensorService.updateSensor(sensor));

        assertEquals("An error occurred while updating sensor with id: 1", exception.getMessage());
        assertEquals("Test message", exception.getCause().getMessage());
    }

    @Test
    public void getAllSensorsByDevice_DatabaseError() {
        Device device = new Device();
        ReflectionTestUtils.setField(device, "id", 1L);
        when(deviceRepository.existsById(any())).thenReturn(true);
        when(sensorRepository.findByDevice(any())).thenThrow(new DataRetrievalFailureException("Test message"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> sensorService.getAllSensorsByDevice(device));

        assertEquals("Could not retrieve the sensors for device:1", exception.getMessage());
        assertEquals("Test message", exception.getCause().getMessage());
    }

    @Test
    public void getAllSensorsByDevice_UnexpectedError() {
        Device device = new Device();
        ReflectionTestUtils.setField(device, "id", 1L);
        when(deviceRepository.existsById(any())).thenReturn(true);
        when(sensorRepository.findByDevice(any())).thenThrow(new RuntimeException("Test message"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> sensorService.getAllSensorsByDevice(device));

        assertEquals("An error occurred while retrieving the sensors for device:1", exception.getMessage());
        assertEquals("Test message", exception.getCause().getMessage());
    }

    @Test
    public void getAllSensors_DatabaseError() {
        when(sensorRepository.findAll()).thenThrow(new DataRetrievalFailureException("Test message"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> sensorService.getAllSensors());

        assertEquals("Could not retrieve all sensors", exception.getMessage());
        assertEquals("Test message", exception.getCause().getMessage());
    }

    @Test
    public void getAllSensors_UnexpectedError() {
        when(sensorRepository.findAll()).thenThrow(new RuntimeException("Test message"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> sensorService.getAllSensors());

        assertEquals("An error occurred while retrieving all sensors", exception.getMessage());
        assertEquals("Test message", exception.getCause().getMessage());
    }

}
