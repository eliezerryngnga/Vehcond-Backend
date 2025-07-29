package vehcon.services.appdata;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vehcon.annotations.Auditable;
import vehcon.common.data.util.VehicleSpecifications;
import vehcon.dto.appdata.AllottVehiclesDTO;
import vehcon.dto.appdata.ApproveRequestDTO;
import vehcon.dto.appdata.LiftingDTO;
import vehcon.dto.appdata.PlacedBeforeVccRequestDTO;
import vehcon.dto.appdata.ReturnRequestDTO;
import vehcon.dto.appdata.ScrapDTO;
import vehcon.dto.appdata.TenderVehiclesDTO;
import vehcon.dto.appdata.VehicleDraftListDTO;
import vehcon.dto.appdata.YearMonthDTO;
import vehcon.dto.appdata.YearMonthProjection;
import vehcon.models.appdata.AllottedVehicle;
import vehcon.models.appdata.LiftedVehicles;
import vehcon.models.appdata.Scrap;
import vehcon.models.appdata.TenderVehicles;
import vehcon.models.appdata.Vcctc;
import vehcon.models.appdata.VcctcTemp;
import vehcon.models.appdata.VehicleFinal;
import vehcon.models.appdata.VehiclePartsConditionFinal;
import vehcon.models.appdata.Verification;
import vehcon.models.auth.User;
import vehcon.models.masters.Processes;
import vehcon.models.vehiclecondemnations.VehicleDraft;
import vehcon.models.vehiclecondemnations.VehiclePartsConditionDraft;
import vehcon.repo.appdata.AllottedVehiclesRepository;
import vehcon.repo.appdata.LiftedVehiclesRepository;
import vehcon.repo.appdata.ProcessesRepository;
import vehcon.repo.appdata.ScrapRepository;
import vehcon.repo.appdata.TenderVehiclesRepository;
import vehcon.repo.appdata.VccTcRepository;
import vehcon.repo.appdata.VcctcTempRepository;
import vehcon.repo.appdata.VehicleDraftRepository;
import vehcon.repo.appdata.VehicleFinalRepository;
import vehcon.repo.appdata.VehiclePartsConditionDraftRepository;
import vehcon.repo.appdata.VehiclePartsConditionFinalRepository;
import vehcon.repo.appdata.VerificationRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransportService {
	private static final int PROCESS_CODE_DRAFT = 1;
	
	private static final int PROCESS_CODE_REJECT_VEHICLE = 14;
	
	private static final int PROCESS_CODE_SUBMITTED_TO_TRANSPORT = 15;
	
	private static final int PROCESS_CODE_APPROVED_BY_TRANSPORT = 2;
	
	private static final int PROCESS_CODE_PLACED_BEFORE_VCC = 3; //3b.
	
	private static final int PROCESS_CODE_PRICE_FIXED_BY_TC = 5; //3c.
	
	private static final int PROCESS_CODE_ALLOTMENT_OF_VEHICLE = 7; //3e.
	
	private static final int PROCESS_CODE_FOR_TENDER = 11;
	
	private static final int PROCESS_CODE_FOR_SCRAP_TC = -2;
	
	private static final int PROCESS_CODE_LIFTED_VEHICLE = 9;
	
	private static final int PROCESS_CODE_NON_LIFTED_VEHICLE = 10;
	
	private static final String Mvi_Available_Status = "Y";
	
	
	private final VehicleFinalRepository vehicleFinalRepo;
	private final VehiclePartsConditionFinalRepository vehPartsConditionRepo;
	private final ProcessesRepository processRepo;
	private final VerificationRepository verificationRepo;
	
	private final VehicleDraftRepository vehDraftRepo;
	private final VehiclePartsConditionDraftRepository vehPartsConditionDraftRepo;
	
	private final VcctcTempRepository vcctcTempRepo;
	
	private final VccTcRepository vcctcRepo;
	
	private final AllottedVehiclesRepository allottedVehicleRepo;
	
	private final ScrapRepository scrapRepo;
	
	
	private final TenderVehiclesRepository tenderRepo;
	
	private final LiftedVehiclesRepository liftedVehicleRepo;
	
	private static final List<Integer> Heavy_Vehicle_Type_Codes = Arrays.asList(1,3);
	
	private static final List<String> searchableFields = Arrays.asList(
			"registrationNo",
			"vehicledescription",
			"applicationcode"
			);
	
	//To be Approved
	@Transactional(readOnly = true)
    public Page<VehicleDraftListDTO> getVehiclesPendingApprovalWithMvi(
    		String searchTerm, 
    		Pageable pageable
    	) 
	{
        log.info("Fetching vehicles pending approval (Process Code: {}) with Mvi Report. SearchTerm: '{}', Pageable: {}",
                 PROCESS_CODE_SUBMITTED_TO_TRANSPORT, searchTerm, pageable);
        
        Specification<VehicleFinal> finalSpec = Specification
        		.where(VehicleSpecifications.hasProcessCode(PROCESS_CODE_SUBMITTED_TO_TRANSPORT))
        		.and(VehicleSpecifications.isMviAvailable(true,  Mvi_Available_Status))
        		.and(VehicleSpecifications.hasSearchTerm(searchTerm, searchableFields));
        
        return findAndMapVehicles(finalSpec, pageable);
    }
	
	@Transactional(readOnly = true)
	public Page<VehicleDraftListDTO> getVehiclesPendingApprovalWithoutMvi(
			String searchTerm,
			Pageable pageable
			)
	{
		log.info("Fetching vehicles pending approval (Process Code: {}) WITHOUT MVI Report. SearchTerm: '{}', Pageable: {}",
                PROCESS_CODE_SUBMITTED_TO_TRANSPORT, searchTerm, pageable);
		
		Specification<VehicleFinal> finalSpec = Specification
				.where(VehicleSpecifications.hasProcessCode(PROCESS_CODE_SUBMITTED_TO_TRANSPORT))
				.and(VehicleSpecifications.isMviAvailable(false, Mvi_Available_Status))
				.and(VehicleSpecifications.hasSearchTerm(searchTerm, searchableFields));
		
		return findAndMapVehicles(finalSpec, pageable);
	}
	
	
	//3a. CASE I
	@Auditable
	@Transactional
    public VehicleFinal approveVehicle(ApproveRequestDTO approvalData) 
	{
		String applicationCode = approvalData.getApplicationCode();
		try
		{
			Processes approvedProcess = processRepo.findById(PROCESS_CODE_APPROVED_BY_TRANSPORT)
		            .orElseThrow(() -> new EntityNotFoundException("Process configuration error: Process code " + PROCESS_CODE_APPROVED_BY_TRANSPORT + " not found."));
			
	        VehicleFinal vehicle = vehicleFinalRepo.findByApplicationCodeAndProcesscodeProcesscode(applicationCode, PROCESS_CODE_SUBMITTED_TO_TRANSPORT)
	                .orElseThrow(() -> new EntityNotFoundException("Vehicle with application code " + applicationCode + " not found or not awaiting transport action (Process Code != 2)."));

	        vehicle.setProcesscode(approvedProcess); 
	        if(vehicle.getRemarks() != null)
	        {
	        	vehicle.setRemarks(null);
	        }
	        
	        VehicleFinal updatedVehicle = vehicleFinalRepo.save(vehicle);

	        Verification verification = new Verification();
	        
	        verification.setEntryDate(LocalDate.now());
	        	  
	        verification.setVehicleFinal(updatedVehicle);
	        
	        verificationRepo.save(verification);

	        return updatedVehicle;
		}
		catch(Exception e)
		{
			throw e;
		}
		
	}

	@Auditable
	@Transactional
	public VehicleDraft rejectVehicle(ReturnRequestDTO returnData)
	{
		String applicationCode = returnData.getApplicationCode();
		try
		{
			//Process code 14
			Processes returnedProcess = processRepo.findById(PROCESS_CODE_REJECT_VEHICLE)
					.orElseThrow(() -> new EntityNotFoundException("Process code " + PROCESS_CODE_REJECT_VEHICLE+ " not found"));
			
			VehicleFinal vehFinal = vehicleFinalRepo.findByApplicationCodeAndProcesscodeProcesscode(applicationCode, PROCESS_CODE_SUBMITTED_TO_TRANSPORT)
					.orElseThrow(() -> new EntityNotFoundException("Vehicle with application code " + applicationCode + " and with process code " + PROCESS_CODE_SUBMITTED_TO_TRANSPORT + " not found"));
			
			List<VehiclePartsConditionFinal> finalParts = vehPartsConditionRepo.findByApplicationCode(vehFinal);
			
			VehicleDraft vehDraft = mapVehicleFinalToDraft(vehFinal, returnedProcess, returnData.getRemarks());
			
			VehicleDraft savedDraft = vehDraftRepo.save(vehDraft);
			
			if(finalParts != null && !finalParts.isEmpty())
			{
				List<VehiclePartsConditionDraft> draftParts = finalParts
						.stream()
						.map(finalPart -> mapPartFinalToDraft(finalPart, savedDraft))
						.collect(Collectors.toList());
				
				vehPartsConditionDraftRepo.saveAll(draftParts);
				
				vehPartsConditionRepo.deleteAllInBatch(finalParts);
				
			}
			
			vehicleFinalRepo.delete(vehFinal);
			
			return savedDraft;
			
		}
		catch(EntityNotFoundException enfe)
		{
			//log.error("Rejection failed for {}: {}", applicationCode, enfe.getMessage());
            throw enfe;
		}
		catch(Exception ex)
		{
			//log.error("An unexpected error occurred during rejection of vehicle {}: {}", applicationCode, ex.getMessage(), ex);
            throw new RuntimeException("Failed to reject vehicle " + applicationCode + " due to an internal error.", ex);
		}
		
	}

	//To be Placed Before VCC.

	@Transactional(readOnly = true)
	public Page<VehicleDraftListDTO> getApprovedVehicles(
			String searchTerm,
			Integer year,
			Integer month,
			Pageable pageable,
			User user
			)
	{
		log.info("Fetching approved vehicles (Process Code: {}). SearchTerm: '{}', Pageable: {}", PROCESS_CODE_APPROVED_BY_TRANSPORT, searchTerm, pageable);
		
		Specification<VehicleFinal> finalSpec = Specification
				.where(VehicleSpecifications.hasProcessCode(PROCESS_CODE_APPROVED_BY_TRANSPORT))
				.and(VehicleSpecifications.hasSearchTerm(searchTerm, searchableFields))
				.and(VehicleSpecifications.hasVerificationDateIn(year, month));
		
		if (!userHasAnyRole(user, "ADMIN", "TD")) {
		    Integer deptCode = user.getDepartment() != null ? user.getDepartment().getDepartmentCode() : null;
		    if (deptCode == null) {
		        throw new AccessDeniedException("User does not have a department assigned.");
		    }
		    finalSpec = finalSpec.and(VehicleSpecifications.isInDepartment(deptCode));
		}
				
		return findAndMapVehicles(finalSpec, pageable);
	}
	
	@Auditable
	@Transactional
	public VehicleFinal placeVehicleBeforeVcc(PlacedBeforeVccRequestDTO dto)
	{
		 try {
			 	String applicationCode = dto.getApplicationCode();
		        // Fetch the vehicle with current process code = 2 (Approved by Transport)
		        VehicleFinal vehicle = vehicleFinalRepo.findByApplicationCodeAndProcesscodeProcesscode(applicationCode, PROCESS_CODE_APPROVED_BY_TRANSPORT
		        ).orElseThrow(() -> new EntityNotFoundException(
		            "Vehicle not found or not approved by Transport for application code: " + dto.getApplicationCode()
		        ));

		        // Set process code = 3 (Placed before VCC)
		        Processes placedBeforeVccProcess = processRepo.findById(PROCESS_CODE_PLACED_BEFORE_VCC)
		            .orElseThrow(() -> new EntityNotFoundException("Process code " + PROCESS_CODE_PLACED_BEFORE_VCC + " not found"));

		        vehicle.setProcesscode(placedBeforeVccProcess);
		        
		        VehicleFinal updatedVehicle = vehicleFinalRepo.save(vehicle);
		        log.info("Vehicle {} process code updated to {}.", applicationCode, PROCESS_CODE_PLACED_BEFORE_VCC);
		     
		        VcctcTemp vccRecord = new VcctcTemp();
		        
		        vccRecord.setLetterNoDate(dto.getVccLetterNo()+ "|" + dto.getVccLetterDate());
		        vccRecord.setEntryDate(dto.getVccLetterDate());
		        vccRecord.setVcctc("V"); 
		        vccRecord.setPriceApproved("N");
		        
		        vccRecord.setVehicleFinal(updatedVehicle);
		        
		        vcctcTempRepo.save(vccRecord);

		        return updatedVehicle;

		    } catch (EntityNotFoundException e) {
		        log.error("Failed to place vehicle before VCC for {}: {}", dto.getApplicationCode(), e.getMessage());
		        throw e;
		    } catch (Exception e) {
		        log.error("Unexpected error during placing before VCC: {}", e.getMessage(), e);
		        throw new RuntimeException("Error placing vehicle before VCC", e);
		    }
		}
	
	
	
	//Price as Fixed By TC
	@Transactional(readOnly = true)
	public Page<VehicleDraftListDTO> getVehiclesPlacedBeforeVcc(
			String searchTerm,
			Integer year,
			Integer month,
			Pageable pageable,
			User user
			)
	{
		Specification<VehicleFinal> finalSpec = Specification
				.where(VehicleSpecifications.hasProcessCode(PROCESS_CODE_PLACED_BEFORE_VCC))
				.and(VehicleSpecifications.hasSearchTerm(searchTerm, searchableFields))
				.and(VehicleSpecifications.hasVcctcTempDateIn(year, month));
		
		if (!userHasAnyRole(user, "ADMIN", "TD")) {
		    Integer deptCode = user.getDepartment() != null ? user.getDepartment().getDepartmentCode() : null;
		    if (deptCode == null) {
		        throw new AccessDeniedException("User does not have a department assigned.");
		    }
		    finalSpec = finalSpec.and(VehicleSpecifications.isInDepartment(deptCode));
		}
		return findAndMapVehicles(finalSpec, pageable);	
	}
	
	@Auditable
	@Transactional
	public VehicleFinal priceFixationByTc(PlacedBeforeVccRequestDTO dto) {
	    try {
	        String applicationCode = dto.getApplicationCode();
	        log.info("Starting priceFixationByTc for applicationCode: {}, PriceApproved DTO: '{}', Price DTO: {}",
	                applicationCode, dto.getPriceApproved(), dto.getPrice());

	        // Fetch vehicle with current process code = 3 (PROCESS_CODE_PLACED_BEFORE_VCC)
	        VehicleFinal vehicle = vehicleFinalRepo.findByApplicationCodeAndProcesscodeProcesscode(
	                applicationCode, PROCESS_CODE_PLACED_BEFORE_VCC)
	                .orElseThrow(() -> {
	                    log.warn("Vehicle not found or not in correct state (PROCESS_CODE_PLACED_BEFORE_VCC) for appCode: {}", applicationCode);
	                    return new EntityNotFoundException("Vehicle not found or has not been placed before VCC for application code: " + applicationCode);
	                });
	        log.debug("Found vehicle: {}", vehicle.getApplicationCode());

	        // Fetch VcctcTemp record
	        // Assuming VcctcTemp's ID is also applicationCode and it's linked to VehicleFinal
	        VcctcTemp vcctcTemp = vcctcTempRepo.findByApplicationCode(applicationCode) // Or findById if VcctcTemp's PK is just applicationCode
	                .orElseThrow(() -> {
	                    log.warn("VcctcTemp record not found for appCode: {}", applicationCode);
	                    return new EntityNotFoundException("VCCTC temp record not found for application: " + applicationCode);
	                });
	        log.debug("Found VcctcTemp linked to VehicleFinal: {}", vcctcTemp.getVehicleFinal().getApplicationCode());

	        // DTO's priceApproved is String "Y" or "N"
	        if ("Y".equals(dto.getPriceApproved())) {
	            log.info("Price IS APPROVED for applicationCode: {}", applicationCode);
	            Processes priceFixProcess = processRepo.findById(PROCESS_CODE_PRICE_FIXED_BY_TC)
	                    .orElseThrow(() -> new EntityNotFoundException("Process code " + PROCESS_CODE_PRICE_FIXED_BY_TC + " not found."));

	            vehicle.setProcesscode(priceFixProcess);
	            // Optionally set the fixed price on VehicleFinal itself if it has such a field
	            // if (dto.getPrice() != null) {
	            //     vehicle.setTcApprovedPrice(dto.getPrice().intValue()); // Example
	            // }
	            VehicleFinal updatedVehicle = vehicleFinalRepo.save(vehicle); // Save VehicleFinal first
	            log.debug("VehicleFinal {} saved with new process code {}.", updatedVehicle.getApplicationCode(), priceFixProcess.getProcesscode());

	            // Create and save Vcctc entry using the updatedVehicle
	            Vcctc vcctcEntry = mapVcctcTempToVcctc(
	                    vcctcTemp,
	                    updatedVehicle,         // Pass the managed, updated VehicleFinal
	                    dto.getPriceApproved(), // Pass "Y"
	                    dto.getPrice()          // Pass the Double price
	            );
	            log.debug("Mapped Vcctc entry: {}", vcctcEntry); // Log to see its state before save

	            if (vcctcEntry.getVehicleFinal() == null) {
	                 log.error("CRITICAL ERROR: vcctcEntry.getVehicleFinal() is NULL before saving Vcctc. This should not happen with the corrected mapper.");
	                 throw new IllegalStateException("Vcctc entry is not correctly associated with VehicleFinal before save.");
	            }

	            vcctcRepo.save(vcctcEntry); // This should now work
	            log.info("Vcctc record saved for applicationCode: {}", applicationCode);

	            vcctcTempRepo.delete(vcctcTemp);
	            log.info("VcctcTemp record deleted for applicationCode: {}", applicationCode);

	            return updatedVehicle;

	        } else { // Price is NOT approved ("N")
	            log.info("Price IS NOT APPROVED for applicationCode: {}", applicationCode);
	            // Revert vehicle to a state for re-verification or further action
	            Processes processBackToReverification = processRepo.findById(PROCESS_CODE_APPROVED_BY_TRANSPORT) // Example: back to 'Approved by Transport'
	                    .orElseThrow(() -> new EntityNotFoundException("Process code " + PROCESS_CODE_APPROVED_BY_TRANSPORT + " not found."));

	            vehicle.setProcesscode(processBackToReverification);
	            VehicleFinal revertedVehicle = vehicleFinalRepo.save(vehicle); // Save the reverted VehicleFinal
	            log.debug("VehicleFinal {} saved with reverted process code {}.", revertedVehicle.getApplicationCode(), processBackToReverification.getProcesscode());

	            // Update Verification remarks
	            // Assuming Verification's ID is also applicationCode and it's linked to VehicleFinal
	            Verification verification = verificationRepo.findByApplicationCode(applicationCode) // Or findById
	                    .orElseThrow(() -> new EntityNotFoundException("Verification record not found for application: " + applicationCode));

	            verification.setRemarks("No price was fixed in last VCC/TC on " +
	                    (vcctcTemp.getEntryDate() != null ? vcctcTemp.getEntryDate().toString() : "N/A") +
	                    ". Returned for re-assessment.");
	            verification.setEntryDate(LocalDate.now()); // Update verification entry date
	            // verification.setVehicleFinal(revertedVehicle); // Ensure Verification is linked to the latest state of VehicleFinal
	            verificationRepo.save(verification);
	            log.info("Verification record updated with remarks for applicationCode: {}", applicationCode);

	            vcctcTempRepo.delete(vcctcTemp);
	            log.info("VcctcTemp record deleted for applicationCode: {}", applicationCode);

	            return revertedVehicle;
	        }

	    } catch (EntityNotFoundException enfe) {
	        log.warn("EntityNotFoundException in priceFixationByTc for DTO {}: {}", dto, enfe.getMessage());
	        throw enfe; // Rethrow for controller to handle as 404
	    } catch (IllegalArgumentException | IllegalStateException bizEx) {
	        log.warn("Business logic exception in priceFixationByTc for DTO {}: {}", dto, bizEx.getMessage());
	        throw bizEx; // Rethrow for controller to handle as 400
	    } catch (Exception e) {
	        log.error("Unexpected error in priceFixationByTc for DTO {}: {}", dto, e.getMessage(), e);
	        throw new RuntimeException("An unexpected error occurred during price fixation for " + dto.getApplicationCode(), e); // Wrap with more context
	    }
	}

	
	//To be allotted
		//processcode = 5.
	@Transactional(readOnly = true)
	public Page<VehicleDraftListDTO> getPricedVehicles(
			String searchTerm,
			Integer year,
			Integer month,
			Pageable pageable,
			User user
			)
	{
		Specification<VehicleFinal> finalSpec = Specification
				.where(VehicleSpecifications.hasProcessCode(PROCESS_CODE_PRICE_FIXED_BY_TC))
				.and(VehicleSpecifications.hasSearchTerm(searchTerm, searchableFields))
				.and(VehicleSpecifications.hasVcctcDateIn(year, month));
		
		if (!userHasAnyRole(user, "ADMIN", "TD")) {
		    Integer deptCode = user.getDepartment() != null ? user.getDepartment().getDepartmentCode() : null;
		    if (deptCode == null) {
		        throw new AccessDeniedException("User does not have a department assigned.");
		    }
		    finalSpec = finalSpec.and(VehicleSpecifications.isInDepartment(deptCode));
		}
		
		return findAndMapVehicles(finalSpec, pageable);	
	}
	
	@Auditable
	@Transactional
	public VehicleFinal allotVehicles(
			AllottVehiclesDTO dto)
	{
		log.info("Attempting to allot vehicle. DTO: {}", dto);
		
		try {
			
			String applicationCode = dto.getApplicationCode();
			
			VehicleFinal vehicle = vehicleFinalRepo.findByApplicationCodeAndProcesscodeProcesscode(applicationCode, PROCESS_CODE_PRICE_FIXED_BY_TC)
					.orElseThrow(() -> new EntityNotFoundException("Vehicle Not Found with applicationcode " + applicationCode + " or have not been Priced by TC"));
			
			//CASE I : Vehicle is Allotted
			if(StringUtils.hasText(dto.getAllotteesName()))
			{
				 log.info("Allottees name present, proceeding with allotment for applicationCode: {}", applicationCode);
				 log.debug("Fetching 'allotment' process code with ID: {}", PROCESS_CODE_ALLOTMENT_OF_VEHICLE);
				 Processes allotVehicle = processRepo.findById(PROCESS_CODE_ALLOTMENT_OF_VEHICLE)
						.orElseThrow(() -> new EntityNotFoundException("Process Code " + PROCESS_CODE_ALLOTMENT_OF_VEHICLE + " not found"));
				   log.error("FATAL: Process Code for 'allotment' (ID: {}) not found in database.", PROCESS_CODE_ALLOTMENT_OF_VEHICLE);
				 vehicle.setProcesscode(allotVehicle);
				VehicleFinal updatedVehicle = vehicleFinalRepo.save(vehicle);
				
				AllottedVehicle allotttedRecord = mapDtoToAllottedVehicle(dto, updatedVehicle);
				allottedVehicleRepo.save(allotttedRecord); 
				
				return updatedVehicle;
			}
			else
			{
				Processes sentForTender = processRepo.findById(PROCESS_CODE_FOR_TENDER)
						.orElseThrow(() -> new EntityNotFoundException("Process Code " + PROCESS_CODE_FOR_TENDER + " not found"));
				
				vehicle.setProcesscode(sentForTender);
				
				VehicleFinal updatedVehicle = vehicleFinalRepo.save(vehicle);
				
				return updatedVehicle;
			} 
		}
		catch(Exception e)
		{
			throw e;
		}
	}
	
	// For Tender
		//display Heavy
		//process code = 5.
	@Transactional
	public Page<VehicleDraftListDTO> getHeavyVehiclesForTender(
			String searchTerm,
			Pageable pageable
			)
	{
		Specification<VehicleFinal> finalSpec = Specification
				.where(VehicleSpecifications.hasProcessCode(PROCESS_CODE_PRICE_FIXED_BY_TC))
				.and(VehicleSpecifications.hasVehicleTypeIn(Heavy_Vehicle_Type_Codes))
				.and(VehicleSpecifications.hasSearchTerm(searchTerm, searchableFields));
		
		return findAndMapVehicles(finalSpec, pageable);	
	}
	
	//Scrap
	@Auditable
	@Transactional
	public VehicleFinal declareVehicleScrapByTc(
    		//String applicationCode,
    		ScrapDTO dto)
    { 
    	try {
    		String applicationCode = dto.getApplicationCode();
    		
    		VehicleFinal vehicle = vehicleFinalRepo.findByApplicationCodeAndProcesscodeProcesscode(applicationCode, PROCESS_CODE_PRICE_FIXED_BY_TC)
    				.orElseThrow(() -> new EntityNotFoundException("Vehicle Not Found with applicationcode " + applicationCode + " or have not been Priced by TC"));
    		
    		
    		Processes scrapProcess = processRepo.findById(PROCESS_CODE_FOR_SCRAP_TC)
    				.orElseThrow(() -> new EntityNotFoundException("Process code " + PROCESS_CODE_FOR_SCRAP_TC + " or have not been Priced by TC"));
    		
    		vehicle.setProcesscode(scrapProcess);
    		
    		String vehicleFinalRemarks = "Declare as Scrap by TC. ";
    		
    		if(StringUtils.hasText(dto.getRemarks()))
    		{
    			vehicleFinalRemarks += " " + dto.getRemarks();
    		}
    		
    		vehicle.setRemarks(vehicleFinalRemarks);

    		VehicleFinal updatedVehicle = vehicleFinalRepo.save(vehicle);
    		
    		Scrap scrapEntry = mapToScrapEntry(dto, updatedVehicle, "Declared as Scrap by TC.");
    		scrapRepo.save(scrapEntry);
    		
    		return updatedVehicle;
    	}
    	catch(Exception e)
    	{
    		throw e;
    	}
    }
    
	//Tender
	@Auditable
	@Transactional
    public VehicleFinal declareVehicleTenderByTc(TenderVehiclesDTO dto )
    {
    	try {
    		String applicationCode = dto.getApplicationCode();
    		
    		VehicleFinal vehicle = vehicleFinalRepo.findByApplicationCodeAndProcesscodeProcesscode(applicationCode, PROCESS_CODE_PRICE_FIXED_BY_TC)
    				.orElseThrow(() -> new EntityNotFoundException("Vehicle Not Found with applicationcode " + applicationCode + " or have not been Priced by TC"));
    		
    		
    		Processes tenderProcess = processRepo.findById(PROCESS_CODE_FOR_TENDER)
    				.orElseThrow(() -> new EntityNotFoundException("Process code " + PROCESS_CODE_FOR_TENDER + " or have not been Priced by TC"));
    		
    		vehicle.setProcesscode(tenderProcess);
    		
    		VehicleFinal updatedVehicle = vehicleFinalRepo.save(vehicle);
    		
    		TenderVehicles tenderEntry= mapToTenderEntry(dto, updatedVehicle);
    		tenderRepo.save(tenderEntry);
    		
    		return updatedVehicle;
    	}
    	catch(Exception e)
    	{
    		throw e;
    	}
    }
    
    //Lifting Process - This is used in the dealing assistant page (TO BE LIFTED)
	@Transactional(readOnly = true)
    public Page<VehicleDraftListDTO> getAllottedVehicles(
			String searchTerm,
			Integer year,
			Integer month,
			Pageable pageable,
			User user
			)
	{
		Specification<VehicleFinal> finalSpec = Specification
				.where(VehicleSpecifications.hasProcessCode(PROCESS_CODE_ALLOTMENT_OF_VEHICLE))
				.and(VehicleSpecifications.hasSearchTerm(searchTerm, searchableFields))
				.and(VehicleSpecifications.hasAllottedVehiclesDateIn(year, month));
		
		if (!userHasAnyRole(user, "ADMIN", "TD")) {
		    Integer deptCode = user.getDepartment() != null ? user.getDepartment().getDepartmentCode() : null;
		    if (deptCode == null) {
		        throw new AccessDeniedException("User does not have a department assigned.");
		    }
		    finalSpec = finalSpec.and(VehicleSpecifications.isInDepartment(deptCode));
		}
		
		return findAndMapVehicles(finalSpec, pageable);	
	}
    
	@Auditable
	@Transactional
    public VehicleFinal handleVehicleLifting(LiftingDTO liftingDto )
    {
    	try
    	{
    		String applicationCode = liftingDto.getApplicationCode();
    		
    		VehicleFinal vehicle = vehicleFinalRepo.findByApplicationCodeAndProcesscodeProcesscode(applicationCode, PROCESS_CODE_ALLOTMENT_OF_VEHICLE)
    				.orElseThrow(() -> new EntityNotFoundException("Vehicle with application code " + applicationCode + " not found or not allotted."));
    	
    		if("Y".equalsIgnoreCase(liftingDto.getLiftMode()))
    		{
    			
    			AllottedVehicle allottedVehicle = allottedVehicleRepo.findById(applicationCode)
    					.orElseThrow(() -> new EntityNotFoundException("AllottedVehicle record not found for application code: " + applicationCode));
    			
    			vehicle.setAllottedVehicle(null);
    			
    			LiftedVehicles liftedEntry = mapToLiftedEntry(liftingDto, vehicle);
    				
    			liftedVehicleRepo.save(liftedEntry);
    			
    			allottedVehicleRepo.delete(allottedVehicle);
    			
    			Processes liftedProcess = processRepo.findById(PROCESS_CODE_LIFTED_VEHICLE)
    					.orElseThrow(() -> new EntityNotFoundException("Process code " + PROCESS_CODE_LIFTED_VEHICLE + " not found"));
    			
    			vehicle.setProcesscode(liftedProcess);
    		}
    		else if("N".equalsIgnoreCase(liftingDto.getLiftMode()))
    		{
    			vcctcRepo.findById(applicationCode).ifPresent(vcctcRecord -> {
    				vcctcRepo.delete(vcctcRecord);
    			});
    			
    			AllottedVehicle allottedVehicle = allottedVehicleRepo.findById(applicationCode)
    					.orElseThrow(() -> new EntityNotFoundException("AllottedVehicle record not found for application code: " + applicationCode));
    			
    			vehicle.setAllottedVehicle(null);
    			allottedVehicleRepo.delete(allottedVehicle);
    			
    			Processes notLiftedProcess = processRepo.findById(PROCESS_CODE_NON_LIFTED_VEHICLE)
    					.orElseThrow(() -> new EntityNotFoundException("Process code " + PROCESS_CODE_NON_LIFTED_VEHICLE + " not found."));
    			
    			vehicle.setProcesscode(notLiftedProcess);
    			
//    			vehicle.setRemarks("Vehicle marked as not lifted. Awaiting Transport decision for Scrap/Tender.");
    		}
    		else
    		{
    			log.warn("Invalid liftMode '{}' received for application code: {}", liftingDto.getLiftMode(), liftingDto.getApplicationCode());
                throw new IllegalArgumentException("Invalid liftMode provided. Must be 'Y' or 'N'.");
    		}
    		
    		VehicleFinal updatedVehicle = vehicleFinalRepo.save(vehicle);
    		
    		return updatedVehicle;
    	}
    	catch(Exception e)
    	{
    		throw e;
    	}
    }
		
	@Transactional(readOnly = true)
    public Page<VehicleDraftListDTO> getTenderedVehicles(
			String searchTerm,
			Integer year,
			Integer month,
			Pageable pageable,
			User user
			)
	{
		Specification<VehicleFinal> finalSpec = Specification
				.where(VehicleSpecifications.hasProcessCode(PROCESS_CODE_FOR_TENDER))
				.and(VehicleSpecifications.hasSearchTerm(searchTerm, searchableFields))
			.and(VehicleSpecifications.hasTenderedDateIn(year, month));
		
		if (!userHasAnyRole(user, "ADMIN", "TD")) {
		    Integer deptCode = user.getDepartment() != null ? user.getDepartment().getDepartmentCode() : null;
		    if (deptCode == null) {
		        throw new AccessDeniedException("User does not have a department assigned.");
		    }
		    finalSpec = finalSpec.and(VehicleSpecifications.isInDepartment(deptCode));
		}
		
		return findAndMapVehicles(finalSpec, pageable);	
	}
	
	@Transactional(readOnly = true)
    public Page<VehicleDraftListDTO> getScrappedVehicles(
			String searchTerm,
			Integer year,
			Integer month,
			Pageable pageable,
			User user
			)
	{
		Specification<VehicleFinal> finalSpec = Specification
				.where(VehicleSpecifications.hasProcessCode(PROCESS_CODE_FOR_SCRAP_TC))
				.and(VehicleSpecifications.hasSearchTerm(searchTerm, searchableFields))
				.and(VehicleSpecifications.hasScrapperDateIn(year, month));
		
		if (!userHasAnyRole(user, "ADMIN", "TD")) {
		    Integer deptCode = user.getDepartment() != null ? user.getDepartment().getDepartmentCode() : null;
		    if (deptCode == null) {
		        throw new AccessDeniedException("User does not have a department assigned.");
		    }
		    finalSpec = finalSpec.and(VehicleSpecifications.isInDepartment(deptCode));
		}
		
		return findAndMapVehicles(finalSpec, pageable);	
	}
	
	@Transactional(readOnly = true)
    public Page<VehicleDraftListDTO> getLiftedVehicles(
			String searchTerm,
			Integer year,
			Integer month,
			Pageable pageable,
			User user
			)
	{
		Specification<VehicleFinal> finalSpec = Specification
				.where(VehicleSpecifications.hasProcessCode(PROCESS_CODE_LIFTED_VEHICLE))
				.and(VehicleSpecifications.hasSearchTerm(searchTerm, searchableFields))
				.and(VehicleSpecifications.hasLiftedVehiclesDateIn(year, month));
		
		
		if (!userHasAnyRole(user, "ADMIN", "TD")) {
		    Integer deptCode = user.getDepartment() != null ? user.getDepartment().getDepartmentCode() : null;
		    if (deptCode == null) {
		        throw new AccessDeniedException("User does not have a department assigned.");
		    }
		    finalSpec = finalSpec.and(VehicleSpecifications.isInDepartment(deptCode));
		}
		
		return findAndMapVehicles(finalSpec, pageable);	
	}
	
	//List Non - Lifted Vehicle
	@Transactional(readOnly = true)
	public Page<VehicleDraftListDTO> getNonLiftedVehicles(
			String searchTerm,
			Pageable pageable
			)
	{
		Specification<VehicleFinal> finalSpec = Specification
				.where(VehicleSpecifications.hasProcessCode(PROCESS_CODE_NON_LIFTED_VEHICLE))
				.and(VehicleSpecifications.hasSearchTerm(searchTerm, searchableFields));
		
		return findAndMapVehicles(finalSpec, pageable);
	}
	
	@Transactional(readOnly = true)
	public List<Map<String, Object>> getAvailableDatesByProcessCode(Integer processCode)
	{
		List<YearMonthDTO> flatDates = verificationRepo.findDistinctYearAndMonthByProcessCode(processCode);
		
		log.info("Raw dates from repository before grouping: {}", flatDates);
		
		Map<Integer, List<Integer>> groupedByYear = flatDates.stream().collect(Collectors.groupingBy(YearMonthDTO::getYear, 
				Collectors.mapping(YearMonthDTO::getMonth, Collectors.toList())));
		
		List<Map<String, Object>> finalResult = new ArrayList<>();
		
		groupedByYear.forEach((year, months) -> {
			months.sort(Collections.reverseOrder());
			
			finalResult.add(Map.of("year", year, "months",months));

		});
		
		 finalResult.sort((a, b) -> Integer.compare((Integer) b.get("year"), (Integer) a.get("year")));

		 return finalResult;
	}
	
	@Transactional(readOnly = true)
	public List<Map<String, Object>> getAllottedVehicleAvailableDates(Integer processCode)
	{
		List<YearMonthDTO> flatDates = allottedVehicleRepo.findAllottedDistinctYearAndMonthByProcessCode(processCode);
		
		log.info("Raw dates from repository before grouping: {}", flatDates);
		
		Map<Integer, List<Integer>> groupedByYear = flatDates.stream().collect(Collectors.groupingBy(YearMonthDTO::getYear, 
				Collectors.mapping(YearMonthDTO::getMonth, Collectors.toList())));
		
		List<Map<String, Object>> finalResult = new ArrayList<>();
		
		groupedByYear.forEach((year, months) -> {
			months.sort(Collections.reverseOrder());
			
			finalResult.add(Map.of("year", year, "months",months));

		});
		
		 finalResult.sort((a, b) -> Integer.compare((Integer) b.get("year"), (Integer) a.get("year")));

		 return finalResult;
	}
	
	@Transactional(readOnly = true)
	public List<Map<String, Object>> getLiftedVehicleAvailableDates(Integer processCode)
	{
		List<YearMonthDTO> flatDates = liftedVehicleRepo.findLiftedDistinctYearAndMonthByProcessCode(processCode);
		
		log.info("Raw dates from repository before grouping: {}", flatDates);
		
		Map<Integer, List<Integer>> groupedByYear = flatDates.stream().collect(Collectors.groupingBy(YearMonthDTO::getYear, 
				Collectors.mapping(YearMonthDTO::getMonth, Collectors.toList())));
		
		List<Map<String, Object>> finalResult = new ArrayList<>();
		
		groupedByYear.forEach((year, months) -> {
			months.sort(Collections.reverseOrder());
			
			finalResult.add(Map.of("year", year, "months",months));

		});
		
		 finalResult.sort((a, b) -> Integer.compare((Integer) b.get("year"), (Integer) a.get("year")));

		 return finalResult;
	}
	
	@Transactional(readOnly = true)
	public List<Map<String, Object>> getAvailableVcctcTempDatesByProcessCode(Integer processCode)
	{
	
	    List<YearMonthProjection> flatDates = vcctcTempRepo.findVcctcTempYearAndMonthFromLetterNoDate(processCode);

	    Map<Integer, List<Integer>> groupedByYear = flatDates.stream().collect(Collectors.groupingBy(YearMonthProjection::getYear,
	            Collectors.mapping(YearMonthProjection::getMonth, Collectors.toList())));

	    List<Map<String, Object>> finalResult = new ArrayList<>();

	    groupedByYear.forEach((year, months) -> {
	        months.sort(Collections.reverseOrder());
	        finalResult.add(Map.of("year", year, "months", months));
	    });

	    finalResult.sort((a, b) -> Integer.compare((Integer) b.get("year"), (Integer) a.get("year")));

	    return finalResult;
	}

	@Transactional(readOnly = true)
	public List<Map<String, Object>> getAvailableVcctcDatesByProcessCode(Integer processCode)
	{
	
	    List<YearMonthProjection> flatDates = vcctcRepo.findVcctcYearAndMonthFromLetterNoDate(processCode);

	    Map<Integer, List<Integer>> groupedByYear = flatDates.stream().collect(Collectors.groupingBy(YearMonthProjection::getYear,
	            Collectors.mapping(YearMonthProjection::getMonth, Collectors.toList())));

	    List<Map<String, Object>> finalResult = new ArrayList<>();

	    groupedByYear.forEach((year, months) -> {
	        months.sort(Collections.reverseOrder());
	        finalResult.add(Map.of("year", year, "months", months));
	    });

	    finalResult.sort((a, b) -> Integer.compare((Integer) b.get("year"), (Integer) a.get("year")));

	    return finalResult;
	}
	
	@Transactional(readOnly = true)
	public List<Map<String, Object>> getAvailableScrappedDatesByProcessCode(Integer processCode)
	{
	
	    List<YearMonthProjection> flatDates = scrapRepo.findScrappedYearAndMonthFromLetterNoDate(processCode);

	    Map<Integer, List<Integer>> groupedByYear = flatDates.stream().collect(Collectors.groupingBy(YearMonthProjection::getYear,
	            Collectors.mapping(YearMonthProjection::getMonth, Collectors.toList())));

	    List<Map<String, Object>> finalResult = new ArrayList<>();

	    groupedByYear.forEach((year, months) -> {
	        months.sort(Collections.reverseOrder());
	        finalResult.add(Map.of("year", year, "months", months));
	    });

	    finalResult.sort((a, b) -> Integer.compare((Integer) b.get("year"), (Integer) a.get("year")));

	    return finalResult;
	}
	
	@Transactional(readOnly = true)
	public List<Map<String, Object>> getAvailableTenderedDatesByProcessCode(Integer processCode)
	{
	
	    List<YearMonthProjection> flatDates = tenderRepo.findTenderdYearAndMonthFromLetterNoDate(processCode);

	    Map<Integer, List<Integer>> groupedByYear = flatDates.stream().collect(Collectors.groupingBy(YearMonthProjection::getYear,
	            Collectors.mapping(YearMonthProjection::getMonth, Collectors.toList())));

	    List<Map<String, Object>> finalResult = new ArrayList<>();

	    groupedByYear.forEach((year, months) -> {
	        months.sort(Collections.reverseOrder());
	        finalResult.add(Map.of("year", year, "months", months));
	    });

	    finalResult.sort((a, b) -> Integer.compare((Integer) b.get("year"), (Integer) a.get("year")));

	    return finalResult;
	}
	
    private boolean userHasAnyRole(User user, String... roles) {
        Set<String> roleSet = Arrays.stream(roles)
                .map(String::toUpperCase)
                .collect(Collectors.toSet());

        return user.getAuthorities().stream()
                .map(granted -> granted.getAuthority().toUpperCase())
                .anyMatch(roleSet::contains);
    }
    // --- Private Helper Method for Query Execution and Mapping (for Pending Lists) ---

    /**
     * Executes the JPA query using the combined Specification and maps the result Page to DTOs.
     */
    private Page<VehicleDraftListDTO> findAndMapVehicles(Specification<VehicleFinal> spec, Pageable pageable) {
        Page<VehicleFinal> finalPage = vehicleFinalRepo.findAll(spec, pageable);
        log.debug("Found {} vehicles matching criteria for pending list.", finalPage.getTotalElements());
        
        if(finalPage.isEmpty())
        {
        	return Page.empty(pageable);
        }
        
        List<String> applicationCodes = finalPage.getContent().stream()
                .map(VehicleFinal::getApplicationCode)
                .collect(Collectors.toList());

		Map<String, Vcctc> vcctcMap = vcctcRepo.findAllById(applicationCodes).stream()
		            .collect(Collectors.toMap(Vcctc::getApplicationCode, v -> v)); 
		Map<String, AllottedVehicle> allottedVehicleMap = allottedVehicleRepo.findAllById(applicationCodes).stream() 
		            .collect(Collectors.toMap(av -> av.getVehicleFinal().getApplicationCode(), av -> av));
		Map<String, LiftedVehicles> liftedVehiclesMap = liftedVehicleRepo.findAllById(applicationCodes).stream()
		            .collect(Collectors.toMap(lv -> lv.getVehicleFinal().getApplicationCode(), lv -> lv));
		Map<String, Scrap> scrapMap = scrapRepo.findAllById(applicationCodes).stream()
		            .collect(Collectors.toMap(s -> s.getVehicleFinal().getApplicationCode(), s -> s));
		
		
		return finalPage.map(vehicleFinal -> mapToVehicleDraftListDTO(
				vehicleFinal,
				vcctcMap.get(vehicleFinal.getApplicationCode()),
				allottedVehicleMap.get(vehicleFinal.getApplicationCode()),
				liftedVehiclesMap.get(vehicleFinal.getApplicationCode()),
				scrapMap.get(vehicleFinal.getApplicationCode())
		));
    }

    private VehicleDraftListDTO mapToVehicleDraftListDTO(
    		VehicleFinal finalList,
    		Vcctc vcctc,
    		AllottedVehicle allottedVehicle,
            LiftedVehicles liftedVehicle, 
            Scrap scrap
        ) {
        VehicleDraftListDTO dto = new VehicleDraftListDTO();
        
        dto.setApplicationCode(finalList.getApplicationCode());
        dto.setRegistrationNo(finalList.getRegistrationNo());
        dto.setVehicleDescription(finalList.getVehicledescription());
        dto.setPurchaseDate(finalList.getPurchasedate());
        dto.setDepreciatedValue(finalList.getDepreciatedamount());
        dto.setTotalKmsLogged(finalList.getTotalkms());
        
        dto.setProcesscode(finalList.getProcesscode().getProcesscode());
       
        dto.setMviReportsAvailable(Mvi_Available_Status.equalsIgnoreCase(finalList.getMvireportavailable()));
    
        dto.setAnyCasePending(Mvi_Available_Status.equalsIgnoreCase(finalList.getWhetheraccident()));
        
        if(finalList.getDepartmentCode() != null)
        {
        	dto.setDepartmentName(finalList.getDepartmentCode().getDepartmentName());
        }
        if (finalList.getDepartmentCode() != null) {
            dto.setDepartmentName(finalList.getDepartmentCode().getDepartmentName());
        }

        // Vcctc Table
        if (vcctc != null) {
            dto.setTcPrice(vcctc.getVehiclePrice());
        }

        // AllotVehicle Table
        if (allottedVehicle != null) {
            dto.setAllotteddate(allottedVehicle.getAllottedDate());
            dto.setAllotteesname(allottedVehicle.getAllotteesName());
            dto.setAllotteesaddress(allottedVehicle.getAllotteesAddress());

            String combinedInfo = allottedVehicle.getLetternodate();
            
            // Correctly check if the string is valid before trying to split it
            if (combinedInfo != null && !combinedInfo.trim().isEmpty() && combinedInfo.contains("|")) {
                // Split on the pipe character. Remember to escape it for regex.
                String[] parts = combinedInfo.split("\\|");

                // Safely set the letter number part
                if (parts.length > 0) {
                    dto.setAllotmentLetterNo(parts[0].trim());
                }

                // Safely parse and set the date part
                if (parts.length > 1) {
                    String dateString = parts[1].trim();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    try {
                        LocalDate localDate = LocalDate.parse(dateString, formatter);
                        // Convert to java.util.Date for the DTO
                        dto.setAllotmentLetterDate(localDate);
                    } catch (DateTimeParseException e) {
                        log.error("Could not parse allotment date '{}'", dateString);
                        // Don't set the date if it's invalid
                    }
                }
            }
        }

        // Lifter Table (LiftedVehicles)
        if (liftedVehicle != null) {
            dto.setLiftersname(liftedVehicle.getLiftersName());
            dto.setLiftersaddress(liftedVehicle.getLiftersAddress());
            dto.setLifteddate(liftedVehicle.getLifteddate());
        }

        // Scrap Table
        if (scrap != null) {
            dto.setScrapAmount(scrap.getAmount());
        }
        return dto;
    }

    private VehicleDraft mapVehicleFinalToDraft(VehicleFinal vehFinal, Processes returnedProcess, String returnRemarks) {
        VehicleDraft vehDraft = new VehicleDraft();
        
        vehDraft.setApplicationCode(vehFinal.getApplicationCode());
        vehDraft.setApplicationMode(vehFinal.getApplicationMode());
        vehDraft.setApplicationSlno(vehFinal.getApplicationSlno());
        vehDraft.setDepartmentCode(vehFinal.getDepartmentCode());
        vehDraft.setOfficeName(vehFinal.getOfficeName());
        vehDraft.setOfficerDesignation(vehFinal.getOfficerDesignation());
        vehDraft.setPremises(vehFinal.getPremises());
        vehDraft.setLocations(vehFinal.getLocations());
        vehDraft.setDirectorateLetterNodate(vehFinal.getDirectorateLetterNodate());
        vehDraft.setGovtLetterNoDate(vehFinal.getGovtLetterNoDate());
        vehDraft.setVehicletypecode(vehFinal.getVehicletypecode());
        vehDraft.setVehicledescription(vehFinal.getVehicledescription());
        vehDraft.setVehiclemanufacturercode(vehFinal.getVehiclemanufacturercode());
        vehDraft.setRegisteredDistrict(vehFinal.getRegisteredDistrict());
        vehDraft.setRegistrationNo(vehFinal.getRegistrationNo());
        vehDraft.setEngineno(vehFinal.getEngineno());
        vehDraft.setChassisno(vehFinal.getChassisno());
        vehDraft.setManufactureyear(vehFinal.getManufactureyear());
        vehDraft.setPurchasedate(vehFinal.getPurchasedate());
        vehDraft.setVehicleprice(vehFinal.getVehicleprice());
        vehDraft.setTotalkms(vehFinal.getTotalkms());
        vehDraft.setDepreciatedamount(vehFinal.getDepreciatedamount());
        vehDraft.setImprovements(vehFinal.getImprovements());
        vehDraft.setExpenses(vehFinal.getExpenses());
        vehDraft.setRepairexpenses(vehFinal.getRepairexpenses());
        vehDraft.setRepairslastsixmonths(vehFinal.getRepairslastsixmonths());
        vehDraft.setWhetheraccident(vehFinal.getWhetheraccident());
        vehDraft.setAccidentcaseresolved(vehFinal.getAccidentcaseresolved());
        vehDraft.setComments(vehFinal.getComments());
        vehDraft.setMvireportavailable(vehFinal.getMvireportavailable());
        vehDraft.setBattery(vehFinal.getBattery());
        vehDraft.setTyres(vehFinal.getTyres());
        vehDraft.setAccidentdamage(vehFinal.getAccidentdamage());
        vehDraft.setMviprice(vehFinal.getMviprice());
        vehDraft.setMviremarks(vehFinal.getMviremarks());
        vehDraft.setEntrydate(vehFinal.getEntrydate()); 
        vehDraft.setFinancialYearCode(vehFinal.getFinancialYearCode());
        vehDraft.setVersionflagcode(vehFinal.getVersionflagcode());
        
        vehDraft.setProcesscode(returnedProcess);
        vehDraft.setRemarks(returnRemarks);
        return vehDraft;
    }

    private VehiclePartsConditionDraft mapPartFinalToDraft(VehiclePartsConditionFinal finalPart, VehicleDraft savedDraft) {
        VehiclePartsConditionDraft draftPart = new VehiclePartsConditionDraft();
        
        draftPart.setApplicationCode(savedDraft);
        
        draftPart.setCondition(finalPart.getCondition());
        draftPart.setSlno(finalPart.getSlno());
        draftPart.setVehiclepartcode(finalPart.getVehiclepartcode());
        return draftPart;
    }
    
    private Vcctc mapVcctcTempToVcctc(
    		VcctcTemp temp,
    		VehicleFinal associatedVehicleFinal,
    		String priceApprovedStatusFromDto,
    		Integer priceFromDto
    		) {
        Vcctc vcc = new Vcctc();
        
        vcc.setVehicleFinal(associatedVehicleFinal);
        
        vcc.setLetterNoDate(temp.getLetterNoDate());
        
        vcc.setEntryDate(temp.getEntryDate());
        
        vcc.setVcctc(temp.getVcctc());
        
        vcc.setPriceApproved(priceApprovedStatusFromDto);
        
        if(priceFromDto != null && "Y".equals(priceApprovedStatusFromDto))
        {
        	vcc.setVehiclePrice(priceFromDto);
        }
        else {
        	vcc.setVehiclePrice(null);
        }
        
        return vcc;
    }
    
    private AllottedVehicle mapDtoToAllottedVehicle(AllottVehiclesDTO dto, VehicleFinal vehicleFinal) {
        AllottedVehicle allottedVehicle = new AllottedVehicle();
 
        allottedVehicle.setVehicleFinal(vehicleFinal);

        allottedVehicle.setLetternodate(dto.getLetterNo() + "|" + dto.getLetterDate());
        
        allottedVehicle.setAllottedDate(dto.getAllotmentDate()); // Matches entity field name 'allottedDate'
        allottedVehicle.setAllotteesName(dto.getAllotteesName());
        allottedVehicle.setAllotteesAddress(dto.getAllotteesAddress());
        allottedVehicle.setEntryDate(LocalDate.now());

        return allottedVehicle;
    }

    private Scrap mapToScrapEntry(ScrapDTO dto, VehicleFinal vehicleFinal, String remarksPrefix)
    {
    	Scrap scrapEntry = new Scrap();
    	
    	scrapEntry.setVehicleFinal(vehicleFinal);
    	
//    	StringBuilder letterNoDateBuilder = new StringBuilder();
//    	
//    	if(StringUtils.hasText(dto.getLetterNo()))
//    	{
//    		letterNoDateBuilder.append(dto.getLetterNo());
//    	}
//    	
//    	if(dto.getLetterDate() != null)
//    	{
//    		if(letterNoDateBuilder.length() > 0)
//    		{
//    			letterNoDateBuilder.append(" Dt.");
//    		}
//    		
//    		letterNoDateBuilder.append(dto.getLetterDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
//    	}
//    	
//    	scrapEntry.setLetterNoDate(letterNoDateBuilder.toString());
    	
    	scrapEntry.setLetterNoDate(dto.getLetterNo() + "|" + dto.getLetterDate());
    	
    	scrapEntry.setAmount(dto.getPrice());
    	
    	scrapEntry.setEntryDate(LocalDate.now());
    	
    	String finalScrapRemarks = remarksPrefix;
    	
    	if(StringUtils.hasText(dto.getRemarks()))
    	{
    		if(StringUtils.hasText(finalScrapRemarks) && !finalScrapRemarks.endsWith("."))
    		{
    			finalScrapRemarks += ". ";
    		}
    		else if(StringUtils.hasText(finalScrapRemarks))
    		{
    			finalScrapRemarks += " ";
    		}
    		
    		finalScrapRemarks += dto.getRemarks();
    	}
    	
    	scrapEntry.setRemarks(finalScrapRemarks);
    	
    	//TODO Set slno.
    	
    	return scrapEntry;
    }

    private TenderVehicles mapToTenderEntry(TenderVehiclesDTO dto, VehicleFinal vehicleFinal)
    {
    	TenderVehicles tenderEntry = new TenderVehicles();
    	
    	tenderEntry.setVehicleFinal(vehicleFinal);
    	
//    	StringBuilder letterNoDateBuilder = new StringBuilder();
//    	if(StringUtils.hasText(dto.getLetterNo()))
//    	{
//    		letterNoDateBuilder.append(dto.getLetterNo());
//    	}
//    	
//    	if(dto.getLetterDate() != null)
//    	{
//    		if(letterNoDateBuilder.length() > 0)
//    		{
//    			letterNoDateBuilder.append(" Dt."); 
//    		}
//    		
//    		letterNoDateBuilder.append(dto.getLetterDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
//    	}
    	
//    	tenderEntry.setLetternodate(letterNoDateBuilder.toString());
    	
    	tenderEntry.setLetternodate(dto.getLetterNo() + "|" + dto.getLetterDate());
    	
    	tenderEntry.setEntryDate(LocalDate.now());
    	
    	return tenderEntry;
    }
    
    private LiftedVehicles mapToLiftedEntry(LiftingDTO dto, VehicleFinal vehicleFinal)
    {
    	LiftedVehicles liftedEntry = new LiftedVehicles();
    
    	liftedEntry.setVehicleFinal(vehicleFinal);
    	
    	liftedEntry.setLetterNoDate(dto.getForwardingLetterNo() + "|" + dto.getForwardingLetterDate());
    	
    	liftedEntry.setLifteddate(dto.getLiftedDate());
    	
    	liftedEntry.setLiftersName(dto.getLiftersName());
    	
    	liftedEntry.setLiftersAddress(dto.getLiftersAddress());
    	
    	liftedEntry.setLiftedMode(dto.getLiftMode());
    	
    	liftedEntry.setEntryDate(LocalDate.now());
    	
    	return liftedEntry;
    }
    
}