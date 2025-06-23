package vehcon.services.appdata;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
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
import vehcon.models.auth.User;
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

    private static final int PROCESS_CODE_DRAFT = 1;
    private static final int PROCESS_CODE_REJECTED_VEHICLE = 14;
    
    private static final List<String> searchableFields = Arrays.asList(
			"registrationNo",
			"vehicledescription",
			"applicationcode"
			);
    
	  @Auditable
	  @Transactional
	  public String saveOrUpdateVehicleDraft(VehicleDraftDTO vehicleDraftDTO) {
	      try {
	          VehicleDraft vehicleDraft;
	          
	          if(vehicleDraftDTO.getApplicationCode() != null && !vehicleDraftDTO.getApplicationCode().isEmpty())
	          {
	        	  log.info("Updating existing draft with applicaitonCode: {}", vehicleDraftDTO.getApplicationCode());
	        	  
	        	  vehicleDraft = vehicleDraftRepo.findByApplicationCode(vehicleDraftDTO.getApplicationCode())
	        			  .orElseThrow(() -> new EntityNotFoundException("Draft with application code " + vehicleDraftDTO.getApplicationCode() + " not found."));
	          }
	          else {
	        	  log.info("Creating new vehicle draft.");
	        	  
	        	  vehicleDraft = new VehicleDraft();

		          Processes initialProcess = processesRepo.findById(1) 
		                  .orElseThrow(() -> new RuntimeException("Initial Process with ID 1 not found."));
		          vehicleDraft.setProcesscode(initialProcess);
		          
		          String applicationMode = "F";
		          vehicleDraft.setApplicationMode(applicationMode);
		
		          String applicationSlno = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
		          vehicleDraft.setApplicationSlno(applicationSlno);
		
		          String applicationCode = applicationMode + applicationSlno;
		          vehicleDraft.setApplicationCode(applicationCode);
		
		          String versionFlagCode = "2";
		          vehicleDraft.setVersionflagcode(versionFlagCode);
		
		          vehicleDraft.setEntrydate(LocalDate.now());
	          }
	
	          Integer departmentCodeFromDTO = vehicleDraftDTO.getDepartmentCode();
	          if (departmentCodeFromDTO != null) {
	              Departments department = departmentRepo.findById(departmentCodeFromDTO)
	                      .orElseThrow(() -> new RuntimeException("Department with code " + departmentCodeFromDTO + " not found."));
	              vehicleDraft.setDepartmentCode(department);
	          }
	
	          Integer financialYearFromDTO = vehicleDraftDTO.getFinancialYearCode();
	          if (financialYearFromDTO != null) {
	              FinancialYear financialYear = financialYearRepo.findById(financialYearFromDTO)
	                      .orElseThrow(() -> new RuntimeException("Financial Year with code " + financialYearFromDTO + " not found"));
	              vehicleDraft.setFinancialYearCode(financialYear);
	          }
	
	          Integer registeredDistrictFromDTO = vehicleDraftDTO.getRegisteredDistrict();
	          if (registeredDistrictFromDTO != null) {
	              Districts registeredDistricts = districtRepo.findById(registeredDistrictFromDTO)
	                      .orElseThrow(() -> new RuntimeException("Registered District with code " + registeredDistrictFromDTO + " not found"));
	              vehicleDraft.setRegisteredDistrict(registeredDistricts);
	          }
	
	          Integer vehicleTypeCodeFromDTO = vehicleDraftDTO.getVehicletypecode();
	          if (vehicleTypeCodeFromDTO != null) {
	              VehicleType vehicleType = vehicleTypeRepo.findById(vehicleTypeCodeFromDTO)
	                      .orElseThrow(() -> new RuntimeException("Vehicle Type with code " + vehicleTypeCodeFromDTO + " not found"));
	              vehicleDraft.setVehicletypecode(vehicleType);
	          }
	
	          Integer vehicleManufacturerFromDTO = vehicleDraftDTO.getVehiclemanufacturercode();
	          if (vehicleManufacturerFromDTO != null) {
	              VehicleManufacturer vehicleManufacturer = vehicleManufacturerRepo.findById(vehicleManufacturerFromDTO)
	                      .orElseThrow(() -> new RuntimeException("Vehicle Manufacturer with code " + vehicleManufacturerFromDTO + " not found"));
	              vehicleDraft.setVehiclemanufacturercode(vehicleManufacturer);
	          }
	
	
	          String locations = (vehicleDraftDTO.getAddress1() != null ? vehicleDraftDTO.getAddress1() : "") + ", " + (vehicleDraftDTO.getAddress2() != null ? vehicleDraftDTO.getAddress2() : "");
	          vehicleDraft.setLocations(locations.equals(", ") ? null : locations.trim());

	          String dirDateStr = vehicleDraftDTO.getDirectorateLetterDate() != null ? vehicleDraftDTO.getDirectorateLetterDate().toString() : "";
	          String directorateLetterNo = vehicleDraftDTO.getDirectorateLetterNo() != null ? vehicleDraftDTO.getDirectorateLetterNo() : "";
	          if (!directorateLetterNo.isEmpty() || !dirDateStr.isEmpty()) {
	              vehicleDraft.setDirectorateLetterNodate(directorateLetterNo + "|" + dirDateStr);
	          } else {
	              vehicleDraft.setDirectorateLetterNodate(null);
	          }
	
	
	          String govDateStr = vehicleDraftDTO.getGovForwardingLetterDate() != null ? vehicleDraftDTO.getGovForwardingLetterDate().toString() : "";
	          String forwardingLetterNo = vehicleDraftDTO.getForwardingLetterNo() != null ? vehicleDraftDTO.getForwardingLetterNo() : "";
	          if (!forwardingLetterNo.isEmpty() || !govDateStr.isEmpty()) {
	              vehicleDraft.setGovtLetterNoDate(forwardingLetterNo + "|" + govDateStr);
	          } else {
	              vehicleDraft.setGovtLetterNoDate(null);
	          }
	          
	          String rtoCode = vehicleDraftDTO.getRtoCode() != null ? vehicleDraftDTO.getRtoCode() : "";
	          String vehicleRegNo = vehicleDraftDTO.getVehicleRegistrationNumber() != null ? vehicleDraftDTO.getVehicleRegistrationNumber() : "";
	          if (!rtoCode.isEmpty() || !vehicleRegNo.isEmpty()) {
	               vehicleDraft.setRegistrationNo(rtoCode + "|" + vehicleRegNo);
	          } else {
	               vehicleDraft.setRegistrationNo(null);
	          }
	
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
		
	          log.debug("Populated VehicleDraft before save: {}", vehicleDraft);
	
	          VehicleDraft savedDraft = vehicleDraftRepo.save(vehicleDraft);
	          log.info("VehicleDraft saved with applicationCode: {}", savedDraft.getApplicationCode());
	
	          vehiclePartsConditionDraftRepo.deleteByApplicationCode(savedDraft);
	          log.debug("Cleared existing part conditions for applicationCode: {}", savedDraft.getApplicationCode());
	
	          
	          List<PartsConditionInputDTO> partConditions = vehicleDraftDTO.getVehiclePartsDraft();
	          if (partConditions != null && !partConditions.isEmpty()) {
	              log.debug("Processing {} part conditions from DTO.", partConditions.size());
	              for (PartsConditionInputDTO partDTO : partConditions) {
	
	                  VehicleParts vehiclePart = vehiclePartsRepo.findById(partDTO.getVehiclepartcode())
	                          .orElseThrow(() -> new EntityNotFoundException("Vehicle Part with code " + partDTO.getVehiclepartcode() + " not found.")); // Corrected typo
	
	                  VehiclePartsConditionDraft partsDraft = new VehiclePartsConditionDraft();
	                  partsDraft.setApplicationCode(savedDraft);
	                  partsDraft.setVehiclepartcode(vehiclePart);
	                  partsDraft.setCondition(partDTO.getCondition());
	
	                  vehiclePartsConditionDraftRepo.save(partsDraft);
	              }
	              log.info("Saved {} vehicle part conditions for applicationCode: {}", partConditions.size(), savedDraft.getApplicationCode());
	          } else {
	              log.warn("No vehicle part conditions received in DTO for applicationCode: {}", savedDraft.getApplicationCode());
	          }
	
	          return savedDraft.getApplicationCode();
	          
	      } catch (EntityNotFoundException ex) {
	          log.error("Data integrity error: {}", ex.getMessage());
	          throw new RuntimeException("Invalid reference data provided: " + ex.getMessage(), ex);
	      } catch (Exception ex) {
	          log.error("Error adding vehicle draft for DTO: {}", vehicleDraftDTO, ex);
	          throw new RuntimeException("An unexpected error occurred while saving the draft.", ex);
	      }
	  }
	  
	  
	  @Transactional(readOnly = true)
	  public Page<VehicleDraftListDTO> getDraftVehicles(
	            String searchTerm,
	            Pageable pageable,
	            User user
	    ) {

	        Specification<VehicleDraft> finalSpec = Specification
	                .where(getSpecByProcessCode(PROCESS_CODE_DRAFT))
	                .and(getSearchSpec(searchTerm));
	                
	         Integer deptCode = user.getDepartment() != null ? user.getDepartment().getDepartmentCode(): null;
	         if(deptCode == null)
	         {
	        	 throw new AccessDeniedException("User does not have a department assigned.");
	         }
	         finalSpec = finalSpec.and(getSpecByDepartmentCode(deptCode));
	         

	        Page<VehicleDraft> draftsPage = vehicleDraftRepo.findAll(finalSpec, pageable);
	        return draftsPage.map(this::mapToVehicleDraftListDTO);
	    }
	  
	  public Page<VehicleDraftListDTO> getRejectedVehicles(
	            String searchTerm,
	            Pageable pageable,
	            User user
	    ) {

	        Specification<VehicleDraft> finalSpec = Specification
	                .where(getSpecByProcessCode(PROCESS_CODE_REJECTED_VEHICLE))
	                .and(getSearchSpec(searchTerm));
	                
	         Integer deptCode = user.getDepartment() != null ? user.getDepartment().getDepartmentCode(): null;
	         if(deptCode == null)
	         {
	        	 throw new AccessDeniedException("User does not have a department assigned.");
	         }
	         finalSpec = finalSpec.and(getSpecByDepartmentCode(deptCode));
	         

	        Page<VehicleDraft> draftsPage = vehicleDraftRepo.findAll(finalSpec, pageable);
	        return draftsPage.map(this::mapToVehicleDraftListDTO);
	    }
	  
