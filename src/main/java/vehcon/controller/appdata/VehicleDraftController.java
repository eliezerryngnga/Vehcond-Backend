package vehcon.controller.appdata;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import vehcon.annotations.Auditable;
import vehcon.dto.appdata.VehicleDraftDTO;
import vehcon.services.appdata.VehicleDraftServices;

@RequestMapping("/draft")
@RestController
@RequiredArgsConstructor
public class VehicleDraftController {

    private final VehicleDraftServices vehDraftService;

    @Auditable
    @PostMapping
    @Transactional
    public ResponseEntity<String> addVehicleDraft(@RequestBody VehicleDraftDTO vehicleDraft) {
//    	System.out.print("Received VichleDraft:" + vehicleDraft);
//    	
//    	 System.out.println("Received VehicleDraft:");
//    	    System.out.println("  Registered District: " + vehicleDraft.getRegisteredDistrict());
//    	    System.out.println("  RTO No: " + vehicleDraft.getRtoNo());
//    	    System.out.println("  Vehicle Registration Number: " + vehicleDraft.getVehicleRegistrationNumber());
//    	    System.out.println("  Financial Year Code: " + vehicleDraft.getFinancialYearCode());
//    	    System.out.println("  Department Code: " + vehicleDraft.getDepartmentCode());
//    	    // ... log other fields
//    	    System.out.println("  Address1: " + vehicleDraft.getAddress1());
//    	    System.out.println("  Address2: " + vehicleDraft.getAddress2());
//    	    System.out.println("  Directorate Letter No: " + vehicleDraft.getDirectorateLetterNo());
//    	    System.out.println("  Directorate Letter Date: " + vehicleDraft.getDirectorateLetterDate());
//    	    System.out.println("  Forwarding Letter No: " + vehicleDraft.getForwardingLetterNo());
//    	    System.out.println("  Gov Forwarding Letter Date: " + vehicleDraft.getGovForwardingLetterDate());
//    	    System.out.println("  Vehicle Parts Draft: " + vehicleDraft.getVehiclePartsDraft());
    	    
        try {
            String applicationCode = vehDraftService.addVehicleDraft(vehicleDraft);
            return new ResponseEntity<>("Vehicle draft created successfully with Application Code: " + applicationCode, HttpStatus.CREATED);
        } catch (Exception ex) {
            // Log the exception for debugging purposes
            ex.printStackTrace();
            return new ResponseEntity<>("Failed to create vehicle draft: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}