package vehcon.controller.appdata;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vehcon.dto.appdata.VehicleDraftDTO;
import vehcon.dto.appdata.VehicleDraftListDTO;
import vehcon.models.auth.User;
import vehcon.services.appdata.VehicleDraftServices;

@RequestMapping("/draft")
@RestController
@RequiredArgsConstructor
@Slf4j
public class VehicleDraftController {

    private final VehicleDraftServices vehDraftService;

//    @Auditable
//    @PostMapping
//    @Transactional
//    public ResponseEntity<String> addVehicleDraft(@RequestBody VehicleDraftDTO vehicleDraft) {
////    	log.info(">>> DTO Received in Draft Controller: {}", vehicleDraft);
//        try {
//            String applicationCode = vehDraftService.addVehicleDraft(vehicleDraft);
//            return new ResponseEntity<>("Vehicle draft created successfully with Application Code: " + applicationCode, HttpStatus.CREATED);
//        } catch (RuntimeException ex) {
//        	log.error("Failed to create vehicle draft", ex);
//
//            return new ResponseEntity<>("Failed to create vehicle draft: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
    
    @PostMapping
    @Transactional
    public ResponseEntity<?> saveOrUpdateVehicleDraft(@RequestBody VehicleDraftDTO vehicleDraft) {
//    	log.info(">>> DTO Received in Draft Controller: {}", vehicleDraft);
        try {
            String applicationCode = vehDraftService.saveOrUpdateVehicleDraft(vehicleDraft);
            
            Map<String, String> response = new HashMap<>();
            response.put("applicationCode", applicationCode);
            
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException ex) {
        	log.error("Failed to save or update vehicle draft", ex);
        	
        	Map<String, String> errorResponse = new HashMap<>();
        	errorResponse.put("meesage", "Failed to process draft: " + ex.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/list")
    public ResponseEntity<?> getCurrentUsersDrafts(
    		@RequestParam(value = "search", required = false) String searchTerm,
    		Pageable pageable,
    		@AuthenticationPrincipal User user
    	) 
    {
    	try {
            
            Page<VehicleDraftListDTO> draftsPage = vehDraftService.getDraftVehicles(
                    searchTerm, 
                    pageable,
                    user
            );

            return new ResponseEntity<>(draftsPage, HttpStatus.OK);

        } catch (Exception ex) {
//            log.error("Failed to retrieve paginated vehicle drafts for department {}: {}", userDepartmentCode, ex.getMessage(), ex);
            
            return new ResponseEntity<>("Error retrieving drafts: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/reject-list")
    public ResponseEntity<?> getReturedVehicles(
    		@RequestParam(value = "search", required = false) String searchTerm,
    		Pageable pageable,
    		@AuthenticationPrincipal User user
    		)
    {
    	try
    	{
    		Page<VehicleDraftListDTO> rejectedVehicle = vehDraftService.getRejectedVehicles(searchTerm, pageable, user);
    		
    		return new ResponseEntity<>(rejectedVehicle, HttpStatus.OK);
    	}
    	catch(Exception e)
    	{
    		return new ResponseEntity<>("Error retrieving rejected vehicles: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    
    @GetMapping("/details/{applicationCode}")
    public ResponseEntity<?> getVehicleDraft(@PathVariable String applicationCode)
    {
    	try
    	{
    		VehicleDraftDTO dto = vehDraftService.getVehicleDraftByApplicationCode(applicationCode);
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