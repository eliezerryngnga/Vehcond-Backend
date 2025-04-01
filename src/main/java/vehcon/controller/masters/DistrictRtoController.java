package vehcon.controller.masters;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vehcon.exception.UnauthorizedException;
import vehcon.models.masters.DistrictRto;
import vehcon.services.masters.DistrictRtoService;

@RestController
@RequestMapping("/district-rto")
@RequiredArgsConstructor
public class DistrictRtoController {
	
	private final DistrictRtoService districtRtoService;
	
	@GetMapping
	public List<DistrictRto> getDistrictRto()
	{
		try
		{
			return districtRtoService.getDistrictRto();
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
