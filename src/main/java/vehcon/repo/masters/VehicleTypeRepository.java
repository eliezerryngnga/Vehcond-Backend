package vehcon.repo.masters;

import org.springframework.data.jpa.repository.JpaRepository;

import vehcon.models.masters.VehicleType;

public interface VehicleTypeRepository extends JpaRepository<VehicleType, Integer> {

}
