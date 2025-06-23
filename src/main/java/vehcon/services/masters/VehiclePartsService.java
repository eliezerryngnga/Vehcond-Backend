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
import vehcon.dto.masters.VehiclePartDTO;
import vehcon.models.masters.VehicleParts;
import vehcon.repo.masters.VehiclePartsRepository;

@Service
@RequiredArgsConstructor
public class VehiclePartsService {
	
	private final VehiclePartsRepository vehiclePartsRepo;
	
	@Transactional
	public List<VehicleParts> getByVehicleParts()
	{
		return vehiclePartsRepo.findAllByOrderByVehiclePartCodeAsc();
	}
	
	@Auditable
	@Transactional
	public VehicleParts addVehicleParts(VehiclePartDTO dto) {
        VehicleParts vehPart = new VehicleParts();
        vehPart.setVehiclePartDescription(dto.getVehiclePartDescription());
        return vehiclePartsRepo.save(vehPart);
    }
	
	@Transactional
	public Page<VehicleParts> getAllVehicleParts(
			String globalSearchTerm,
			Pageable pageable
			)
	{
		Specification<VehicleParts> spec = Specification.where(null);

        if (globalSearchTerm != null && !globalSearchTerm.trim().isEmpty()) {
            List<String> globalSearchFields = List.of("vehiclePartDescription");
            Specification<VehicleParts> globalSearchSpec = SpecificationUtils.searchInFields(globalSearchTerm, globalSearchFields);
            if (globalSearchSpec != null) 
            {
                spec = spec.and(globalSearchSpec);
            }
        }
        return vehiclePartsRepo.findAll(spec, pageable);
    }
}
