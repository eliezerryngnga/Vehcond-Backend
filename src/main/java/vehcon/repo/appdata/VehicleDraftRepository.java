package vehcon.repo.appdata;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vehcon.models.vehiclecondemnations.VehicleDraft;

public interface VehicleDraftRepository extends JpaRepository<VehicleDraft, String>{
	
	@Query("SELECT vd FROM VehicleDraft vd " +  
	"WHERE vd.processcode.processcode = :processCodeNumber " + 
	"And vd.departmentCode.departmentCode = :departmentCode " +
	"ORDER BY vd.entrydate DESC")
	
    List<VehicleDraft> findDraftByProcessCodeAndDepartmentCode(
    		@Param("processCodeNumber") int processCodeNumber,
    		@Param("departmentCode") int departmentCode);
}
