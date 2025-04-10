package vehcon.services.appdata;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
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

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE;
    
    @Transactional
    public String addVehicleDraft(VehicleDraftDTO vehicleDraftDTO) {
        try {
        	
        	VehicleDraft vehicleDraft = new VehicleDraft();
        	vehicleDraft.setLocations(vehicleDraftDTO.getAddress1()+", "+vehicleDraftDTO.getAddress2());
        	vehicleDraft.setDirectorateLetterNodate(vehicleDraftDTO.getDirectorateLetterNo()+" "+vehicleDraftDTO.getDirectorateLetterDate());
            vehicleDraft.setGovtLetterNoDate(vehicleDraftDTO.getForwardingLetterNo()+" "+vehicleDraftDTO.getGovForwardingLetterDate());
            vehicleDraft.setRegistrationNo(vehicleDraftDTO.getRtoNo()+" "+vehicleDraftDTO.getVehicleRegistrationNumber());
        	
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
        	vehicleDraft.setAccidentdamage(vehicleDraft.getAccidentdamage());
        	vehicleDraft.setMviprice(vehicleDraftDTO.getMviprice());
        	vehicleDraft.setMviremarks(vehicleDraftDTO.getMviremarks());
        	
        	Processes initialProcess = processesRepo.findById(1)
                    .orElseThrow(() -> new RuntimeException("Initial Process with code 1 not found."));
            vehicleDraft.setProcesscode(initialProcess);
            
            Integer departmentCodeFromDTO = vehicleDraftDTO.getDepartmentCode();
            Departments department = departmentRepo.findById(departmentCodeFromDTO)
            		.orElseThrow(() -> new RuntimeException("Department with code " + departmentCodeFromDTO + " not found."));
           
            Integer financialYearFromDTO = vehicleDraftDTO.getFinancialYearCode();
            FinancialYear financialYear = financialYearRepo.findById(financialYearFromDTO)
            		.orElseThrow(() -> new RuntimeException("Fiancial Year with code " + financialYearFromDTO + " not found"));
            
            Integer registeredDistrictFromDTO = vehicleDraftDTO.getRegisteredDistrict();
            Districts registeredDistricts = districtRepo.findById(registeredDistrictFromDTO)
            		.orElseThrow(() -> new RuntimeException("Registered District with code " + registeredDistrictFromDTO + " not found"));
            
            Integer vehicleTypeCodeFromDTO = vehicleDraftDTO.getVehicletypecode();
            VehicleType vehicleType = vehicleTypeRepo.findById(vehicleTypeCodeFromDTO)
            	.orElseThrow(() -> new RuntimeException("Vehicle Type with code " + vehicleTypeCodeFromDTO + " not found"));

            Integer vehicleManufacturerFromDTO = vehicleDraftDTO.getVehiclemanufacturercode();
            VehicleManufacturer vehicleManufacturer = vehicleManufacturerRepo.findById(vehicleManufacturerFromDTO)
            		.orElseThrow(() -> new RuntimeException("Vehicle Type with code " + vehicleManufacturerFromDTO + " not found"));
            String applicationMode = "F";
            vehicleDraft.setApplicationMode(applicationMode);
            
            String applicationSlno = UUID.randomUUID().toString().substring(0,10);
            vehicleDraft.setApplicationSlno(applicationSlno);
            
            String applicationCode = applicationMode + applicationSlno;
            vehicleDraft.setApplicationCode(applicationCode);

            String versionFlagCode = "2";
            vehicleDraft.setVersionflagcode(versionFlagCode);
            
            vehicleDraft.setEntrydate(LocalDateTime.now());            
            VehicleDraft savedDraft = vehicleDraftRepo.save(vehicleDraft);
            
            // Fetch all VehicleParts entities from the master.vehicleparts table
            List<VehicleParts> vehiclePartsList = vehiclePartsRepo.findAll();

            // Using the applicationCode, Create all the VehiclePartsDraft using this applicationCode and vehiclepartcode
            for (VehicleParts vehiclePart : vehiclePartsList) {
                VehiclePartsConditionDraft partsDraft = new VehiclePartsConditionDraft();

                // Setting the related entities for the composite primary key
                partsDraft.setApplicationcode(savedDraft);
                partsDraft.setVehiclepartcode(vehiclePart);

                vehiclePartsConditionDraftRepo.save(partsDraft);
            }
            System.out.println("Draft: "+savedDraft);
            return savedDraft.getApplicationCode(); // Return the generated applicationCode
        } catch (Exception ex) {
        	ex.printStackTrace();
            throw ex;
        }
    }

}