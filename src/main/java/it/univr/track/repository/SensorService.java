package it.univr.track.repository;

import it.univr.track.entity.Device;
import it.univr.track.entity.Sensor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SensorService {

    private final SensorRepository sensorRepository;
    private final DeviceRepository deviceRepository;

    @Autowired
    public SensorService(SensorRepository sensorRepository, DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
        this.sensorRepository = sensorRepository;
    }

    private void deleteWithErrorHandling(Sensor sensor) {
        try {
            // Attached sensor must first be detached, then removed
            if (sensor.getDevice() != null) {
                Device device = sensor.getDevice();
                device.getSensor().remove(sensor);
                deviceRepository.save(device);
            }
            sensorRepository.delete(sensor);
        } catch (DataAccessException e) {
            throw new RuntimeException("Could not delete sensor: " + sensor.getId(), e);
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while deleting sensor: " + sensor.getId(), e);
        }
    }

    public Optional<Sensor> getSensorById(Long id) {
        return sensorRepository.findById(id);
    }

    public Device getDeviceBySensor(Sensor sensor) {
        try {
            Device device = getSensorById(sensor.getId()).get().getDevice();
            if(device == null)
                throw new RuntimeException("No device found for sensor with ID: " + sensor.getId());
            return device;
        } catch (DataAccessException dae) {
            throw new RuntimeException("Failed to retrieve the device for sensor ID: " + sensor.getId(), dae);
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while retrieving the device for sensor ID: " + sensor.getId(), e);
        }
    }

    public void deleteSensor(Sensor sensor) {
        if (!sensorRepository.existsById(sensor.getId())) {
            throw new RuntimeException("Sensor with id: " + sensor.getId() + " not found");
        }

        deleteWithErrorHandling(sensor);
    }

    public void deleteSensorById(Long id) {
        if (!sensorRepository.existsById(id)) {
            throw new IllegalArgumentException("Sensor with id: " + id + " not found");
        }

        deleteWithErrorHandling(sensorRepository.findById(id).get());
    }

    public void deleteAllSensors() {
        if (!(sensorRepository.count() > 0)) {
            throw new IllegalStateException("There are no sensors");
        }

        try {
            // Same reason as deleteWithErrorHandling
            Iterable<Device> devices = deviceRepository.findAll();
            for (Device device : devices) {
                device.getSensor().clear();
                deviceRepository.save(device);
            }
            sensorRepository.deleteAll();
        } catch (DataAccessException e) {
            throw new RuntimeException("Could not delete all sensors", e);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while deleting all sensors", e);
        }
    }

    public Sensor registerSensor(Sensor sensor) {
        if (sensor.getId() != null && sensorRepository.existsById(sensor.getId())) {
            throw new IllegalArgumentException("Id of sensor already exists. Could not register sensor: " + sensor.getId());
        }

        try {
            return sensorRepository.save(sensor);
        } catch (DataAccessException e) {
            throw new RuntimeException("Could not save sensor", e);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while saving sensor", e);
        }
    }

    public void updateSensor(Sensor sensor) {
        if (!sensorRepository.existsById(sensor.getId())) {
            throw new IllegalArgumentException("Id of sensor does not exists. Could not update sensor: " + sensor.getId());
        }

        try {
            sensorRepository.save(sensor);
        } catch (DataAccessException e) {
            throw new RuntimeException("Could not update sensor", e);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while updating sensor with id: " + sensor.getId(), e);
        }

    }

    public List<Sensor> getAllSensorsByDevice(Device device) {
        if (!deviceRepository.existsById(device.getId())) {
            throw new IllegalArgumentException("This device with id: " + device.getId() + " has no sensors!");
        }

        try {
            return sensorRepository.findByDevice(device);
        } catch (DataAccessException e) {
            throw new RuntimeException("Could not retrieve the sensors for device:" + device.getId(), e);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while retrieving the sensors for device:" + device.getId(), e);
        }
    }

    public Iterable<Sensor> getAllSensors() {
        try {
            return sensorRepository.findAll();
        } catch (DataAccessException e) {
            throw new RuntimeException("Could not retrieve all sensors", e);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while retrieving all sensors", e);
        }

    }

    public Long countSensors() {
        return sensorRepository.count();
    }
}
