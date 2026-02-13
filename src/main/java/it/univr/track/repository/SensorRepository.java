package it.univr.track.repository;

import it.univr.track.entity.Device;
import it.univr.track.entity.Sensor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SensorRepository extends CrudRepository<Sensor, Long> {

    List<Sensor> findByDevice(Device device);

}
