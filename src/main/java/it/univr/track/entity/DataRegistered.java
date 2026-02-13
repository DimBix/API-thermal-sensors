package it.univr.track.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@Entity
@Table(name = "DATA_REGISTERED")
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Setter
@Getter
public class DataRegistered extends AbstractEntity {

    @OneToOne(mappedBy = "dataRegistered")
    private DeviceInfo deviceInfo;

    private int savingHour;
    private boolean dataTampered;
    private boolean dataValid;
    private Date savingDate;

    public DataRegistered(DeviceInfo deviceInfo, int savingHour, boolean dataTampered, boolean dataValid, Date savingDate) {
        this.deviceInfo = deviceInfo;
        this.savingHour = savingHour;
        this.dataTampered = dataTampered;
        this.dataValid = dataValid;
        this.savingDate = savingDate;
    }
}
