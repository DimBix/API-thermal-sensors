package it.univr.track.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.univr.track.entity.enumeration.Origin;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "DEVICE")
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter
@Setter
public class Device extends AbstractEntity {

    //    @ManyToOne
    //    @JoinColumn(name = "shipment_id")
    //    private Shipment shipment;
    @OneToMany(mappedBy = "device")
    @JsonIgnoreProperties("device") // Fix per dipendenza circolare
    private List<DeviceInfo> deviceInfos;

    @ManyToOne
    private UserRegistered user;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnoreProperties("device") // Fix per dipendenza circolare
    private List<Sensor> sensor = new ArrayList<>();
    private boolean isCalibrated;
    private boolean state;
    @Enumerated(EnumType.STRING)
    private Origin origin;
    private boolean isConnected;
    private float price;

    public Device(Shipment shipment, UserRegistered user, boolean isCalibrated, boolean state, Origin origin, boolean isConnected, float price) {
        //this.shipment = shipment;
        this.isCalibrated = isCalibrated;
        this.user = user;
        this.state = state;
        this.origin = origin;
        this.isConnected = isConnected;
        this.price = price;
    }

    public Device(Shipment shipment, UserRegistered user, Sensor sensor, boolean isCalibrated, boolean state, Origin origin, boolean isConnected, float price) {
        //this.shipment = shipment;
        this.isCalibrated = isCalibrated;
        this.user = user;
        this.state = state;
        this.origin = origin;
        this.isConnected = isConnected;
        this.price = price;
        this.sensor.add(sensor);
    }

    public Device(Shipment shipment, UserRegistered user, List<Sensor> sensor, boolean isCalibrated, boolean state, Origin origin, boolean isConnected, float price) {
        //this.shipment = shipment;
        this.isCalibrated = isCalibrated;
        this.user = user;
        this.state = state;
        this.origin = origin;
        this.isConnected = isConnected;
        this.price = price;
        this.sensor = sensor;
    }

    public boolean getIsCalibrated() {
        return isCalibrated;
    }

    public boolean getState() {
        return state;
    }

    public boolean getIsConnected() {
        return isConnected;
    }

    public void addSensor(Sensor s) {
        this.sensor.add(s);
        s.setDevice(this);
    }

    public void removeSensor(Sensor s) {
        this.sensor.remove(s);
        s.setDevice(null);
    }
}

