package vehcon.controller.masters;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vehcon.exception.UnauthorizedException;
import vehcon.models.masters.VehicleType;
import vehcon.services.masters.VehicleTypeService;

@RestController
@RequestMapping("/vehicle-type")
@RequiredArgsConstructor
public class VehicleTypeController {
	
	private final VehicleTypeService vehicleTypeService;
	
	@GetMapping
	public List<VehicleType> getVehicleType()
	{
		try
		{
			return vehicleTypeService.getByVehicleTypeId();
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
