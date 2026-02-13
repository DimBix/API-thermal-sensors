package it.univr.track.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@NoArgsConstructor
@Setter
@Getter
public class Shipment extends AbstractEntity {
    @OneToMany
    private List<Device> devices;
}
