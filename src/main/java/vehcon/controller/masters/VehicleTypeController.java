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
import vehcon.dto.masters.VehicleTypeDTO;
import vehcon.exception.UnauthorizedException;
import vehcon.models.auth.User;
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
	
	@GetMapping("/list")
	public ResponseEntity<Page<VehicleType>> getAllVehicleType(
			@RequestParam(required = false) String searchTerm,
			Pageable pageable,
			@AuthenticationPrincipal User user)
	{
		try
		{
			Page<VehicleType> vehicleTypePage = vehicleTypeService.getAllVehicleType(searchTerm, pageable);
			return ResponseEntity.ok(vehicleTypePage);
		}
		catch(Exception e)
		{
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(null);
		}
	}
	
	@PostMapping("/add-vehicle-type")
	public ResponseEntity<String> addVehicleType(@RequestBody VehicleTypeDTO dto)
	{
		try
		{
			VehicleType savedVehType = vehicleTypeService.addVehicleType(dto);
			
			return ResponseEntity.status(HttpStatus.CREATED)
					.body("Vehicle Type " + savedVehType.getVehicletypedescription() + " created successfully");
		}
		catch(Exception e)
		{
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to create vehicle type " + e.getMessage());
		}
	}
}