//    @Transactional(readOnly = true)
//    public VehicleDraft getDraftById(String applicationCode, Integer userDepartmentCode) {
//        log.info("Fetching draft with applicationCode: {} for user department: {}", applicationCode, userDepartmentCode);
//
//        VehicleDraft draft = vehicleDraftRepo.findById(applicationCode)
//                .orElseThrow(() -> {
//                    log.warn("VehicleDraft not found with code: {}", applicationCode);
//                    return new ObjectNotFoundException("VehicleDraft not found with code: " + applicationCode);
//                });
//
//        if (userDepartmentCode != null && draft.getDepartmentCode() != null) {
//            if (!userDepartmentCode.equals(draft.getDepartmentCode().getDepartmentCode())) {
//                 log.warn("User from department {} attempting to access draft belonging to department {}",
//                         userDepartmentCode, draft.getDepartmentCode().getDepartmentCode());
//            }
//        }
//        return draft;
//    }

    @Transactional(readOnly = true)
    public VehicleDraftDTO getVehicleDraftByApplicationCode(String applicationCode) 
    {
        VehicleDraft vehDraft = vehicleDraftRepo.findByApplicationCode(applicationCode)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle Draft with applicationCode " + applicationCode + " not found."));

        List<VehiclePartsConditionDraft> partConditionsFromDb = vehiclePartsConditionDraftRepo.findByApplicationCode(vehDraft);
       
        VehicleDraftDTO dto = new VehicleDraftDTO();

        dto.setApplicationCode(vehDraft.getApplicationCode());
        
        dto.setOfficeName(vehDraft.getOfficeName());
        dto.setOfficerDesignation(vehDraft.getOfficerDesignation());
        dto.setPremises(vehDraft.getPremises());
        dto.setVehicledescription(vehDraft.getVehicledescription());
        dto.setEngineno(vehDraft.getEngineno());
        dto.setChassisno(vehDraft.getChassisno());
        dto.setManufactureyear(vehDraft.getManufactureyear());
        dto.setPurchasedate(vehDraft.getPurchasedate());
        dto.setVehicleprice(vehDraft.getVehicleprice());
        dto.setTotalkms(vehDraft.getTotalkms());
        dto.setDepreciatedamount(vehDraft.getDepreciatedamount());
        dto.setImprovements(vehDraft.getImprovements());
        dto.setExpenses(vehDraft.getExpenses());
        dto.setRepairexpenses(vehDraft.getRepairexpenses());
        dto.setRepairslastsixmonths(vehDraft.getRepairslastsixmonths());
        dto.setWhetheraccident(vehDraft.getWhetheraccident());
        dto.setAccidentcaseresolved(vehDraft.getAccidentcaseresolved());
        dto.setComments(vehDraft.getComments());
        dto.setMvireportavailable(vehDraft.getMvireportavailable());
        dto.setBattery(vehDraft.getBattery());
        dto.setTyres(vehDraft.getTyres());
        dto.setAccidentdamage(vehDraft.getAccidentdamage());
        dto.setMviprice(vehDraft.getMviprice());
        dto.setMviremarks(vehDraft.getMviremarks());

        if (vehDraft.getDepartmentCode() != null) {
            dto.setDepartmentCode(vehDraft.getDepartmentCode().getDepartmentCode());
        }

        if (vehDraft.getFinancialYearCode() != null) {
            dto.setFinancialYearCode(vehDraft.getFinancialYearCode().getFinancialyearcode());
        }

        if (vehDraft.getRegisteredDistrict() != null) {
            dto.setRegisteredDistrict(vehDraft.getRegisteredDistrict().getDistrictCode());
        }

        if (vehDraft.getVehicletypecode() != null) {
            dto.setVehicletypecode(vehDraft.getVehicletypecode().getVehicleTypeCode());
        }

        if (vehDraft.getVehiclemanufacturercode() != null) {
            dto.setVehiclemanufacturercode(vehDraft.getVehiclemanufacturercode().getVehicleManufacturerCode());
        }

        // Updated parsing logic assuming "|" as separator
        if (vehDraft.getRegistrationNo() != null && !vehDraft.getRegistrationNo().trim().isEmpty()) {
            String[] regParts = vehDraft.getRegistrationNo().split("\\|", 2); // Split by |
            dto.setRtoCode(regParts.length > 0 ? regParts[0] : null);
            dto.setVehicleRegistrationNumber(regParts.length > 1 ? regParts[1] : null);
        }
        
        if (vehDraft.getLocations() != null && !vehDraft.getLocations().trim().isEmpty()) {
            String[] addressParts = vehDraft.getLocations().split(",", 2);
            dto.setAddress1(addressParts.length > 0 ? addressParts[0].trim() : null);
            dto.setAddress2(addressParts.length > 1 ? addressParts[1].trim() : null);
        }


        if (vehDraft.getDirectorateLetterNodate() != null && vehDraft.getDirectorateLetterNodate().contains("|")) {
            String[] letterParts = vehDraft.getDirectorateLetterNodate().split("\\|", 2);
            dto.setDirectorateLetterNo(letterParts[0]);
            if (letterParts.length > 1 && !letterParts[1].trim().isEmpty()) {
                try {
                    dto.setDirectorateLetterDate(LocalDate.parse(letterParts[1]));
                } catch (Exception e) {
                    log.warn("Could not parse directorate letter date: {}", letterParts[1], e);
                    dto.setDirectorateLetterDate(null);
                }
            }
        }

        if (vehDraft.getGovtLetterNoDate() != null && vehDraft.getGovtLetterNoDate().contains("|")) {
            String[] letterParts = vehDraft.getGovtLetterNoDate().split("\\|", 2);
            dto.setForwardingLetterNo(letterParts[0]);
            if (letterParts.length > 1 && !letterParts[1].trim().isEmpty()) {
                try {
                    dto.setGovForwardingLetterDate(LocalDate.parse(letterParts[1]));
                } catch (Exception e) {
                    log.warn("Could not parse government forwarding letter date: {}", letterParts[1], e);
                    dto.setGovForwardingLetterDate(null);
                }
            }
        }


        if (partConditionsFromDb != null && !partConditionsFromDb.isEmpty()) {
            List<PartsConditionInputDTO> partConditionDTOs = partConditionsFromDb.stream()
                    .map(pcd -> {
                        PartsConditionInputDTO partDto = new PartsConditionInputDTO();
                        if (pcd.getVehiclepartcode() != null) {
                            partDto.setVehiclepartcode(pcd.getVehiclepartcode().getVehiclePartCode());
                        }
                        partDto.setCondition(pcd.getCondition());
                        return partDto;
                    })
                    .collect(Collectors.toList());
            dto.setVehiclePartsDraft(partConditionDTOs);
        }

        return dto;
    }

 // --- Private Specification Helper Methods ---
    private Specification<VehicleDraft> getSearchSpec(String searchTerm) {
        return SpecificationUtils.searchInFields(searchTerm, searchableFields);
    }
    
    private Specification<VehicleDraft> getSpecByProcessCode(int targetProcessCode) {
        return SpecificationUtils.nestedPropertyEquals(
            targetProcessCode, "processcode", "processcode"
        );
    }
    
    private Specification<VehicleDraft> getSpecByDepartmentCode(int departmentCode) {
        return SpecificationUtils.nestedPropertyEquals(departmentCode, "departmentCode", "departmentCode");
    }
