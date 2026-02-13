package it.univr.track.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "SENSOR")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
@Getter
@Setter
public class Sensor extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device device;

    private Boolean isCalibrated;
    private String parametersTypes;

    public Sensor(boolean isCalibrated, String parametersTypes) {
        this.device = null;
        this.isCalibrated = isCalibrated;
        this.parametersTypes = parametersTypes;
    }

    public Sensor(Device device, boolean isCalibrated, String parametersTypes) {
        this.device = device;
        this.isCalibrated = isCalibrated;
        this.parametersTypes = parametersTypes;
    }

}
