package vehcon.controller.appdata;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vehcon.annotations.Auditable;
import vehcon.dto.appdata.VehicleDraftDTO;
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
}
