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
import vehcon.models.appdata.VehicleFinal;
import vehcon.models.appdata.VehiclePartsConditionFinal;
import vehcon.models.masters.Departments;
import vehcon.models.masters.Districts;
import vehcon.models.masters.FinancialYear;
import vehcon.models.masters.Processes;
import vehcon.models.masters.VehicleManufacturer;
import vehcon.models.masters.VehicleParts;
import vehcon.models.masters.VehicleType;
import vehcon.repo.appdata.ProcessesRepository;
import vehcon.repo.appdata.VehicleFinalRepository;
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

    private final VehicleFinalRepository vehicleFinalRepo;
    private final VehiclePartsConditionFinalRepository vehiclePartsConditionFinalRepo;
    private final VehiclePartsRepository vehiclePartsRepo;
    private final ProcessesRepository processesRepo;
    private final DepartmentsRepository departmentRepo;
    private final FinancialYearRepo financialYearRepo;
    private final DistrictsRepository districtRepo;
    private final VehicleTypeRepository vehicleTypeRepo;
    private final VehicleManufacturerRepository vehicleManufacturerRepo;

    @Transactional
    public String addVehicleFinal(VehicleDraftDTO vehicleDraftDTO) {
        try {
        	
        	VehicleFinal vehicleFinal = new VehicleFinal();
        	
        	Processes finalProcess = processesRepo.findById(2)
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
        	vehicleFinal.setLocations(locations.equals(", ") ? "N/A" : locations);
        	
        	// Handle potential null dates before toString()
            String dirDateStr = vehicleDraftDTO.getDirectorateLetterDate() != null ? vehicleDraftDTO.getDirectorateLetterDate().toString() : "";
            String directorateLetterNodate = (vehicleDraftDTO.getDirectorateLetterNo() != null ? vehicleDraftDTO.getDirectorateLetterNo() : "") + " " + dirDateStr;
             vehicleFinal.setDirectorateLetterNodate(directorateLetterNodate.trim().isEmpty() ? null : directorateLetterNodate.trim());

            String govDateStr = vehicleDraftDTO.getGovForwardingLetterDate() != null ? vehicleDraftDTO.getGovForwardingLetterDate().toString() : "";
            String govtLetterNoDate = (vehicleDraftDTO.getForwardingLetterNo() != null ? vehicleDraftDTO.getForwardingLetterNo() : "") + " " + govDateStr;
            vehicleFinal.setGovtLetterNoDate(govtLetterNoDate.trim().isEmpty() ? null : govtLetterNoDate.trim());

            String registrationNo = (vehicleDraftDTO.getRtoNo() != null ? vehicleDraftDTO.getRtoNo() : "") + " " +
                                     (vehicleDraftDTO.getVehicleRegistrationNumber() != null ? vehicleDraftDTO.getVehicleRegistrationNumber() : "");
             vehicleFinal.setRegistrationNo(registrationNo.trim().isEmpty() ? null : registrationNo.trim());
        	
             
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
            
            vehicleFinal.setEntrydate(LocalDateTime.now());     
            
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
                    partsFinal.setApplicationcode(saveFinalized);      
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
            
            return saveFinalized.getApplicationCode();
            
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
}