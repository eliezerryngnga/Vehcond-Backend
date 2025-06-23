package vehcon.repo.masters;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vehcon.models.masters.VehicleParts;

public interface VehiclePartsRepository extends JpaRepository<VehicleParts, Integer>, JpaSpecificationExecutor<VehicleParts> {

	List<VehicleParts> findAllByOrderByVehiclePartCodeAsc();
}
