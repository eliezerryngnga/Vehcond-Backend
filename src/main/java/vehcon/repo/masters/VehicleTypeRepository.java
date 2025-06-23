package vehcon.repo.masters;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vehcon.models.masters.VehicleType;

public interface VehicleTypeRepository extends JpaRepository<VehicleType, Integer>, JpaSpecificationExecutor<VehicleType> {

}
