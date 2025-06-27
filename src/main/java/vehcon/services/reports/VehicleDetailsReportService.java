package vehcon.services.reports;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.pdf.JRPdfExporter;
import vehcon.dto.reports.VehicleDetailsDTO;
import vehcon.dto.reports.VehiclePartsConditionDTO;
import vehcon.exception.ReportGenerationException;
import vehcon.exception.ResourceNotFoundException;
import vehcon.models.appdata.VehicleFinal;
import vehcon.models.appdata.VehiclePartsConditionFinal;
import vehcon.models.masters.VehicleParts;
import vehcon.repo.appdata.VehicleFinalRepository;
import vehcon.repo.appdata.VehiclePartsConditionFinalRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class VehicleDetailsReportService {
	
	private final VehicleFinalRepository vehFinalRepo;
	private final VehiclePartsConditionFinalRepository vehPartsConditionRepo;
	
//	jasper reports (.jrxml file)
	private static final String VEHICLE_DETAILS_REPORT_PATH = "/reports/vehicleDetails.jasper"; 
	
	public byte[] generateVehicleDetailsReport(String applicationCode) {
		
    	VehicleFinal vehFinal = vehFinalRepo.findById(applicationCode)
    			.orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with application code: " + applicationCode));
    	
    	List<VehiclePartsConditionFinal> partsList = vehPartsConditionRepo.findByApplicationCode(vehFinal);
    
        VehicleDetailsDTO dto = toVehicleDetailsDTO(vehFinal, partsList);
        
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(Collections.singletonList(dto));
        
        try {
            InputStream reportStream = this.getClass().getResourceAsStream(VEHICLE_DETAILS_REPORT_PATH);
            if (reportStream == null) {
                log.error("Main report template not found at path: {}", VEHICLE_DETAILS_REPORT_PATH);
                throw new ReportGenerationException("Main report template not found at path: " + VEHICLE_DETAILS_REPORT_PATH, null);
            }

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportStream);
            log.info("Main report loaded successfully from .jasper file.");
            
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("SUBREPORT_DIR", "reports/");

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            JRPdfExporter pdfExporter = new JRPdfExporter();
            pdfExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            pdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));
            
            pdfExporter.exportReport();

            return byteArrayOutputStream.toByteArray();

        } catch (JRException | ReportGenerationException e) {
            log.error("Error generating JasperReport for application code: {}", applicationCode, e);
            throw new ReportGenerationException("Failed to generate report due to a JasperReports error.", e);
        }
    }


	private String[] parseLetterAndDate(String combinedString) {
	    if (combinedString == null || combinedString.trim().isEmpty() || !combinedString.contains("|")) {
	        return new String[]{combinedString, null};
	    }
	    return combinedString.split("\\|",2);
	}
	
	private VehicleDetailsDTO toVehicleDetailsDTO(VehicleFinal vehFinal, List<VehiclePartsConditionFinal> parts) 
	{
		VehicleDetailsDTO dto = new VehicleDetailsDTO();
	
        String[] dirLetter = parseLetterAndDate(vehFinal.getDirectorateLetterNodate());
        dto.setDirectorateLetterNo(dirLetter[0]);
        if (dirLetter.length > 1 && dirLetter[1] != null && !dirLetter[1].isEmpty()) {
            LocalDate localDate = LocalDate.parse(dirLetter[1]);
            dto.setDirectorateLetterDate(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }

        String[] govtLetter = parseLetterAndDate(vehFinal.getGovtLetterNoDate());
        dto.setGovtLetterNo(govtLetter[0]);
        if (govtLetter.length > 1 && govtLetter[1] != null && !govtLetter[1].isEmpty()) {
            LocalDate localDate = LocalDate.parse(govtLetter[1]);
            dto.setGovtLetterDate(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
		
        StringJoiner locationJoiner = new StringJoiner(", ");
        if(vehFinal.getPremises() != null && !vehFinal.getPremises().trim().isEmpty())
        {
        	locationJoiner.add(vehFinal.getPremises());
        }
        if(vehFinal.getLocations() != null && !vehFinal.getLocations().trim().isEmpty())
        {
        	locationJoiner.add(vehFinal.getLocations());
        }
        
        if (vehFinal.getPurchasedate() != null) {
            dto.setPurchasedate(Date.from(vehFinal.getPurchasedate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        
        dto.setFullLocation(locationJoiner.toString());
        
	      	dto.setOfficerdesignation(vehFinal.getOfficerDesignation());

	        dto.setVehicledescription(vehFinal.getVehicledescription());
	        dto.setRegistrationno(vehFinal.getRegistrationNo());
	        dto.setEngineno(vehFinal.getEngineno());
	        dto.setChassisno(vehFinal.getChassisno());
	        dto.setManufactureyear(vehFinal.getManufactureyear());
	        dto.setVehicleprice(vehFinal.getVehicleprice());
	        dto.setTotalkms(vehFinal.getTotalkms());
	        dto.setDepreciatedamount(vehFinal.getDepreciatedamount());
	        dto.setImprovements(vehFinal.getImprovements());
	        dto.setExpenses(vehFinal.getExpenses());
	        dto.setRepairexpenses(vehFinal.getRepairexpenses());
	        dto.setRepairslastsixmonths(vehFinal.getRepairslastsixmonths());
	        dto.setWhetheraccident(vehFinal.getWhetheraccident());
	        dto.setAccidentcaseresolved(vehFinal.getAccidentcaseresolved());
	        dto.setComments(vehFinal.getComments());
	        dto.setMvireportavailable(vehFinal.getMvireportavailable());
	        dto.setBattery(vehFinal.getBattery());
	        dto.setTyres(vehFinal.getTyres());
	        dto.setAccidentdamage(vehFinal.getAccidentdamage());
	        dto.setMviprice(vehFinal.getMviprice());
	        dto.setMviremarks(vehFinal.getMviremarks());
	       
	        if (vehFinal.getDepartmentCode() != null) {
	          
	            dto.setDepartmentname(vehFinal.getDepartmentCode().getDepartmentName());
	        }
	        if (vehFinal.getVehicletypecode() != null) {
	            dto.setVehicletypedescription(vehFinal.getVehicletypecode().getVehicletypedescription());
	        }
	        if (vehFinal.getVehiclemanufacturercode() != null) {
	            dto.setVehiclemanufacturername(vehFinal.getVehiclemanufacturercode().getVehicleManufacturerName());
	        }
	        if (vehFinal.getRegisteredDistrict() != null) {
	            dto.setDistrictname(vehFinal.getRegisteredDistrict().getDistrictName());
	        }
	       
	        List<VehiclePartsConditionDTO> partDTOs = parts.stream()
	            .map(part -> {
	                VehiclePartsConditionDTO partDto = new VehiclePartsConditionDTO();
	                
	                partDto.setCondition(part.getCondition());
	                
	                VehicleParts vehiclePart = part.getVehiclepartcode();
	                if (vehiclePart != null) {
	                    partDto.setVehiclepartdescription(vehiclePart.getVehiclePartDescription()); 
	                }
	                
	                return partDto;
	            })
	            .collect(Collectors.toList());
	        
	        dto.setParts(partDTOs);
	
	        return dto;
	    }
	}
