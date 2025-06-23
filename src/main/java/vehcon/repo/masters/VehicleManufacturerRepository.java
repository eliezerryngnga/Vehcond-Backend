package vehcon.repo.masters;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vehcon.models.masters.VehicleManufacturer;

public interface VehicleManufacturerRepository extends JpaRepository<VehicleManufacturer, Integer>, JpaSpecificationExecutor<VehicleManufacturer> {

}
