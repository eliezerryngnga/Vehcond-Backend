package vehcon.controller.masters;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vehcon.exception.UnauthorizedException;
import vehcon.models.masters.VehicleParts;
import vehcon.services.masters.VehiclePartsService;

@RestController
@RequestMapping("/vehicle-parts")
@RequiredArgsConstructor
public class VehiclePartsController {
	
	private final VehiclePartsService vehiclePartsService;
	
	@GetMapping
	List<VehicleParts> getByVehiclePartsName()
	{
		try
		{
			return vehiclePartsService.getByVehicleParts();
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
