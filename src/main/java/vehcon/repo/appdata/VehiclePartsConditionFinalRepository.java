package vehcon.repo.appdata;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vehcon.models.appdata.VehicleFinal;
import vehcon.models.appdata.VehiclePartsConditionFinal;
import vehcon.models.appdata.VehiclePartsConditionFinalId;

public interface VehiclePartsConditionFinalRepository extends JpaRepository<VehiclePartsConditionFinal, VehiclePartsConditionFinalId> {

//	List<VehiclePartsConditionFinal> findByApplicationCodeApplicationCode(String applicationCode);
	
	List<VehiclePartsConditionFinal> findByApplicationCode(VehicleFinal applicationCode);

}
