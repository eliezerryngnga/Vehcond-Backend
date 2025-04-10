package vehcon.controller.masters;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vehcon.exception.UnauthorizedException;
import vehcon.models.masters.VehicleManufacturer;
import vehcon.services.masters.VehicleManufacturerService;

@RestController
@RequestMapping("/vehicle-manufacturer")
@RequiredArgsConstructor
public class VehicleManufacturerController {
	
	
	private final VehicleManufacturerService vehicleManufacturerService;
	
	@GetMapping 
	public List<VehicleManufacturer> getVehicleManufacturer()
	{
		try
		{
			return vehicleManufacturerService.getVehicleManufacturer();
		}
		catch(UnauthorizedException ex)
		{
			throw ex;
		}
		catch(Exception e)
		{
			throw e;
		}
	}
}
