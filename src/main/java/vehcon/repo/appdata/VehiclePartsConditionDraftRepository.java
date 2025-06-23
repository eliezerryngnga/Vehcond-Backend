package vehcon.repo.appdata;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vehcon.models.vehiclecondemnations.VehicleDraft;
import vehcon.models.vehiclecondemnations.VehiclePartsConditionDraft;
import vehcon.models.vehiclecondemnations.VehiclePartsConditionDraftId;

public interface VehiclePartsConditionDraftRepository extends JpaRepository<VehiclePartsConditionDraft, VehiclePartsConditionDraftId>{

	List<VehiclePartsConditionDraft> findByApplicationCode(VehicleDraft applicationCode);
	
	List<VehiclePartsConditionDraft> deleteByApplicationCode(VehicleDraft applicationCode);
}
