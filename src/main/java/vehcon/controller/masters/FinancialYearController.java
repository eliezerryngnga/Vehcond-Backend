package vehcon.controller.masters;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vehcon.exception.UnauthorizedException;
import vehcon.models.masters.FinancialYear;
import vehcon.services.masters.FinancialYearService;

@RestController
@RequestMapping("/financial-year")
@RequiredArgsConstructor
public class FinancialYearController {
	
	private final FinancialYearService financialYearService;
	
	@GetMapping
	public List<FinancialYear> getFinancialYear()
	{
		try
		{
			return financialYearService.getFinancialYear();
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
