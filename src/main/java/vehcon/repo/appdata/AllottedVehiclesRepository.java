package vehcon.repo.appdata;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vehcon.dto.appdata.YearMonthDTO;
import vehcon.models.appdata.AllottedVehicle;

public interface AllottedVehiclesRepository extends JpaRepository<AllottedVehicle, String> {
	
    @Query("SELECT av FROM AllottedVehicle av " +
            "JOIN FETCH av.vehicleFinal vf " +
            "JOIN FETCH vf.departmentCode " +
            "WHERE av.applicationCode = :applicationCode")
     Optional<AllottedVehicle> findDetailsByApplicationCode(@Param("applicationCode") String applicationCode);
    
 // CORRECTED QUERY
 		@Query("SELECT new vehcon.dto.appdata.YearMonthDTO(EXTRACT(YEAR FROM av.allottedDate), EXTRACT(MONTH FROM av.allottedDate)) " +
 			       "FROM AllottedVehicle av " +
 			       "WHERE av.vehicleFinal.processcode.processcode = :processCode " +
 			       "GROUP BY EXTRACT(YEAR FROM av.allottedDate), EXTRACT(MONTH FROM av.allottedDate) " +
 			       "ORDER BY EXTRACT(YEAR FROM av.allottedDate) DESC, EXTRACT(MONTH FROM av.allottedDate) DESC")
 		List<YearMonthDTO> findAllottedDistinctYearAndMonthByProcessCode(@Param("processCode") Integer processCode);
 		
// 		@Query(value = "SELECT " +
//                "    EXTRACT(YEAR FROM av.allotteddate) AS year, " +
//                "    EXTRACT(MONTH FROM av.allotteddate) AS month " +
//                "FROM " +
//                "    vehiclecondemnations.allottedvehicles av " +
//                "JOIN " +
//                "    vehiclecondemnations.vehicles vf ON av.applicationcode = vf.applicationcode " +
//                "JOIN " +
//                "    master.processes pc ON vf.processcode = pc.processcode " +
//                "WHERE " +
//                "    pc.processcode = :processCode " +
//                "GROUP BY " +
//                "    year, month " +
//                "ORDER BY " +
//                "    year DESC, month DESC",
//        nativeQuery = true) // <-- This flag is the key difference
// List<Object[]> findDistinctYearAndMonthByProcessCodeNative(@Param("processCode") Integer processCode);
}
