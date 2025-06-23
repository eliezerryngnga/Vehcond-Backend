package vehcon.services.masters;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vehcon.common.data.util.SpecificationUtils;
import vehcon.dto.masters.VehicleTypeDTO;
import vehcon.models.masters.VehicleType;
import vehcon.repo.masters.VehicleTypeRepository;

@Service
@RequiredArgsConstructor
public class VehicleTypeService {
	
	private final VehicleTypeRepository vehicleTypeRepo;
	
	public List<VehicleType> getByVehicleTypeId()
	{
		return vehicleTypeRepo.findAll();
	}
	
	public VehicleType addVehicleType(VehicleTypeDTO dto) {
        VehicleType vehType = new VehicleType();
        vehType.setVehicletypedescription(dto.getVehicleTypeDescription());
        return vehicleTypeRepo.save(vehType);
    }
	
	public Page<VehicleType> getAllVehicleType(
			String globalSearchTerm,
			Pageable pageable
			)
	{
		Specification<VehicleType> spec = Specification.where(null);

        if (globalSearchTerm != null && !globalSearchTerm.trim().isEmpty()) {
            List<String> globalSearchFields = List.of("financialyearfrom");
            Specification<VehicleType> globalSearchSpec = SpecificationUtils.searchInFields(globalSearchTerm, globalSearchFields);
            if (globalSearchSpec != null) 
            {
                spec = spec.and(globalSearchSpec);
            }
        }
        return vehicleTypeRepo.findAll(spec, pageable);
    }
}
