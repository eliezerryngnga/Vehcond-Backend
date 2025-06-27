 package vehcon.services.appdata;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vehcon.annotations.Auditable;
import vehcon.common.data.util.SpecificationUtils;
import vehcon.dto.appdata.PartsConditionInputDTO;
import vehcon.dto.appdata.VehicleDraftDTO;
import vehcon.dto.appdata.VehicleDraftListDTO;
import vehcon.models.appdata.VehicleFinal;
import vehcon.models.appdata.VehiclePartsConditionFinal;
import vehcon.models.masters.Departments;
import vehcon.models.masters.Districts;
import vehcon.models.masters.FinancialYear;
import vehcon.models.masters.Processes;
import vehcon.models.masters.VehicleManufacturer;
import vehcon.models.masters.VehicleParts;
import vehcon.models.masters.VehicleType;
import vehcon.models.vehiclecondemnations.VehicleDraft;
import vehcon.repo.appdata.ProcessesRepository;
import vehcon.repo.appdata.VehicleDraftRepository;
import vehcon.repo.appdata.VehicleFinalRepository;
import vehcon.repo.appdata.VehiclePartsConditionDraftRepository;
import vehcon.repo.appdata.VehiclePartsConditionFinalRepository;
import vehcon.repo.masters.DepartmentsRepository;
import vehcon.repo.masters.DistrictsRepository;
import vehcon.repo.masters.FinancialYearRepo;
import vehcon.repo.masters.VehicleManufacturerRepository;
import vehcon.repo.masters.VehiclePartsRepository;
import vehcon.repo.masters.VehicleTypeRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleFinalServices {

	private final VehicleDraftRepository vehDraftRepo;
    private final VehiclePartsConditionDraftRepository vehPartsConditionDraftRepo;
    
	private final VehicleFinalRepository vehicleFinalRepo;
    private final VehiclePartsConditionFinalRepository vehiclePartsConditionFinalRepo;
    private final VehiclePartsRepository vehiclePartsRepo;
    private final ProcessesRepository processesRepo;
    private final DepartmentsRepository departmentRepo;
    private final FinancialYearRepo financialYearRepo;
    private final DistrictsRepository districtRepo;
    private final VehicleTypeRepository vehicleTypeRepo;
    private final VehicleManufacturerRepository vehicleManufacturerRepo;

    @Auditable
    @Transactional
    public String addVehicleFinal(VehicleDraftDTO vehicleDraftDTO) {
        try {
        	
        	VehicleFinal vehicleFinal = new VehicleFinal();
        	
        	Processes finalProcess = processesRepo.findById(15)
                    .orElseThrow(() -> new RuntimeException("Initial Process with code 1 not found."));
            vehicleFinal.setProcesscode(finalProcess);
            
           
            Integer departmentCodeFromDTO = vehicleDraftDTO.getDepartmentCode();
            log.info("Department code from DTO: {}", departmentCodeFromDTO);
            if ( departmentCodeFromDTO != null)
            {
            	log.info("Attempting to find Department with code: {}", departmentCodeFromDTO);
            	
                Departments department = departmentRepo.findById(departmentCodeFromDTO)
                		.orElseThrow(() -> new RuntimeException("Department with code " + departmentCodeFromDTO + " not found."));
                
                log.info("Found Department object: {}", (department != null ? department.getDepartmentCode() : "NULL"));
                
                vehicleFinal.setDepartmentCode(department);
                
                log.info("Set department on vehicleFinal. Current vehicleFinal.departmentCode is {}",
                        (vehicleFinal.getDepartmentCode() != null ? vehicleFinal.getDepartmentCode().getDepartmentCode() : "NULL"));
           } else {
               log.warn("Department code from DTO was NULL.");
           }
                       
            Integer financialYearFromDTO = vehicleDraftDTO.getFinancialYearCode();
            if(financialYearFromDTO != null) {
            	FinancialYear financialYear = financialYearRepo.findById(financialYearFromDTO)
                		.orElseThrow(() -> new RuntimeException("Fiancial Year with code " + financialYearFromDTO + " not found"));
            	vehicleFinal.setFinancialYearCode(financialYear);
            }
            
            Integer registeredDistrictFromDTO = vehicleDraftDTO.getRegisteredDistrict();
            if(registeredDistrictFromDTO != null) {
            	Districts registeredDistricts = districtRepo.findById(registeredDistrictFromDTO)
                		.orElseThrow(() -> new RuntimeException("Registered District with code " + registeredDistrictFromDTO + " not found"));
            	vehicleFinal.setRegisteredDistrict(registeredDistricts);
            }
            
            Integer vehicleTypeCodeFromDTO = vehicleDraftDTO.getVehicletypecode();
            if(vehicleTypeCodeFromDTO != null)
            {
            	VehicleType vehicleType = vehicleTypeRepo.findById(vehicleTypeCodeFromDTO)
                    	.orElseThrow(() -> new RuntimeException("Vehicle Type with code " + vehicleTypeCodeFromDTO + " not found"));
            	vehicleFinal.setVehicletypecode(vehicleType);
            }
            
            Integer vehicleManufacturerFromDTO = vehicleDraftDTO.getVehiclemanufacturercode();
            if(vehicleManufacturerFromDTO != null) {
            	VehicleManufacturer vehicleManufacturer = vehicleManufacturerRepo.findById(vehicleManufacturerFromDTO)
                		.orElseThrow(() -> new RuntimeException("Vehicle Manufacturer with code " + vehicleManufacturerFromDTO + " not found"));
            	vehicleFinal.setVehiclemanufacturercode(vehicleManufacturer);
            }
            
            
        	String locations = (vehicleDraftDTO.getAddress1() != null ? vehicleDraftDTO.getAddress1(): "") + ", " + 
        						(vehicleDraftDTO.getAddress2() != null ? vehicleDraftDTO.getAddress2() : "");
        	vehicleFinal.setLocations(locations.equals(", ") ? null : locations.trim());
        	
        	// Handle potential null dates before toString()
        	String dirDateStr = vehicleDraftDTO.getDirectorateLetterDate() != null ? vehicleDraftDTO.getDirectorateLetterDate().toString() : "";
	          String directorateLetterNo = vehicleDraftDTO.getDirectorateLetterNo() != null ? vehicleDraftDTO.getDirectorateLetterNo() : "";
	          if (!directorateLetterNo.isEmpty() || !dirDateStr.isEmpty()) {
	              vehicleFinal.setDirectorateLetterNodate(directorateLetterNo + "|" + dirDateStr);
	          } else {
	              vehicleFinal.setDirectorateLetterNodate(null);
	          }

	          String govDateStr = vehicleDraftDTO.getGovForwardingLetterDate() != null ? vehicleDraftDTO.getGovForwardingLetterDate().toString() : "";
	          String forwardingLetterNo = vehicleDraftDTO.getForwardingLetterNo() != null ? vehicleDraftDTO.getForwardingLetterNo() : "";
	          if (!forwardingLetterNo.isEmpty() || !govDateStr.isEmpty()) {
	              vehicleFinal.setGovtLetterNoDate(forwardingLetterNo + "|" + govDateStr);
	          } else {
	              vehicleFinal.setGovtLetterNoDate(null);
	          }

	          String rtoCode = vehicleDraftDTO.getRtoCode() != null ? vehicleDraftDTO.getRtoCode() : "";
	          String vehicleRegNo = vehicleDraftDTO.getVehicleRegistrationNumber() != null ? vehicleDraftDTO.getVehicleRegistrationNumber() : "";
	          if (!rtoCode.isEmpty() || !vehicleRegNo.isEmpty()) {
	               vehicleFinal.setRegistrationNo(rtoCode + " " + vehicleRegNo);
	          } else {
	               vehicleFinal.setRegistrationNo(null);
	          }
             
            vehicleFinal.setOfficeName(vehicleDraftDTO.getOfficeName());
        	vehicleFinal.setOfficerDesignation(vehicleDraftDTO.getOfficerDesignation());
        	vehicleFinal.setPremises(vehicleDraftDTO.getPremises());
        	vehicleFinal.setVehicledescription(vehicleDraftDTO.getVehicledescription());
        	vehicleFinal.setEngineno(vehicleDraftDTO.getEngineno());
        	vehicleFinal.setChassisno(vehicleDraftDTO.getChassisno());
        	vehicleFinal.setManufactureyear(vehicleDraftDTO.getManufactureyear());
        	vehicleFinal.setPurchasedate(vehicleDraftDTO.getPurchasedate());
        	vehicleFinal.setVehicleprice(vehicleDraftDTO.getVehicleprice());
        	vehicleFinal.setTotalkms(vehicleDraftDTO.getTotalkms());
        	vehicleFinal.setDepreciatedamount(vehicleDraftDTO.getDepreciatedamount());
        	vehicleFinal.setImprovements(vehicleDraftDTO.getImprovements());
        	vehicleFinal.setExpenses(vehicleDraftDTO.getExpenses());
        	vehicleFinal.setRepairexpenses(vehicleDraftDTO.getRepairexpenses());
        	vehicleFinal.setRepairslastsixmonths(vehicleDraftDTO.getRepairslastsixmonths());
        	vehicleFinal.setWhetheraccident(vehicleDraftDTO.getWhetheraccident());
        	vehicleFinal.setAccidentcaseresolved(vehicleDraftDTO.getAccidentcaseresolved());
        	vehicleFinal.setComments(vehicleDraftDTO.getComments());
        	vehicleFinal.setMvireportavailable(vehicleDraftDTO.getMvireportavailable());
        	vehicleFinal.setBattery(vehicleDraftDTO.getBattery());
        	vehicleFinal.setTyres(vehicleDraftDTO.getTyres());
        	vehicleFinal.setAccidentdamage(vehicleDraftDTO.getAccidentdamage());
        	vehicleFinal.setMviprice(vehicleDraftDTO.getMviprice());
        	vehicleFinal.setMviremarks(vehicleDraftDTO.getMviremarks());
        	
        	
            String applicationMode = "F";
            vehicleFinal.setApplicationMode(applicationMode);
            
            String applicationSlno = UUID.randomUUID().toString().substring(0,10);
            vehicleFinal.setApplicationSlno(applicationSlno);
            
            String applicationCode = applicationMode + applicationSlno;
            vehicleFinal.setApplicationCode(applicationCode);

            String versionFlagCode = "2";
            vehicleFinal.setVersionflagcode(versionFlagCode);
            
            vehicleFinal.setEntrydate(LocalDate.now());     
            
            log.debug("Populated VehicleDraft before save: {}", vehicleFinal);
            
            log.info(">>> Final state of VehicleFinal entity BEFORE save: {}", vehicleFinal);
            if (vehicleFinal.getDepartmentCode() == null) {
                 log.error(">>> CRITICAL: vehicleFinal.departmentCode is NULL immediately before calling save!");
            } else {
                 log.info(">>> vehicleFinal.departmentCode object before save is NOT NULL. Associated Dept ID: {}",
                          vehicleFinal.getDepartmentCode().getDepartmentCode());
            }
            
            VehicleFinal saveFinalized = vehicleFinalRepo.save(vehicleFinal);
            
            log.info("Vehicle Final saved with applicationCode: {}", saveFinalized.getApplicationCode()); 
            // Fetch all VehicleParts entities from the master.vehicleparts table
//            List<VehicleParts> vehiclePartsList = vehiclePartsRepo.findAll();

            List<PartsConditionInputDTO> partConditions = vehicleDraftDTO.getVehiclePartsDraft();
            if(partConditions != null && !partConditions.isEmpty())
            {
            	log.debug("Processing {} part conditions from DTO.", partConditions.size());
            	for(PartsConditionInputDTO partDTO : partConditions) {
            		VehicleParts vehiclePart = vehiclePartsRepo.findById(partDTO.getVehiclepartcode())
            				.orElseThrow(() -> new EntityNotFoundException("Vehicle Part with code" + partDTO.getVehiclepartcode() + "not found."));
            		
            		VehiclePartsConditionFinal partsFinal = new VehiclePartsConditionFinal();
                    partsFinal.setApplicationCode(saveFinalized);      
                    partsFinal.setVehiclepartcode(vehiclePart);     
                    partsFinal.setCondition(partDTO.getCondition()); 

                    vehiclePartsConditionFinalRepo.save(partsFinal);
            	}
            	log.info("Saved {} vehicle part conditions for applicationCode: {}", partConditions.size(), saveFinalized.getApplicationCode());
            }
            else
            {
            	log.warn("No vehicle part conditions received in DTO for applicationCode: {}", saveFinalized.getApplicationCode());
            }
            
            String draftApplicationCode = vehicleDraftDTO.getApplicationCode();
            
            
            
            if(draftApplicationCode != null && !draftApplicationCode.isEmpty())
            {
            	VehicleDraft draftToDelete = vehDraftRepo.findByApplicationCode(draftApplicationCode)
                		.orElse(null);
            	
            	if(draftToDelete != null)
            	{
            		vehPartsConditionDraftRepo.deleteByApplicationCode(draftToDelete);
            		
            		vehDraftRepo.delete(draftToDelete);
            		log.info("Successfully deleted draft with applicationCode {} after final submission.", draftApplicationCode);
                } else {
                    log.warn("Could not find draft with applicationCode {} to delete after finalization. It may have been deleted already or never existed.", draftApplicationCode);
                }
            } else {
                log.warn("No draft applicationCode was provided in the DTO, so no draft record could be deleted.");
            }
            
            return saveFinalized.getApplicationCode();
            
        } catch (EntityNotFoundException ex) {
            log.error("Data integrity error: {}", ex.getMessage());
            throw new RuntimeException("Invalid reference data provided: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            log.error("Error adding vehicle draft for DTO: {}", vehicleDraftDTO, ex);
            throw new RuntimeException("An unexpected error occurred while saving the draft.", ex);
        }
    }

    @Transactional(readOnly = true)
    public Page<VehicleDraftListDTO> getFilteredFinalsByProcessCode(
            int processCodeNumber, // Required filter
            Integer userDepartmentCode, // Required filter
            String searchTerm, // Optional search
            Pageable pageable) // For pagination/sorting
    {
        log.info("Fetching final vehicles with processCode: {}, departmentCode: {}, searchTerm: '{}', pageable: {}",
                 processCodeNumber, userDepartmentCode, searchTerm, pageable);

        Specification<VehicleFinal> finalSpec = Specification.where(null);
        
        Specification<VehicleFinal> processSpec = SpecificationUtils.nestedPropertyEquals(
            processCodeNumber, "processcode", "processcode"); 
        if(processSpec != null)
        {
        	finalSpec = finalSpec.and(processSpec);
        }
        
        if(userDepartmentCode != null)
        {
        	Specification<VehicleFinal> deptSpec = SpecificationUtils.nestedPropertyEquals(
                    userDepartmentCode, "departmentCode", "departmentCode");
        	
        	if(deptSpec != null)
        	{
        		finalSpec = finalSpec.and(deptSpec);
        	}
        }	

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            List<String> searchableFields = Arrays.asList(
                "registrationNo",     
                "vehicledescription", 
                "applicationCode"     
            );
            
            Specification<VehicleFinal> searchSpec = SpecificationUtils.searchInFields(searchTerm, searchableFields);
            if (searchSpec != null) {
                finalSpec = finalSpec.and(searchSpec);
            }
        }
        Page<VehicleFinal> finalPage = vehicleFinalRepo.findAll(finalSpec, pageable);

        return finalPage.map(this::mapToVehicleDraftListDTO);
    }
    
    
    private VehicleDraftListDTO mapToVehicleDraftListDTO(VehicleFinal finalList) {
        VehicleDraftListDTO dto = new VehicleDraftListDTO();
        dto.setApplicationCode(finalList.getApplicationCode()); // Needed for potential Edit action
        dto.setRegistrationNo(finalList.getRegistrationNo());
        dto.setVehicleDescription(finalList.getVehicledescription());
        dto.setPurchaseDate(finalList.getPurchasedate()); // Keep as Date/LocalDate
        dto.setDepreciatedValue(finalList.getDepreciatedamount()); // Map field name
        dto.setTotalKmsLogged(finalList.getTotalkms()); // Map field name
//        dto.setMviReportsAvailable(finalList.getMvireportavailable() != null && finalList.getMvireportavailable().equalsIgnoreCase("Y"));
//        dto.setAnyCasePending(finalList.getWhetheraccident() != null && finalList.getWhetheraccident().equalsIgnoreCase("Y"));

        //To be Removed:
        dto.setMviReportsAvailable(finalList.getMvireportavailable() != null && finalList.getMvireportavailable().equalsIgnoreCase("Y"));
        dto.setAnyCasePending(finalList.getWhetheraccident() != null && finalList.getWhetheraccident().equalsIgnoreCase("Y"));
        return dto;
    }
}