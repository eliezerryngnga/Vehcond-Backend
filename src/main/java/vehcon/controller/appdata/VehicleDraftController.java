package vehcon.controller.appdata;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vehcon.annotations.Auditable;
import vehcon.models.vehiclecondemnations.VehicleDraft;
import vehcon.services.appdata.VehicleDraftServices;

@RequestMapping("/draft")
@RestController
@RequiredArgsConstructor
public class VehicleDraftController {

    private final VehicleDraftServices vehDraftService;

    @Auditable
    @PostMapping
    public ResponseEntity<String> addVehicleDraft(@Valid @RequestBody VehicleDraft vehicleDraft) {
    	System.out.print("Received VichleDraft:" + vehicleDraft);
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