package vehcon.repo.appdata;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vehcon.dto.appdata.YearMonthProjection;
import vehcon.models.appdata.VcctcTemp;

public interface VcctcTempRepository extends JpaRepository<VcctcTemp, String> {

    Optional<VcctcTemp> findByApplicationCode(String applicationCode);

    @Query(value =
            "WITH ExtractedDates AS ( " +
            "  SELECT " +
            "    CAST(SPLIT_PART(vt.letternodate, '|', 2) AS DATE) AS extracted_date " +
            "  FROM " +
            "    vehiclecondemnations.vcctc_temp vt " +
            "    JOIN vehiclecondemnations.vehicles vf ON vt.applicationcode = vf.applicationcode " +
            "  WHERE " +
            "    vf.processcode = :processCode " +
            "    AND vt.letternodate LIKE '%|%' " +
            ") " +
            "SELECT " +
            "  CAST(DATE_PART('year', ed.extracted_date) AS INTEGER) AS year, " +
            "  CAST(DATE_PART('month', ed.extracted_date) AS INTEGER) AS month " +
            "FROM ExtractedDates ed " +
            "GROUP BY year, month " +
            "ORDER BY year DESC, month DESC",
            nativeQuery = true)
        List<YearMonthProjection> findVcctcTempYearAndMonthFromLetterNoDate(@Param("processCode") Integer processCode);
}