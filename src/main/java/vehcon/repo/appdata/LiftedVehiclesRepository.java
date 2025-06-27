package vehcon.repo.appdata;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vehcon.dto.appdata.YearMonthDTO;
import vehcon.models.appdata.LiftedVehicles;

public interface LiftedVehiclesRepository extends JpaRepository<LiftedVehicles, String> {
	@Query("SELECT new vehcon.dto.appdata.YearMonthDTO(EXTRACT(YEAR FROM lv.lifteddate), EXTRACT(MONTH FROM lv.lifteddate)) " +
		       "FROM LiftedVehicles lv " +
		       "WHERE lv.vehicleFinal.processcode.processcode = :processCode " +
		       "GROUP BY EXTRACT(YEAR FROM lv.lifteddate), EXTRACT(MONTH FROM lv.lifteddate) " +
		       "ORDER BY EXTRACT(YEAR FROM lv.lifteddate) DESC, EXTRACT(MONTH FROM lv.lifteddate) DESC")
	List<YearMonthDTO> findLiftedDistinctYearAndMonthByProcessCode(@Param("processCode") Integer processCode);
}
