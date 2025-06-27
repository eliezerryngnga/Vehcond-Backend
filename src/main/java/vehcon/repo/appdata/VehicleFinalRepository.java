package vehcon.repo.appdata;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vehcon.models.appdata.VehicleFinal;

public interface VehicleFinalRepository extends JpaRepository<VehicleFinal, String>, JpaSpecificationExecutor<VehicleFinal> {

	List<VehicleFinal> findByProcesscodeProcesscode(Integer processcode);
	
	Optional<VehicleFinal> findByApplicationCodeAndProcesscodeProcesscode(String applicationCode, Integer processCodeValue );

	List<VehicleFinal> findByEntrydate(LocalDate entryDate);
	
	List<VehicleFinal> findByFinancialYearCode_Financialyearcode(Integer financialyearcode);
	
//	 @Query("SELECT v FROM VehicleFinal v WHERE YEAR(v.entrydate) = :year AND MONTH(v.entrydate) = :month")
//	    List<VehicleFinal> findByEntryYearAndMonth(@Param("year") int year, @Param("month") int month);
	 
//	 @Query("SELECT new vehcon.dto.appdata.YearMonthDTO(YEAR(v.entrydate), MONTH(v.entrydate)) " +
//	           "FROM VehicleFinal v " +
//	           "WHERE v.processcode.processcode = :processCode " + 
//	           "GROUP BY YEAR(v.entrydate), MONTH(v.entrydate) " +
//	           "ORDER BY YEAR(v.entrydate) DESC, MONTH(v.entrydate) DESC")
//	    List<YearMonthDTO> findDistinctYearAndMonthOfVehicles(@Param("processCode") Integer processCode);
	 
//	 @Query(
//		        value = "SELECT YEAR(STR_TO_DATE(SUBSTRING_INDEX(v.letternodate, '|', -1), '%Y-%m-%d')) AS year, " +
//		                "       MONTH(STR_TO_DATE(SUBSTRING_INDEX(v.letternodate, '|', -1), '%Y-%m-%d')) AS month " +
//		                "FROM vehicle_final v " + // <-- Use your actual TABLE name here
//		                "WHERE v.process_code = :processCode " + // <-- Use your actual COLUMN name here
//		                "  AND v.letternodate IS NOT NULL AND v.letternodate LIKE '%|%' " + // <-- Safe check
//		                "GROUP BY year, month " +
//		                "ORDER BY year DESC, month DESC",
//		                nativeQuery = true 
//		    )
//		    List<YearMonthDTO> findDistinctLetterYearAndMonthFromCombinedField(@Param("processCode") Integer processCode);
}
