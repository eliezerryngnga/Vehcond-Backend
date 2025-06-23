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
import vehcon.dto.masters.VehicleManufacturerDTO;
import vehcon.models.masters.VehicleManufacturer;
import vehcon.repo.masters.VehicleManufacturerRepository;

@Service
@RequiredArgsConstructor
public class VehicleManufacturerService {
	
	private final VehicleManufacturerRepository vehicleManufacturerRepo;
	
	@Transactional
	public List<VehicleManufacturer> getVehicleManufacturer()
	{
		return vehicleManufacturerRepo.findAll();
	}
	
	@Auditable
	@Transactional
	public VehicleManufacturer addVehicleManufacturer(VehicleManufacturerDTO dto) {
        VehicleManufacturer vehManufacturer = new VehicleManufacturer();
        vehManufacturer.setVehicleManufacturerName(dto.getVehicleManufacturerName());
        return vehicleManufacturerRepo.save(vehManufacturer);
    }
	
	@Transactional
	public Page<VehicleManufacturer> getAllVehicleManufacturer(
			String globalSearchTerm,
			Pageable pageable
			)
	{
		Specification<VehicleManufacturer> spec = Specification.where(null);

        if (globalSearchTerm != null && !globalSearchTerm.trim().isEmpty()) {
            List<String> globalSearchFields = List.of("vehicleManufacturerName");
            Specification<VehicleManufacturer> globalSearchSpec = SpecificationUtils.searchInFields(globalSearchTerm, globalSearchFields);
            if (globalSearchSpec != null) 
            {
                spec = spec.and(globalSearchSpec);
            }
        }
        return vehicleManufacturerRepo.findAll(spec, pageable);
    }
	
}
