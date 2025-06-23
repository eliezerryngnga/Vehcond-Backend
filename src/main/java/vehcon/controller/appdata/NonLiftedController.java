package vehcon.controller.appdata;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vehcon.dto.appdata.VehicleDraftListDTO;
import vehcon.services.appdata.TransportService;

@RestController
@RequestMapping("/transport-non-lifted")
@RequiredArgsConstructor
public class NonLiftedController {
	
	private final TransportService transportService;
	
	@GetMapping("/list")
	public ResponseEntity<?> getNonLiftedVehicles(
			@RequestParam(value = "search", required = false) String searchTerm,
			Pageable pageable
			)
	{
		try {
			Page<VehicleDraftListDTO> nonLiftedVehicles = transportService.getNonLiftedVehicles(searchTerm, pageable);
			return ResponseEntity.ok(nonLiftedVehicles);
		}
		catch(Exception e)
		{
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An unexpected error occured while retrieving Non-Lifted Vehicles"); 
		}
	}

}