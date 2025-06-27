package vehcon.repo.appdata;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vehcon.dto.appdata.YearMonthDTO;
import vehcon.models.appdata.Verification;

public interface VerificationRepository extends JpaRepository<Verification, String > {

	Optional<Verification> findByApplicationCode(String applicationCode);
	
	// CORRECTED QUERY
		@Query("SELECT new vehcon.dto.appdata.YearMonthDTO(EXTRACT(YEAR FROM ver.entryDate), EXTRACT(MONTH FROM ver.entryDate)) " +
			       "FROM Verification ver " +
			       "WHERE ver.vehicleFinal.processcode.processcode = :processCode " +
			       "GROUP BY EXTRACT(YEAR FROM ver.entryDate), EXTRACT(MONTH FROM ver.entryDate) " +
			       "ORDER BY EXTRACT(YEAR FROM ver.entryDate) DESC, EXTRACT(MONTH FROM ver.entryDate) DESC")
		List<YearMonthDTO> findDistinctYearAndMonthByProcessCode(@Param("processCode") Integer processCode);
		
}
