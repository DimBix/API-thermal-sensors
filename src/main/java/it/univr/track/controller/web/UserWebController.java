package it.univr.track.controller.web;

import it.univr.track.entity.Device;
import it.univr.track.entity.Sensor;
import it.univr.track.repository.DeviceService;
import it.univr.track.repository.SensorService;
import it.univr.track.repository.UserService;
import it.univr.track.security.CustomUserDetails;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class UserWebController {

    @Autowired
    private UserService userService;

    @Autowired
    private SensorService sensorService;

    @Autowired
    private DeviceService deviceService;

    @RequestMapping("/")
    public String landingPage(){
        return "redirect:/profile";
    }

    //create user account
    @RequestMapping("/signUp")
    public String signUp() {
        return "signUp";
    }

    //authentication (users)
    @RequestMapping("/signIn")
    public String signIn() { return "signIn"; }

    //edit user profile
    @RequestMapping("/profile")
    public String profile(Model model, @AuthenticationPrincipal CustomUserDetails principal) {
        model.addAttribute("user", principal.getUser());
        return "profile";
    }

    @GetMapping("/employeePage")
    public String employeePage(Model model, @AuthenticationPrincipal CustomUserDetails principal, HttpSession session) {
        List<Device> devices =  deviceService.getDevicesByUser(principal.getUser());
        model.addAttribute("devices",devices);

        List<Long> pendingRemovals =
                (List<Long>) session.getAttribute("pendingRemovals");

        if (pendingRemovals == null) {
            pendingRemovals = new ArrayList<>();
            session.setAttribute("pendingRemovals", pendingRemovals);
        }

        pendingRemovals.clear();

        return "employeePage";
    }

    @GetMapping("/managerPage")
    public String managerPage(Model model, @AuthenticationPrincipal CustomUserDetails principal) {

        Iterable<Device> devices = deviceService.getAllDevices();
        Iterable<Sensor> sensors = sensorService.getAllSensors();
        model.addAttribute("user",principal.getUser());
        model.addAttribute("devices",devices);
        model.addAttribute("sensors",sensors);
        return "managerPage";
    }


}
