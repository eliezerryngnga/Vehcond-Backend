package vehcon.controller.masters;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vehcon.exception.UnauthorizedException;
import vehcon.models.masters.Districts;
import vehcon.services.masters.DistrictsServices;

@RestController
@RequestMapping("/districts")
@RequiredArgsConstructor
public class DistrictController {
	
	
	private final DistrictsServices districtsService;
	
	@GetMapping
	public List<Districts> getDistricts()
	{
		try
		{
			return districtsService.getByDistricts();
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
