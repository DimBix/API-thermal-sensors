package it.univr.track.controller.api;

import it.univr.track.entity.Device;
import it.univr.track.entity.UserRegistered;
import it.univr.track.repository.DeviceService;
import it.univr.track.repository.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class DeviceController {

    @Autowired
    private DeviceService deviceService;
    @Autowired
    private UserService userService;

    // add new device
    @PostMapping("/api/device")
    public boolean addDevice(@RequestBody Device device) {
        deviceService.registerDevice(device);
        return true;
    }

    // read the device configuration
    @GetMapping("/api/device/{deviceId}")
    public ResponseEntity<Device> readDeviceConfig(@PathVariable("deviceId") Long id) {
        Optional<Device> device = deviceService.getDeviceById(id);
        if (device.isPresent()) {
            return ResponseEntity.ok(device.get());
        }
        return ResponseEntity.notFound().build();
    }

    // update device configuration
    @PutMapping("/api/device")
    public boolean editDevice(@RequestBody Device device) {
        deviceService.updateDevice(device);
        return true;
    }

    // decommission a device
    @DeleteMapping("/api/device")
    public boolean deleteDevice(@RequestParam Device device) {
        deviceService.deleteDevice(device);
        return true;
    }

    // list all the devices that are visible for this user
    @GetMapping("/api/devices")
    public ResponseEntity<List<Device>> devices(@RequestParam(name = "username") String username) {
        Optional<UserRegistered> user = userService.getUserByUsername(username);
        return user.isEmpty() ? ResponseEntity.status(HttpStatus.UNAUTHORIZED).build() : ResponseEntity.ok(deviceService.getDevicesByUser(user.get()));
    }

    @GetMapping("/api/test")
    public String test() {
        return "test";
    }
}