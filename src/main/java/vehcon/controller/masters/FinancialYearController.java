package vehcon.controller.masters;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vehcon.dto.masters.FinancialYearDTO;
import vehcon.exception.UnauthorizedException;
import vehcon.models.auth.User;
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
	
	@GetMapping("/list")
	public ResponseEntity<Page<FinancialYear>> getAllFinancialYear(
			@RequestParam(required = false) String searchTerm,
			Pageable pageable,
			@AuthenticationPrincipal User user)
	{
		try
		{
			Page<FinancialYear> financialYearPage = financialYearService.getAllFinancialYear(searchTerm, pageable);
			return ResponseEntity.ok(financialYearPage);
		}
		catch(Exception e)
		{
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(null);
		}
	}
	
	@PostMapping("/add-financial-year")
	public ResponseEntity<String> addFinancialYear(@RequestBody FinancialYearDTO dto)
	{
		try
		{
			FinancialYear savedFinancialYear = financialYearService.addFinancialYear(dto);
			
			return ResponseEntity.status(HttpStatus.CREATED)
						.body("Financial Year " + savedFinancialYear.getFinancialYearFrom() + " created successfully");
		}
		catch(Exception e)
		{
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to create financial year " + e.getMessage());
		}
	}
}
