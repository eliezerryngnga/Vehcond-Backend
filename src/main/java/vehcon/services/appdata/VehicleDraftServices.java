package vehcon.services.appdata;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vehcon.dto.appdata.PartsConditionInputDTO;
import vehcon.dto.appdata.VehicleDraftDTO;
import vehcon.models.masters.Departments;
import vehcon.models.masters.Districts;
import vehcon.models.masters.FinancialYear;
import vehcon.models.masters.Processes;
import vehcon.models.masters.VehicleManufacturer;
import vehcon.models.masters.VehicleParts;
import vehcon.models.masters.VehicleType;
import vehcon.models.vehiclecondemnations.VehicleDraft;
import vehcon.models.vehiclecondemnations.VehiclePartsConditionDraft;
import vehcon.repo.appdata.ProcessesRepository;
import vehcon.repo.appdata.VehicleDraftRepository;
import vehcon.repo.appdata.VehiclePartsConditionDraftRepository;
import vehcon.repo.masters.DepartmentsRepository;
import vehcon.repo.masters.DistrictsRepository;
import vehcon.repo.masters.FinancialYearRepo;
import vehcon.repo.masters.VehicleManufacturerRepository;
import vehcon.repo.masters.VehiclePartsRepository;
import vehcon.repo.masters.VehicleTypeRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleDraftServices {

    private final VehicleDraftRepository vehicleDraftRepo;
    private final VehiclePartsConditionDraftRepository vehiclePartsConditionDraftRepo;
    private final VehiclePartsRepository vehiclePartsRepo;
    private final ProcessesRepository processesRepo;
    private final DepartmentsRepository departmentRepo;
    private final FinancialYearRepo financialYearRepo;
    private final DistrictsRepository districtRepo;
    private final VehicleTypeRepository vehicleTypeRepo;
    private final VehicleManufacturerRepository vehicleManufacturerRepo;

    @Transactional
    public String addVehicleDraft(VehicleDraftDTO vehicleDraftDTO) {
        try {
        	
        	VehicleDraft vehicleDraft = new VehicleDraft();
        	
        	Processes initialProcess = processesRepo.findById(1)
                    .orElseThrow(() -> new RuntimeException("Initial Process with code 1 not found."));
            vehicleDraft.setProcesscode(initialProcess);
            
            Integer departmentCodeFromDTO = vehicleDraftDTO.getDepartmentCode();
            if ( departmentCodeFromDTO != null)
            {
                Departments department = departmentRepo.findById(departmentCodeFromDTO)
                		.orElseThrow(() -> new RuntimeException("Department with code " + departmentCodeFromDTO + " not found."));
                vehicleDraft.setDepartmentCode(department);
            }
                       
            Integer financialYearFromDTO = vehicleDraftDTO.getFinancialYearCode();
            if(financialYearFromDTO != null) {
            	FinancialYear financialYear = financialYearRepo.findById(financialYearFromDTO)
                		.orElseThrow(() -> new RuntimeException("Fiancial Year with code " + financialYearFromDTO + " not found"));
            	vehicleDraft.setFinancialYearCode(financialYear);
            }
            
            Integer registeredDistrictFromDTO = vehicleDraftDTO.getRegisteredDistrict();
            if(registeredDistrictFromDTO != null) {
            	Districts registeredDistricts = districtRepo.findById(registeredDistrictFromDTO)
                		.orElseThrow(() -> new RuntimeException("Registered District with code " + registeredDistrictFromDTO + " not found"));
            	vehicleDraft.setRegisteredDistrict(registeredDistricts);
            }
            
            Integer vehicleTypeCodeFromDTO = vehicleDraftDTO.getVehicletypecode();
            if(vehicleTypeCodeFromDTO != null)
            {
            	VehicleType vehicleType = vehicleTypeRepo.findById(vehicleTypeCodeFromDTO)
                    	.orElseThrow(() -> new RuntimeException("Vehicle Type with code " + vehicleTypeCodeFromDTO + " not found"));
            	vehicleDraft.setVehicletypecode(vehicleType);
            }
            
            Integer vehicleManufacturerFromDTO = vehicleDraftDTO.getVehiclemanufacturercode();
            if(vehicleManufacturerFromDTO != null) {
            	VehicleManufacturer vehicleManufacturer = vehicleManufacturerRepo.findById(vehicleManufacturerFromDTO)
                		.orElseThrow(() -> new RuntimeException("Vehicle Manufacturer with code " + vehicleManufacturerFromDTO + " not found"));
            	vehicleDraft.setVehiclemanufacturercode(vehicleManufacturer);
            }
            
            
        	String locations = (vehicleDraftDTO.getAddress1() != null ? vehicleDraftDTO.getAddress1(): "") + ", " + (vehicleDraftDTO.getAddress2() != null ? vehicleDraftDTO.getAddress2() : "");
        	vehicleDraft.setLocations(locations.equals(", ") ? null : locations);
        	
        	// Handle potential null dates before toString()
            String dirDateStr = vehicleDraftDTO.getDirectorateLetterDate() != null ? vehicleDraftDTO.getDirectorateLetterDate().toString() : "";
            String directorateLetterNodate = (vehicleDraftDTO.getDirectorateLetterNo() != null ? vehicleDraftDTO.getDirectorateLetterNo() : "") + " " + dirDateStr;
             vehicleDraft.setDirectorateLetterNodate(directorateLetterNodate.trim().isEmpty() ? null : directorateLetterNodate.trim());

            String govDateStr = vehicleDraftDTO.getGovForwardingLetterDate() != null ? vehicleDraftDTO.getGovForwardingLetterDate().toString() : "";
            String govtLetterNoDate = (vehicleDraftDTO.getForwardingLetterNo() != null ? vehicleDraftDTO.getForwardingLetterNo() : "") + " " + govDateStr;
            vehicleDraft.setGovtLetterNoDate(govtLetterNoDate.trim().isEmpty() ? null : govtLetterNoDate.trim());

            String registrationNo = (vehicleDraftDTO.getRtoNo() != null ? vehicleDraftDTO.getRtoNo() : "") + " " +
                                     (vehicleDraftDTO.getVehicleRegistrationNumber() != null ? vehicleDraftDTO.getVehicleRegistrationNumber() : "");
             vehicleDraft.setRegistrationNo(registrationNo.trim().isEmpty() ? null : registrationNo.trim());
        	
//        	vehicleDraft.setDirectorateLetterNodate(vehicleDraftDTO.getDirectorateLetterNo()+" "+vehicleDraftDTO.getDirectorateLetterDate());
//            vehicleDraft.setGovtLetterNoDate(vehicleDraftDTO.getForwardingLetterNo()+" "+vehicleDraftDTO.getGovForwardingLetterDate());
//            vehicleDraft.setRegistrationNo(vehicleDraftDTO.getRtoNo()+" "+vehicleDraftDTO.getVehicleRegistrationNumber());
        	
            vehicleDraft.setOfficeName(vehicleDraftDTO.getOfficeName());
        	vehicleDraft.setOfficerDesignation(vehicleDraftDTO.getOfficerDesignation());
        	vehicleDraft.setPremises(vehicleDraftDTO.getPremises());
        	vehicleDraft.setVehicledescription(vehicleDraftDTO.getVehicledescription());
        	vehicleDraft.setEngineno(vehicleDraftDTO.getEngineno());
        	vehicleDraft.setChassisno(vehicleDraftDTO.getChassisno());
        	vehicleDraft.setManufactureyear(vehicleDraftDTO.getManufactureyear());
        	vehicleDraft.setPurchasedate(vehicleDraftDTO.getPurchasedate());
        	vehicleDraft.setVehicleprice(vehicleDraftDTO.getVehicleprice());
        	vehicleDraft.setTotalkms(vehicleDraftDTO.getTotalkms());
        	vehicleDraft.setDepreciatedamount(vehicleDraftDTO.getDepreciatedamount());
        	vehicleDraft.setImprovements(vehicleDraftDTO.getImprovements());
        	vehicleDraft.setExpenses(vehicleDraftDTO.getExpenses());
        	vehicleDraft.setRepairexpenses(vehicleDraftDTO.getRepairexpenses());
        	vehicleDraft.setRepairslastsixmonths(vehicleDraftDTO.getRepairslastsixmonths());
        	
        	vehicleDraft.setWhetheraccident(vehicleDraftDTO.getWhetheraccident());
        	
        	vehicleDraft.setAccidentcaseresolved(vehicleDraftDTO.getAccidentcaseresolved());
        	vehicleDraft.setComments(vehicleDraftDTO.getComments());
        	vehicleDraft.setMvireportavailable(vehicleDraftDTO.getMvireportavailable());
        	vehicleDraft.setBattery(vehicleDraftDTO.getBattery());
        	vehicleDraft.setTyres(vehicleDraftDTO.getTyres());
        	
        	vehicleDraft.setAccidentdamage(vehicleDraftDTO.getAccidentdamage());
        	
        	vehicleDraft.setMviprice(vehicleDraftDTO.getMviprice());
        	vehicleDraft.setMviremarks(vehicleDraftDTO.getMviremarks());
        	
        	
            String applicationMode = "F";
            vehicleDraft.setApplicationMode(applicationMode);
            
            String applicationSlno = UUID.randomUUID().toString().replace("-","").substring(0,10);
            vehicleDraft.setApplicationSlno(applicationSlno);
            
            String applicationCode = applicationMode + applicationSlno;
            vehicleDraft.setApplicationCode(applicationCode);

            String versionFlagCode = "2";
            vehicleDraft.setVersionflagcode(versionFlagCode);
            
            vehicleDraft.setEntrydate(LocalDateTime.now());     
            
            log.debug("Populated VehicleDraft before save: {}", vehicleDraft);
            
            VehicleDraft savedDraft = vehicleDraftRepo.save(vehicleDraft);
            
            log.info("VehicleDraft saved with applicationCode: {}", savedDraft.getApplicationCode()); 


            List<PartsConditionInputDTO> partConditions = vehicleDraftDTO.getVehiclePartsDraft();
            if(partConditions != null && !partConditions.isEmpty())
            {
            	log.debug("Processing {} part conditions from DTO.", partConditions.size());
            	for(PartsConditionInputDTO partDTO : partConditions) 
            	{
	
            		VehicleParts vehiclePart = vehiclePartsRepo.findById(partDTO.getVehiclepartcode())
            				.orElseThrow(() -> new EntityNotFoundException("Vehicle Part with code" + partDTO.getVehiclepartcode() + "not found."));
            		
            		VehiclePartsConditionDraft partsDraft = new VehiclePartsConditionDraft();
                    partsDraft.setApplicationcode(savedDraft);      
                    partsDraft.setVehiclepartcode(vehiclePart);     
                    partsDraft.setCondition(partDTO.getCondition()); 

                    vehiclePartsConditionDraftRepo.save(partsDraft);
            	}
            	log.info("Saved {} vehicle part conditions for applicationCode: {}", partConditions.size(), savedDraft.getApplicationCode());
            }
            else
            {
            	log.warn("No vehicle part conditions received in DTO for applicationCode: {}", savedDraft.getApplicationCode());
            }

            return savedDraft.getApplicationCode(); // Return the generated applicationCode
        } catch (EntityNotFoundException ex) {
            log.error("Data integrity error: {}", ex.getMessage());
            // Rethrow or wrap in a specific exception that your ControllerAdvice can handle, potentially returning a 404 or 400
            throw new RuntimeException("Invalid reference data provided: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            // Log the full stack trace for unexpected errors
            log.error("Error adding vehicle draft for DTO: {}", vehicleDraftDTO, ex);
            // Re-throw a generic exception
            throw new RuntimeException("An unexpected error occurred while saving the draft.", ex);
        }
    }   
//    @Transactional(readOnly = true)
//    public List<VehicleDraftListDTO> getDraftsByProcessCode(int processCodeNumber){
//    	log.info("Fetching drafts with process code: {}", processCodeNumber);
//    	
//    	List<VehicleDraft> drafts = vehicleDraftRepo.findDraftsByProcessCodeNumber(processCodeNumber);
//    	
//    	log.info("Found {} drafts.", drafts.size(), processCodeNumber);
//    	
//    	return drafts.stream()
//    			.map(this::mapToVehicleDraftListDTO)
//    			.collect(Collectors.toList());
//    }
//    
//    private VehicleDraftListDTO mapToVehicleDraftListDTO(VehicleDraft draft) {
//        VehicleDraftListDTO dto = new VehicleDraftListDTO();
//        dto.setApplicationCode(draft.getApplicationCode()); // Needed for potential Edit action
//        dto.setRegistrationNo(draft.getRegistrationNo());
//        dto.setVehicleDescription(draft.getVehicledescription());
//        dto.setPurchaseDate(draft.getPurchasedate()); // Keep as Date/LocalDate
//        dto.setDepreciatedValue(draft.getDepreciatedamount()); // Map field name
//        dto.setTotalKmsLogged(draft.getTotalkms()); // Map field name
//        // Map boolean-like fields (adjust logic based on actual stored values)
//        dto.setMviReportsAvailable(draft.getMvireportavailable() != null && draft.getMvireportavailable().equalsIgnoreCase("YES"));
//        dto.setAnyCasePending(draft.getWhetheraccident() != null && draft.getWhetheraccident().equalsIgnoreCase("YES"));
//        return dto;
//    }
}