// --- Private DTO Helper Methods ---
    private VehicleDraftListDTO mapToVehicleDraftListDTO(VehicleDraft draft) {
        VehicleDraftListDTO dto = new VehicleDraftListDTO();
        dto.setApplicationCode(draft.getApplicationCode());
        
        if (draft.getRegistrationNo() != null && draft.getRegistrationNo().contains("|")) {
            dto.setRegistrationNo(draft.getRegistrationNo().replace("|", " "));
        } else {
            dto.setRegistrationNo(draft.getRegistrationNo());
        }
        
        dto.setVehicleDescription(draft.getVehicledescription());
        dto.setPurchaseDate(draft.getPurchasedate());
        dto.setDepreciatedValue(draft.getDepreciatedamount());
        dto.setTotalKmsLogged(draft.getTotalkms());

//        dto.setMviReportsAvailable(draft.getMvireportavailable() != null && draft.getMvireportavailable().equalsIgnoreCase("YES"));
//        dto.setAnyCasePending(draft.getWhetheraccident() != null && draft.getWhetheraccident().equalsIgnoreCase("YES"));
        
        dto.setMviReportsAvailable(draft.getMvireportavailable() != null && draft.getMvireportavailable().equalsIgnoreCase("Y"));
        dto.setAnyCasePending(draft.getWhetheraccident() != null && draft.getWhetheraccident().equalsIgnoreCase("Y"));
        return dto;
    }
    
}