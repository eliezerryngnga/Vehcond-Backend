package vehcon.repo.appdata;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vehcon.models.appdata.VehicleFinal;
import vehcon.models.appdata.VehiclePartsConditionFinal;
import vehcon.models.appdata.VehiclePartsConditionFinalId;

public interface VehiclePartsConditionFinalRepository extends JpaRepository<VehiclePartsConditionFinal, VehiclePartsConditionFinalId> {

//	List<VehiclePartsConditionFinal> findByApplicationCodeApplicationCode(String applicationCode);
	
	List<VehiclePartsConditionFinal> findByApplicationCode(VehicleFinal applicationCode);

	@Query("SELECT vpc FROM VehiclePartsConditionFinal vpc WHERE vpc.applicationCode = :vehicle")
    List<VehiclePartsConditionFinal> findAllByVehicle(@Param("vehicle") VehicleFinal vehicle);
	
}
