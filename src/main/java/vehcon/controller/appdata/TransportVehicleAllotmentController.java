package vehcon.controller.appdata;

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

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vehcon.dto.appdata.AllottVehiclesDTO;
import vehcon.dto.appdata.VehicleDraftListDTO;
import vehcon.models.appdata.VehicleFinal;
import vehcon.models.auth.User;
import vehcon.services.appdata.TransportService;

@RestController
@RequestMapping("/transport-allotment")
@RequiredArgsConstructor
public class TransportVehicleAllotmentController {

	private final TransportService transportService;
	
	@GetMapping("/priced")
	public ResponseEntity<?> getPricedVehicles(
			@RequestParam(value = "search", required = false) String searchTerm,
			Pageable pageable,
			@AuthenticationPrincipal User user
			)
	{
		try
		{
			
			Page<VehicleDraftListDTO> pricedVehicles = transportService.getPricedVehicles(searchTerm, pageable, user);
			return ResponseEntity.ok(pricedVehicles);
		}
		catch(Exception e)
		{
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An unexpected error occured while retrieving Placed Vehicles");
		}
	}
	
	@PostMapping("/allot")
	public ResponseEntity<?> allotVehicles(
			@Valid @RequestBody AllottVehiclesDTO dto
			)
	{
		try
		{
			VehicleFinal allotVehicle = transportService.allotVehicles(dto);
			//logger.info("Vehicle allotment processed successfully for application code: {}. New process code: {}",
                    //dto.getApplicationCode(), updatedVehicle.getProcesscode() != null ? updatedVehicle.getProcesscode().getProcesscode() : "N/A");
            return ResponseEntity.ok(allotVehicle);
        } catch (EntityNotFoundException e) {
            //logger.warn("Entity not found during vehicle allotment for application code {}: {}", dto.getApplicationCode(), e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) { // Catching potential explicit illegal arguments from service
            //logger.warn("Invalid argument during vehicle allotment for application code {}: {}", dto.getApplicationCode(), e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            //logger.error("Error during vehicle allotment for application code {}: {}", dto.getApplicationCode(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred during allotment: " + e.getMessage());
        }
	}
	
}
