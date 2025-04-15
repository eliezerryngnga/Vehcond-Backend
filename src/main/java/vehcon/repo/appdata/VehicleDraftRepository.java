package vehcon.repo.appdata;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import vehcon.models.vehiclecondemnations.VehicleDraft;

public interface VehicleDraftRepository extends JpaRepository<VehicleDraft, String>{
	
	@Query("SELECT vd FROM VehicleDraft vd WHERE vd.processcode.pCode = 1 ORDER BY vd.entrydate DESC") // Assuming processcode 1 is draft
    List<VehicleDraft> findAllDrafts();

    // --- Keep methods needed for fetching details and saving ---
    @Query("SELECT vd FROM VehicleDraft vd LEFT JOIN FETCH vd.vehiclePartsConditionDrafts WHERE vd.applicationCode = :applicationCode")
    Optional<VehicleDraft> findByApplicationCodeWithParts(String applicationCode);

    Optional<VehicleDraft> findByApplicationCode(String applicationCode);

}
