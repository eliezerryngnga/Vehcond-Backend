package vehcon.repo.appdata;

import org.springframework.data.jpa.repository.JpaRepository;

import vehcon.models.appdata.VehiclePartsConditionFinal;
import vehcon.models.appdata.VehiclePartsConditionFinalId;

public interface VehiclePartsConditionFinalRepository extends JpaRepository<VehiclePartsConditionFinal, VehiclePartsConditionFinalId> {

}
