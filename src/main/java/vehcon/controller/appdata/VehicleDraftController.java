package vehcon.controller.appdata;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vehcon.annotations.Auditable;
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
    
    @GetMapping("/list")
    public ResponseEntity<List<VehicleDraftListDTO>> getCurrentUsersDrafts(@AuthenticationPrincipal User user) {
        
    	Integer userDepartmentCode = user.getDepartmentCode();
    	
    	try {
        	
            int initialProcessCode = 1;
            List<VehicleDraftListDTO> drafts = vehDraftService.getDraftsByProcessCode(initialProcessCode, userDepartmentCode);
            return new ResponseEntity<>(drafts, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Failed to retrieve vehicle drafts", ex);
            // Return an empty list or an error response
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
//    @GetMapping("/{applicationCode}")
//    public ResponseEntity<VehicleDraft> getDraftById(@PathVariable String applicationCode, @AuthenticationPrincipal User user)
//    {
//    	Integer userDepartmentCode = user.getDepartmentCode();
//    	
//    	try
//    	{
//    		VehicleDraft draft = vehDraftService.getDraftById(applicationCode, userDepartmentCode)
//    		return ResponseEntity.ok(draft);
//    	}
//    	catch(Exception ex)
//    	{
//    		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//    	}
//    }

}