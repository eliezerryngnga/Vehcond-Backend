package vehcon.repo.masters;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vehcon.models.masters.VehicleParts;

public interface VehiclePartsRepository extends JpaRepository<VehicleParts, Integer> {

	List<VehicleParts> findAllByOrderByVehiclePartCodeAsc();
}
