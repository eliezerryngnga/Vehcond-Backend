package vehcon.services.masters;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import vehcon.annotations.Auditable;
import vehcon.common.data.util.SpecificationUtils;
import vehcon.dto.masters.DistrictRtoDTO;
import vehcon.models.masters.DistrictRto;
import vehcon.models.masters.Districts;
import vehcon.repo.masters.DistrictRtoRepository;
import vehcon.repo.masters.DistrictsRepository;

@Service
@RequiredArgsConstructor
public class DistrictRtoService {
	
	private final DistrictRtoRepository districtRtoRepo;
	private final DistrictsRepository districtsRepo;
	
	@Transactional
	public List<DistrictRto> getDistrictRto()
	{			
		return districtRtoRepo.findAll();
	}
	
	@Auditable
	@Transactional
	public DistrictRto addDistrictRto(DistrictRtoDTO dto) { 
        DistrictRto districtRto = new DistrictRto();

        
        Districts parentDistrict = districtsRepo.findById(dto.getDistrictCode()) 
            .orElseThrow(() -> new EntityNotFoundException("District not found with code: " + dto.getDistrictCode()));

        districtRto.setDistrict(parentDistrict);

      
        districtRto.setRtoCode(dto.getRtoCode());


        // 5. Save the DistrictRto entity
        return districtRtoRepo.save(districtRto);
    }

	@Transactional(readOnly = true)
	public Page<DistrictRto> getAllDistrictRto(
			String globalSearchTerm,
			Pageable pageable
			)
	{
		Specification<DistrictRto> spec = Specification.where(null);

        if (globalSearchTerm != null && !globalSearchTerm.trim().isEmpty()) {
            List<String> globalSearchFields = List.of("rtoCode","district.districtName");
            Specification<DistrictRto> globalSearchSpec = SpecificationUtils.searchInFields(globalSearchTerm, globalSearchFields);
            if (globalSearchSpec != null) 
            {
                spec = spec.and(globalSearchSpec);
            }
        }
        return districtRtoRepo.findAll(spec, pageable);
    }
	
}
