package vehcon.controller.appdata;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vehcon.annotations.Auditable;
import vehcon.dto.appdata.VehicleDetailsDTO;
import vehcon.dto.appdata.VehicleDraftDTO;
import vehcon.dto.appdata.VehicleDraftListDTO;
import vehcon.models.auth.User;
import vehcon.services.appdata.VehicleFinalServices;


@RestController
@RequestMapping("/final-submit")
@RequiredArgsConstructor
@Slf4j
public class VehicleFinalController {

	private final VehicleFinalServices vehFinalService;
	
	@Auditable
	@PostMapping
	@Transactional
	public ResponseEntity<String> addVehicleFinal(@RequestBody VehicleDraftDTO vehicleFinalDTO)
	{
		log.info(">>> DTO Received in Final Controller: {}", vehicleFinalDTO); // Log here!
		try {
			String applicationCode = vehFinalService.addVehicleFinal(vehicleFinalDTO);
			return new ResponseEntity<>("Vehicle created successfully with Application Code: " + applicationCode, HttpStatus.CREATED);
        } catch (Exception ex) {
            // Log the exception for debugging purposes
            ex.printStackTrace();
            return new ResponseEntity<>("Failed to create vehicle draft: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	
	@GetMapping("/list")
	public ResponseEntity<?> getcurrentUserFinal(
			@AuthenticationPrincipal User user,
			Pageable pageable,
			@RequestParam(value="search", required= false) String searchTerm)
	{
		Integer userDepartmentCode = null;
		
		if(user.getDepartment() != null)
		{
			
			userDepartmentCode = user.getDepartment().getDepartmentCode();		}
		
		try {
			int initialProcessCode = 15;
			
			Page<VehicleDraftListDTO> finalList = vehFinalService.getFilteredFinalsByProcessCode(
					initialProcessCode,
					userDepartmentCode,
					searchTerm,
					pageable
					);
			
			return new ResponseEntity<>(finalList, HttpStatus.OK);
		}
		catch(Exception ex)
		{
			return new ResponseEntity<>("Error retrieving final submitted list: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/details/{applicationCode}")
    public ResponseEntity<?> getVehicleDetails(@PathVariable String applicationCode)
    {
    	try
    	{
    		VehicleDetailsDTO dto = vehFinalService.getVehicleDetailsByApplicationCode(applicationCode);
    		return new ResponseEntity<>(dto, HttpStatus.OK);
    	}
    	catch(EntityNotFoundException enfe)
    	{
    		return new ResponseEntity<>(enfe.getMessage(), HttpStatus.NOT_FOUND);
    	}
    	catch(Exception e)
    	{
    		return new ResponseEntity<>("An unexpected error occurred while retrieving the vehicle draft.", HttpStatus.INTERNAL_SERVER_ERROR); 
    	}
    }

	
}
