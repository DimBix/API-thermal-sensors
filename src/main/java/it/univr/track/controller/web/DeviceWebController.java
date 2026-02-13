package it.univr.track.controller.web;

import it.univr.track.entity.Device;
import it.univr.track.entity.Sensor;
import it.univr.track.repository.DeviceService;
import it.univr.track.repository.SensorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class DeviceWebController {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private SensorService sensorService;

    //provisioning of a new device (QR-code?)
    @RequestMapping("/device/provision")
    public String provision() {
        return "provision";
    }

    //decommissioning of an old device
    @RequestMapping("/device/decommission/{deviceId}")
    public String decommission(@PathVariable("deviceId") Long deviceId) {
        deviceService.deleteDeviceById(deviceId);
        return "redirect:/employeePage";
    }

    //list devices
    @RequestMapping("/device/devices")
    public String devices() {
        return "devices";
    }

    //view device configuration
    @RequestMapping("/device/configDevice/{deviceId}")
    public String configDevice(Model model, @PathVariable("deviceId") Long deviceId) {
        Device device = deviceService.getDeviceById(deviceId).get();
        List<Sensor> sensors = deviceService.getSensorsByDevice(device);


        model.addAttribute("device", device);
        model.addAttribute("sensors", sensors);
        return "configDevice";
    }

    //edit device configuration
    @RequestMapping("/device/editConfigDevice/{deviceId}")
    public String editConfigDevice(Model model, @PathVariable("deviceId") Long deviceId, HttpSession session) {
        Device device = deviceService.getDeviceById(deviceId).get();
        List<Sensor> sensors = deviceService.getSensorsByDevice(device);

        List<Long> pendingRemovals =
                (List<Long>) session.getAttribute("pendingRemovals");

        if (pendingRemovals == null) {
            pendingRemovals = new ArrayList<>();
            session.setAttribute("pendingRemovals", pendingRemovals);
        }
        model.addAttribute("device", device);
        model.addAttribute("sensors", sensors);
        model.addAttribute("pendingRemovals", pendingRemovals);
        return "editConfigDevice";
    }

    @PostMapping("/device/removeSensor")
    public String removeSensor(
            @RequestParam Long deviceId,
            @RequestParam Long sensorId,
            HttpSession session) {

        List<Long> pendingRemovals =
                (List<Long>) session.getAttribute("pendingRemovals");

        if (pendingRemovals == null) {
            pendingRemovals = new ArrayList<>();
            session.setAttribute("pendingRemovals", pendingRemovals);
        }

        pendingRemovals.add(sensorId);
        return "redirect:/device/editConfigDevice/" + deviceId;
    }


    //send configuration to device
    @PostMapping("/device/sendConfigDevice")
    public String sendConfigDevice(@RequestParam Long deviceId, HttpSession session) {


        List<Long> pendingRemovals = (List<Long>) session.getAttribute("pendingRemovals");

        if (pendingRemovals != null) {
            deviceService.removeSensorsFromDevice(deviceId, pendingRemovals);
            session.removeAttribute("pendingRemovals");
        }

        return "redirect:/device/editConfigDevice/" + deviceId;
    }

    @RequestMapping("/device/calibrate/{deviceId}")
    public String calibrateDevice(@PathVariable("deviceId") Long deviceId) {
        // Simply delegate the logic to the service
        deviceService.calibrateDeviceAndSensors(deviceId);
        return "redirect:/employeePage";
    }
}
