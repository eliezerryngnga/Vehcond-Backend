package vehcon.services.masters;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vehcon.annotations.Auditable;
import vehcon.common.data.util.SpecificationUtils;
import vehcon.dto.masters.FinancialYearDTO;
import vehcon.models.masters.FinancialYear;
import vehcon.repo.masters.FinancialYearRepo;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinancialYearService {

	private final FinancialYearRepo financialYearRepo;

	@Transactional
	public List<FinancialYear> getFinancialYear()
	{
		return financialYearRepo.findAll();
	}
	
	@Auditable
	@Transactional
	public FinancialYear addFinancialYear(FinancialYearDTO dto) {
        FinancialYear financialYear = new FinancialYear();
        
        financialYear.setFinancialYearFrom(dto.getFinancialyearfrom());
        
        financialYear.setFinancialYearTo(dto.getFinancialyearfrom() + 1);
        return financialYearRepo.save(financialYear);
    }
	
	@Transactional(readOnly = true)
	public Page<FinancialYear> getAllFinancialYear(
			String globalSearchTerm,
			Pageable pageable
			)
	{
		Specification<FinancialYear> spec = Specification.where(null);

        if (globalSearchTerm != null && !globalSearchTerm.trim().isEmpty()) {
            List<String> globalSearchFields = List.of("financialyearfrom");
            Specification<FinancialYear> globalSearchSpec = SpecificationUtils.searchInFields(globalSearchTerm, globalSearchFields);
            if (globalSearchSpec != null) 
            {
                spec = spec.and(globalSearchSpec);
            }
        }
        return financialYearRepo.findAll(spec, pageable);
    }
}