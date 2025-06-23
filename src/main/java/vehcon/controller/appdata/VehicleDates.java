package vehcon.controller.appdata;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vehcon.services.appdata.TransportService;

@RestController
@RequestMapping("/dates")
@RequiredArgsConstructor
public class VehicleDates {
	
	private final TransportService transportService;
	
	private static final int PROCESS_CODE_DRAFT = 1;
	
	private static final int PROCESS_CODE_REJECT_VEHICLE = 14;
	
	private static final int PROCESS_CODE_SUBMITTED_TO_TRANSPORT = 15;
	
	private static final int PROCESS_CODE_APPROVED_BY_TRANSPORT = 2; //For 3a.
	
	private static final int PROCESS_CODE_PLACED_BEFORE_VCC = 3; //3b.
	
	private static final int PROCESS_CODE_PRICE_FIXED_BY_TC = 5; //3c.
	
	private static final int PROCESS_CODE_ALLOTMENT_OF_VEHICLE = 7; //3e.
	
	private static final int PROCESS_CODE_FOR_TENDER = 11;
	
	private static final int PROCESS_CODE_FOR_SCRAP_TC = -2;
	
	private static final int PROCESS_CODE_LIFTED_VEHICLE = 9;
	
	private static final int PROCESS_CODE_NON_LIFTED_VEHICLE = 10;
	
	@GetMapping("/approved")
    public ResponseEntity<?> getAvailableApprovedDates() 
    {
    	try
    	{
    		List<Map<String, Object>> dates = transportService.getAvailableDatesByProcessCode(PROCESS_CODE_APPROVED_BY_TRANSPORT);
    		return ResponseEntity.ok(dates);
    	}
    	catch(Exception e)
    	{
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching available dates");
    	}
    }
	
	@GetMapping("/condemned-vehicle")
    public ResponseEntity<?> getAvailableCondemnedVehicleDates() 
    {
    	try
    	{
    		List<Map<String, Object>> dates = transportService.getAvailableDatesByProcessCode(PROCESS_CODE_PLACED_BEFORE_VCC);
    		return ResponseEntity.ok(dates);
    	}
    	catch(Exception e)
    	{
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching available dates");
    	}
    }
	
	@GetMapping("/circulated")
    public ResponseEntity<?> getAvailableCirculatedDates() 
    {
    	try
    	{
    		List<Map<String, Object>> dates = transportService.getAvailableDatesByProcessCode(PROCESS_CODE_PRICE_FIXED_BY_TC);
    		return ResponseEntity.ok(dates);
    	}
    	catch(Exception e)
    	{
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching available dates");
    	}
    }
	
	@GetMapping("/allotted")
    public ResponseEntity<?> getAvailableAllottedDates() 
    {
    	try
    	{
    		List<Map<String, Object>> dates = transportService.getAvailableDatesByProcessCode(PROCESS_CODE_ALLOTMENT_OF_VEHICLE);
    		return ResponseEntity.ok(dates);
    	}
    	catch(Exception e)
    	{
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching available dates");
    	}
    }
	
	@GetMapping("/tendered")
    public ResponseEntity<?> getAvailableTenderedDates() 
    {
    	try
    	{
    		List<Map<String, Object>> dates = transportService.getAvailableDatesByProcessCode(PROCESS_CODE_FOR_TENDER);
    		return ResponseEntity.ok(dates);
    	}
    	catch(Exception e)
    	{
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching available dates");
    	}
    }
	
	@GetMapping("/lifted")
    public ResponseEntity<?> getAvailableLiftedDates() 
    {
    	try
    	{
    		List<Map<String, Object>> dates = transportService.getAvailableDatesByProcessCode(PROCESS_CODE_LIFTED_VEHICLE);
    		return ResponseEntity.ok(dates);
    	}
    	catch(Exception e)
    	{
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching available dates");
    	}
    }
	
	@GetMapping("/scrapped")
    public ResponseEntity<?> getAvailableScrappedDates() 
    {
    	try
    	{
    		List<Map<String, Object>> dates = transportService.getAvailableDatesByProcessCode(PROCESS_CODE_FOR_SCRAP_TC);
    		return ResponseEntity.ok(dates);
    	}
    	catch(Exception e)
    	{
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching available dates");
    	}
    }
	
	@GetMapping("/non-lifted")
    public ResponseEntity<?> getAvailableNonLiftedDates() 
    {
    	try
    	{
    		List<Map<String, Object>> dates = transportService.getAvailableDatesByProcessCode(PROCESS_CODE_NON_LIFTED_VEHICLE);
    		return ResponseEntity.ok(dates);
    	}
    	catch(Exception e)
    	{
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching available dates");
    	}
    }
}
