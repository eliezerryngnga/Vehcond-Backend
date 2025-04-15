package vehcon.controller.appdata;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vehcon.annotations.Auditable;
import vehcon.dto.appdata.VehicleDraftDTO;
import vehcon.services.appdata.VehicleDraftServices;

@RequestMapping("/draft")
@RestController
@RequiredArgsConstructor
@Slf4j
public class VehicleDraftController {

    private final VehicleDraftServices vehDraftService;

    @Auditable
    @PostMapping
    @Transactional
    public ResponseEntity<String> addVehicleDraft(@RequestBody VehicleDraftDTO vehicleDraft) {
//    	log.info(">>> DTO Received in Draft Controller: {}", vehicleDraft);
        try {
            String applicationCode = vehDraftService.addVehicleDraft(vehicleDraft);
            return new ResponseEntity<>("Vehicle draft created successfully with Application Code: " + applicationCode, HttpStatus.CREATED);
        } catch (RuntimeException ex) {
        	log.error("Failed to create vehicle draft", ex);
            // Log the exception for debugging purposes
//            ex.printStackTrace();
            return new ResponseEntity<>("Failed to create vehicle draft: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
//    @GetMapping("/list")
//    public ResponseEntity<List<VehicleDraftListDTO>> getDrafts() {
//    	log.info("Received request to fetch draft lists.");
//    	
//    	try
//    	{
//    		List<VehicleDraftListDTO> drafts = vehDraftService.getDraftsByProcessCode(1);
//    		return ResponseEntity.ok(drafts);
//    	}
//    	catch(Exception ex)
//    	{
//    		log.error("Failed to fetch vehicle drafts", ex);
//    		
//    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
//    	}
//    }
    
}