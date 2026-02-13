package it.univr.track.repository;

import it.univr.track.entity.Device;
import it.univr.track.entity.Sensor;
import it.univr.track.entity.UserRegistered;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final SensorRepository sensorRepository;
    private final SensorService sensorService;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository, SensorRepository sensorRepository, SensorService sensorService) {
        this.deviceRepository = deviceRepository;
        this.sensorRepository = sensorRepository;
        this.sensorService = sensorService;
    }

    private void deleteWithErrorHandling(Device device) {
        try {
            deviceRepository.delete(device);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while deleting device: " + device.getId(), e);
        }
    }


    public Optional<Device> getDeviceById(Long id) {
        if (id < 0) {
            throw new IllegalArgumentException("Device id is negative");
        }

        try {
            return deviceRepository.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while retrieving the device for sensor:" + id, e);
        }
    }

    public List<Device> getDevicesByUser(UserRegistered user) {
        if (user == null) {
            throw new IllegalArgumentException("User is null");
        }

        try {
            return deviceRepository.findByUser(user);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while retrieving the device for user: " + user.getUsername(), e);
        }
    }

    public void deleteDevice(Device device) {
        if (!deviceRepository.existsById(device.getId())) {
            throw new RuntimeException("Device with id: " + device.getId() + " not found");
        }

        deleteWithErrorHandling(device);
    }

    public void deleteDeviceById(Long id) {
        if (!deviceRepository.existsById(id)) {
            throw new NoSuchElementException("Device with id: " + id + " not found");
        }

        deleteWithErrorHandling(deviceRepository.findById(id).get());
    }

    public void deleteAllDevices() {
        if (!(deviceRepository.count() > 0)) {
            throw new RuntimeException("There are no devices");
        }

        try {
            deviceRepository.deleteAll();
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while deleting all devices", e);
        }
    }


    public Device registerDevice(Device device) {
        if (device.getId() != null && deviceRepository.existsById(device.getId())) {
            throw new IllegalArgumentException("Device already exists with ID: " + device.getId());
        }

        try {
            return deviceRepository.save(device);
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while saving the device.", e);
        }
    }

    public void updateDevice(Device device) {
        if (!deviceRepository.existsById(device.getId())) {
            throw new IllegalArgumentException("Device ID does not exist: " + device.getId());
        }

        try {
            deviceRepository.save(device);
        } catch (DataAccessException dae) {
            throw new RuntimeException("Failed to update device due to data access issue.", dae);
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while updating the device.", e);
        }
    }

    public List<Sensor> getSensorsByDevice(Device device) {
        if (device.getId() == null || !deviceRepository.existsById(device.getId())) {
            throw new RuntimeException("ID of device does not exist. Could not retrieve its sensors, id of device:" + device.getId());
        }

        try {
            return device.getSensor();
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong while retrieving the list of sensors");
        }
    }

    public void addSensorForDevice(Sensor sensor, Device device) {
        if (device.getId() == null || !deviceRepository.existsById(device.getId())) {
            throw new RuntimeException("ID of device does not exist. Could not add sensor with id: " + sensor.getId() + " to device: " + device.getId());
        }

        if (sensor.getId() == null || !sensorRepository.existsById(sensor.getId())) {
            throw new RuntimeException("ID of sensor does not exist. Could not add sensor with id: " + sensor.getId() + " to device: " + device.getId());
        }

        try {
            List<Sensor> sensors = device.getSensor();
            sensors.add(sensor);
            device.setSensor(sensors);
            deviceRepository.save(device);
            sensor.setDevice(device);
            sensorRepository.save(sensor);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while setting sensor: " + sensor.getId() + " for device: " + device.getId(), e);
        }
    }

    public void addSensorForDevice(List<Sensor> sensors, Device device) {
        if (device.getId() == null || !deviceRepository.existsById(device.getId())) {
            throw new RuntimeException("ID of device does not exist. Could not add list of sensors to device with id: " + device.getId());
        }

        for (Sensor sensor : sensors) {
            if (sensor.getId() == null || !sensorRepository.existsById(sensor.getId())) {
                throw new RuntimeException("ID of sensor does not exist. Could not add sensor with id: " + sensor.getId() + " to device: " + device.getId());
            }
        }

        try {
            List<Sensor> newSensors = device.getSensor();
            newSensors.addAll(sensors);
            device.setSensor(newSensors);
            deviceRepository.save(device);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while adding the list of sensors to the device: " + device.getId(), e);
        }

        for (Sensor sensor : sensors) {
            try {
                sensor.setDevice(device);
                sensorRepository.save(sensor);
            } catch (Exception e) {
                throw new RuntimeException("An error occurred while setting sensor: " + sensor.getId() + " for device: " + device.getId(), e);
            }
        }

    }

    public Iterable<Device> getAllDevices() {
        try {
            return deviceRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while retrieving all devices.", e);
        }
    }


    @Transactional
    public void removeSensorsFromDevice(Long deviceId, List<Long> sensorIdsToRemove) {
        try {
            List<Sensor> allSensors = getSensorsByDevice(getDeviceById(deviceId).get());
            List<Sensor> sensorsToDelete = (List<Sensor>) sensorRepository.findAllById(sensorIdsToRemove);
            allSensors.removeAll(sensorsToDelete);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while removing a sensor", e);
        }
    }

    @Transactional
    public void calibrateDeviceAndSensors(Long deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        try {
            device.setCalibrated(true);

            List<Sensor> sensors = sensorRepository.findByDevice(device);
            for (Sensor sensor : sensors) {
                sensor.setIsCalibrated(true);
            }
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while calibrating a sensor/device", e);
        }
    }


    public Long countDevices() {
        return deviceRepository.count();
    }
}
