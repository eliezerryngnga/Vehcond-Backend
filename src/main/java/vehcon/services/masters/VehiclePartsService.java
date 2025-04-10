package vehcon.services.masters;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vehcon.models.masters.VehicleParts;
import vehcon.repo.masters.VehiclePartsRepository;

@Service
@RequiredArgsConstructor
public class VehiclePartsService {
	
	private final VehiclePartsRepository vehiclePartsRepository;
	
	public List<VehicleParts> getByVehicleParts()
	{
		return vehiclePartsRepository.findAllByOrderByVehiclePartCodeAsc();
	}
}
