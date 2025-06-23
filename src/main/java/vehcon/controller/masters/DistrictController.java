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
import vehcon.dto.masters.DistrictsDTO;
import vehcon.exception.UnauthorizedException;
import vehcon.models.auth.User;
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
	
	@GetMapping("/list")
	public ResponseEntity<Page<Districts>> getAllDistricts(
			@RequestParam(required = false) String searchTerm,
			Pageable pageable,
			@AuthenticationPrincipal User user
			)
	{
		
		try
		{
			Page<Districts> district = districtsService.getAllDistricts(searchTerm, pageable);
			return ResponseEntity.ok(district);
		}
		catch(Exception e)
		{
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(null);
		}
	}
	
	@PostMapping("/add-districts")
	public ResponseEntity<String> addDistrict(@RequestBody DistrictsDTO dto)
	{
		try
		{
			Districts savedDistrict = districtsService.addDistricts(dto);
			return ResponseEntity
					.status(HttpStatus.CREATED)
					.body("District " + savedDistrict.getDistrictName() + "created successfully");
		}
		catch(Exception e)
		{
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to create district: " + e.getMessage());
		}
	}
}
