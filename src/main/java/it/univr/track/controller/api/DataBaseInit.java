package it.univr.track.controller.api;

import it.univr.track.entity.Device;
import it.univr.track.entity.Sensor;
import it.univr.track.entity.Shipment;
import it.univr.track.entity.UserRegistered;
import it.univr.track.entity.enumeration.Gender;
import it.univr.track.entity.enumeration.Origin;
import it.univr.track.repository.DeviceService;
import it.univr.track.repository.SensorService;
import it.univr.track.repository.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static it.univr.track.entity.enumeration.Role.ADMIN;
import static it.univr.track.entity.enumeration.Role.USER;

@Component
public class DataBaseInit implements CommandLineRunner {

    private final UserService userService;

    private final SensorService sensorService;

    private final DeviceService deviceService;

    public DataBaseInit(UserService userService, SensorService sensorService, DeviceService deviceService) {
        this.userService = userService;
        this.sensorService = sensorService;
        this.deviceService = deviceService;
    }

    @Override
    public void run(String... args) {
        UserRegistered user1 = new UserRegistered("John", "Doe", "JhonD", "password", "jhon@gmail.com", USER, Gender.MALE, "Verona", "123 Main", "123-321-123", "902");
        UserRegistered user2 = new UserRegistered("Ada", "Lovelace", "AdaL", "password", "thecreator@gmail.com", ADMIN, Gender.FEMALE, "Padova", "221B Backer street", "123-456-123", "100");

        Device device1 = new Device(new Shipment(), user1, false, true, Origin.BOUGHT, true, 30);

        Sensor sensor1 = new Sensor(false, "non");
        Sensor sensor2 = new Sensor(false, "non");

        Sensor sensor3 = new Sensor(false, "non");
        Sensor sensor4 = new Sensor(false, "non");

        userService.registerUser(user1);
        userService.registerUser(user2);

        deviceService.registerDevice(device1);

        sensorService.registerSensor(sensor1);
        sensorService.registerSensor(sensor2);

        sensorService.registerSensor(sensor3);
        sensorService.registerSensor(sensor4);

        deviceService.addSensorForDevice(sensor1, device1);
        deviceService.addSensorForDevice(sensor2, device1);

        List<Sensor> list = new ArrayList<>();
        list.add(sensor3);
        list.add(sensor4);
        deviceService.addSensorForDevice(list, device1);
        Device deviceR = sensorService.getDeviceBySensor(sensor1);
    }
}