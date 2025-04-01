package vehcon.repo.masters;

import org.springframework.data.jpa.repository.JpaRepository;

import vehcon.models.masters.FinancialYear;

public interface FinancialYearRepo extends JpaRepository<FinancialYear, Integer> {
	
}
