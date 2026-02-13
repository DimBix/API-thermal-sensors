package it.univr.track.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ShipmentWebController {

    //create new shipment
    @RequestMapping("/shipment/newShipment")
    public String newShipment() {
        return "newShipment";
    }

    //list shipments
    @RequestMapping("/shipment/shipments")
    public String shipments() {
        return "shipments";
    }

    //activate/deactivate tracking
    @RequestMapping("/shipment/tracking")
    public String tracking() {
        return "tracking";
    }

    //allocate a device to a shipment
    @RequestMapping("/shipment/shipmentAllocate")
    public String shipmentAllocate() {
        return "shipmentAllocate";
    }

}
