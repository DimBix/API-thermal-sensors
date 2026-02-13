package it.univr.track.repository;

import it.univr.track.entity.Device;
import it.univr.track.entity.UserRegistered;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface DeviceRepository extends CrudRepository<Device, Long> {
    List<Device> findByUser(UserRegistered user);
}
