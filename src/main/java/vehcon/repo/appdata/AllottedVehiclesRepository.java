package vehcon.repo.appdata;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vehcon.models.appdata.AllottedVehicle;

public interface AllottedVehiclesRepository extends JpaRepository<AllottedVehicle, String> {
	
    @Query("SELECT av FROM AllottedVehicle av " +
            "JOIN FETCH av.vehicleFinal vf " +
            "JOIN FETCH vf.departmentCode " +
            "WHERE av.applicationCode = :applicationCode")
     Optional<AllottedVehicle> findDetailsByApplicationCode(@Param("applicationCode") String applicationCode);
}
