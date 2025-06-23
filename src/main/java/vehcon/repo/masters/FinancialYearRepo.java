package vehcon.repo.masters;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vehcon.models.masters.FinancialYear;

public interface FinancialYearRepo extends JpaRepository<FinancialYear, Integer>, JpaSpecificationExecutor<FinancialYear> {
	
}
