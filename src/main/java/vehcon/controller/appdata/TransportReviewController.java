package vehcon.controller.appdata;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vehcon.dto.appdata.ApproveRequestDTO;
import vehcon.dto.appdata.ReturnRequestDTO;
import vehcon.dto.appdata.VehicleDraftListDTO;
import vehcon.models.appdata.VehicleFinal;
import vehcon.models.vehiclecondemnations.VehicleDraft;
import vehcon.services.appdata.TransportService;

@RestController
@RequestMapping("/transport-review")
@RequiredArgsConstructor
@Slf4j
public class TransportReviewController {
	
    private final TransportService transportService;

    private static final int PROCESS_CODE_APPROVED_BY_TRANSPORT = 2;
    
    @GetMapping("/with-mvi")
    public ResponseEntity<?> getVehiclesPendingTransportApprovalWithMviReport(
    		@RequestParam(value="search", required = false) String searchTerm,
    		Pageable pageable
    		) { 
    	try 
    	{
    		 Page<VehicleDraftListDTO> pendingVehiclesPage = transportService.getVehiclesPendingApprovalWithMvi(searchTerm, pageable);
    	        return ResponseEntity.ok(pendingVehiclesPage);
        } 
    	catch (Exception e) 
    	{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("An unexpected error occurred while retrieving pending vehicles.");
        }
    } 

    @GetMapping("/without-mvi")
    public ResponseEntity<?> getVehiclesPendingTransportApprovalWithoutMviReport(
    		@RequestParam(value="search", required = false) String searchTerm,
    		Pageable pageable
    		)
    {
    	try {
    		Page<VehicleDraftListDTO> pendingVehiclePage = transportService.getVehiclesPendingApprovalWithoutMvi(searchTerm, pageable);
    		return ResponseEntity.ok(pendingVehiclePage);
    	}
    	catch(Exception e)
    	{
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    				.body("An unexpected error occurred while retrieving pending vehicles without Mvi Report");
    	}
    	
    }
    
    @PostMapping("/approve")
    public ResponseEntity<?> approveVehicle(
            @Valid @RequestBody ApproveRequestDTO approvalData) 
    {
    	 // Add a log to see the incoming data
        log.info("Attempting to approve vehicle with data: {}", approvalData);
        try {
        	
            VehicleFinal approvedVehicle = transportService.approveVehicle(approvalData);
            return ResponseEntity.ok(approvedVehicle);

        } catch (EntityNotFoundException enfEx) {
        	  log.warn("Approval failed - Entity not found: {}", enfEx.getMessage());
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(enfEx.getMessage());
        } catch (IllegalStateException isEx) {
        	log.warn("Approval failed - Illegal state: {}", isEx.getMessage());
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(isEx.getMessage()); // Return 400
        }
        catch (Exception e) {
        	 log.error("An unexpected error occurred during vehicle approval. DTO: {}", approvalData, e); 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("An unexpected error occurred while approving the vehicle.");
        }
    }
    
    @PostMapping("/reject/{applicationCode}")
    public ResponseEntity<?> rejectVehicle(
             @Valid @RequestBody ReturnRequestDTO returnData) {
        try {
            VehicleDraft returnedVehicleDraft = transportService.rejectVehicle(returnData);
            return ResponseEntity.ok(returnedVehicleDraft);
        } catch (EntityNotFoundException enfEx) {
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(enfEx.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("An unexpected error occurred while rejecting the vehicle.");
        }
    }
}
