package vehcon.repo.appdata;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vehcon.dto.appdata.YearMonthProjection;
import vehcon.models.appdata.TenderVehicles;

public interface TenderVehiclesRepository extends JpaRepository<TenderVehicles, String>{

	 @Query(value =
		        "WITH ExtractedDates AS ( " +
		        "  SELECT " +
		        // THE FIX: Use TO_DATE with an explicit format mask
		        "    TO_DATE(SPLIT_PART(tv.letternodate, '|', 2), 'DD-MM-YYYY') AS extracted_date " +
		        "  FROM " +
		        "    vehiclecondemnations.tendervehicles tv " +
		        "    JOIN vehiclecondemnations.vehicles vf ON tv.applicationcode = vf.applicationcode " +
		        "  WHERE " +
		        "    vf.processcode = :processCode " +
		        "    AND tv.letternodate LIKE '%|%' " +
		        "    AND SPLIT_PART(tv.letternodate, '|', 2) ~ '^[0-9]{2}-[0-9]{2}-[0-9]{4}$' " + // Optional regex filter
		        ") " +
		        "SELECT " +
		        "  CAST(DATE_PART('year', ed.extracted_date) AS INTEGER) AS year, " +
		        "  CAST(DATE_PART('month', ed.extracted_date) AS INTEGER) AS month " +
		        "FROM ExtractedDates ed " +
		        "WHERE ed.extracted_date IS NOT NULL " +
		        "GROUP BY year, month " +
		        "ORDER BY year DESC, month DESC",
		        nativeQuery = true)
		    List<YearMonthProjection> findTenderdYearAndMonthFromLetterNoDate(@Param("processCode") Integer processCode);
}
