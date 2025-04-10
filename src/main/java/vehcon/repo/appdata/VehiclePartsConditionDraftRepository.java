package vehcon.repo.appdata;

import org.springframework.data.jpa.repository.JpaRepository;

import vehcon.models.vehiclecondemnations.VehiclePartsConditionDraft;
import vehcon.models.vehiclecondemnations.VehiclePartsConditionDraftId;

public interface VehiclePartsConditionDraftRepository extends JpaRepository<VehiclePartsConditionDraft, VehiclePartsConditionDraftId>{

}
