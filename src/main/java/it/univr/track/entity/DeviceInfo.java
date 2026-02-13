package it.univr.track.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "DEVICE_INFO")
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Setter
@Getter
public class DeviceInfo extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device device;

    @OneToOne
    @JoinColumn(name = "data_registered_id")
    private DataRegistered dataRegistered;

    private String dataChecked;
    private boolean accident;

    public DeviceInfo(Device device, DataRegistered dataRegistered, String dataChecked, boolean accident) {
        this.device = device;
        this.dataRegistered = dataRegistered;
        this.dataChecked = dataChecked;
        this.accident = accident;
    }
}
