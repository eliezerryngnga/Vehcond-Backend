package vehcon.services.masters;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vehcon.models.masters.VehicleType;
import vehcon.repo.masters.VehicleTypeRepository;

@Service
@RequiredArgsConstructor
public class VehicleTypeService {
	
	private final VehicleTypeRepository vehicleTypeRepository;
	
	public List<VehicleType> getByVehicleTypeId()
	{
		return vehicleTypeRepository.findAll();
	}
}
