package vehcon.services.masters;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vehcon.models.masters.VehicleManufacturer;
import vehcon.repo.masters.VehicleManufacturerRepository;

@Service
@RequiredArgsConstructor
public class VehicleManufacturerService {
	
	private final VehicleManufacturerRepository vehicleManufacturerRepository;
	
	public List<VehicleManufacturer> getVehicleManufacturer()
	{
		return vehicleManufacturerRepository.findAll();
	}
}
