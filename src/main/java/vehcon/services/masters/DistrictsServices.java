package vehcon.services.masters;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vehcon.annotations.Auditable;
import vehcon.common.data.util.SpecificationUtils;
import vehcon.dto.masters.DistrictsDTO;
import vehcon.models.masters.Districts;
import vehcon.repo.masters.DistrictsRepository;

@Service
@RequiredArgsConstructor
public class DistrictsServices {
	
	private final DistrictsRepository districtsRepo;
	
	@Auditable
	@Transactional
	public Districts addDistricts(DistrictsDTO dto) {
        Districts district = new Districts();
        district.setDistrictName(dto.getDistrictName());
        district.setLgdCode(dto.getLgdCode());
        return districtsRepo.save(district);
    }
	
	@Transactional
	public List<Districts> getByDistricts()
	{
		return districtsRepo.findAll();
	}
	
	@Transactional
	public Page<Districts> getAllDistricts(
			String globalSearchTerm,
			Pageable pageable
			)
	{
		Specification<Districts> spec = Specification.where(null);

        if (globalSearchTerm != null && !globalSearchTerm.trim().isEmpty()) {
            List<String> globalSearchFields = List.of("districtName","districtCode","lgdCode");
            Specification<Districts> globalSearchSpec = SpecificationUtils.searchInFields(globalSearchTerm, globalSearchFields);
            if (globalSearchSpec != null) 
            {
                spec = spec.and(globalSearchSpec);
            }
        }
        return districtsRepo.findAll(spec, pageable);
    }
}
