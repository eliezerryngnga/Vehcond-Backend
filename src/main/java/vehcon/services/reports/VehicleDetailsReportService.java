package vehcon.services.reports;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
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
	private static final String VEHICLE_DETAILS_REPORT_PATH = "/reports/vehicleDetails.jrxml";
	private static final String VEHICLE_DETAILS_SUBREPORT_PATH = "/reports/vehicleDetails_subreport1.jrxml"; 
	
	private static final String VEHICLE_SUBREPORT_PARAMETER = "VehicleDetailsSubreport";
	
	public byte[] generateVehicleDetailsReport(String applicationCode) {
		
    	VehicleFinal vehFinal = vehFinalRepo.findById(applicationCode)
    			.orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with application code: " + applicationCode));
    	
    	List<VehiclePartsConditionFinal> partsList = vehPartsConditionRepo.findByApplicationCode(vehFinal);
    
        VehicleDetailsDTO dto = toVehicleDetailsDTO(vehFinal, partsList);
        
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(Collections.singletonList(dto));
        
        try {
        	
            JasperReport subreport;
            try(InputStream subreportStream = this.getClass().getResourceAsStream(VEHICLE_DETAILS_SUBREPORT_PATH))
            {
            	if(subreportStream == null)
            	{
            		throw new ReportGenerationException("Subreport template not found at path: " + VEHICLE_DETAILS_SUBREPORT_PATH, null);
            	}
            	
            	 subreport = JasperCompileManager.compileReport(subreportStream);
            }
            catch(Exception e)
            {
            	log.error("Failed to compile subreport template: {}", VEHICLE_DETAILS_SUBREPORT_PATH, e);
                throw new ReportGenerationException("Could not compile subreport template: " + VEHICLE_DETAILS_SUBREPORT_PATH, e);
            }
            
            JasperReport mainReport;
            try (InputStream reportStream = this.getClass().getResourceAsStream(VEHICLE_DETAILS_REPORT_PATH)) {
                if (reportStream == null) {
                    throw new ReportGenerationException("Main report template not found at path: " + VEHICLE_DETAILS_REPORT_PATH, null);
                }
                mainReport = JasperCompileManager.compileReport(reportStream);
                
            } catch (Exception e) {
                log.error("Failed to compile main report template: {}", VEHICLE_DETAILS_REPORT_PATH, e);
                throw new ReportGenerationException("Could not compile main report template: " + VEHICLE_DETAILS_REPORT_PATH, e);
            }
            
            // --- 5. Prepare Parameters and Fill Report ---
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("applicationcode", applicationCode);
            parameters.put(VEHICLE_SUBREPORT_PARAMETER, subreport); // Pass compiled subreport to main report

            JasperPrint jasperPrint = JasperFillManager.fillReport(mainReport, parameters, dataSource);
            
            // --- 6. Export Report to PDF (Inlined Logic) ---
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
	    if (combinedString == null || combinedString.trim().isEmpty()) {
	        return new String[]{null, null};
	    }
	    String trimmedInput = combinedString.trim();
	    int pipeIndex = trimmedInput.indexOf('|');
	
	    if (pipeIndex == -1) {
	        return new String[]{trimmedInput, null};
	    }
	
	    String letterNoPart = trimmedInput.substring(0, pipeIndex).trim();
	    String potentialDatePart = trimmedInput.substring(pipeIndex + 1).trim();
	
	    try {
	        // CORRECTED: No formatter is needed for the standard YYYY-MM-DD format.
	        LocalDate.parse(potentialDatePart);
	        return new String[]{letterNoPart, potentialDatePart};
	    } catch (DateTimeParseException e) {
	        log.warn("Could not parse date '{}' from combined string '{}'. It was not in YYYY-MM-DD format.", potentialDatePart, trimmedInput);
	        return new String[]{trimmedInput, null};
	    }
	}
	
	private VehicleDetailsDTO toVehicleDetailsDTO(VehicleFinal vehFinal, List<VehiclePartsConditionFinal> parts) 
	{
		VehicleDetailsDTO dto = new VehicleDetailsDTO();
		
		 String[] dirLetter = parseLetterAndDate(vehFinal.getDirectorateLetterNodate());
		 dto.setDirectorateLetterNo(dirLetter[0]);
	     dto.setDirectorateLetterDate(dirLetter[1] != null ? LocalDate.parse(dirLetter[1]) : null);
	
	        // --- Split Government Letter No and Date ---
	        String[] govtLetter = parseLetterAndDate(vehFinal.getGovtLetterNoDate());
	        dto.setGovtLetterNo(govtLetter[0]);
	        dto.setGovtLetterDate(govtLetter[1] != null ? LocalDate.parse(govtLetter[1]) : null);
	
	      	dto.setOfficerdesignation(vehFinal.getOfficerDesignation());
	        dto.setPremises(vehFinal.getPremises());
	        dto.setLocations(vehFinal.getLocations());
	        
	        dto.setVehicledescription(vehFinal.getVehicledescription());
	        dto.setRegistrationno(vehFinal.getRegistrationNo());
	        dto.setEngineno(vehFinal.getEngineno());
	        dto.setChassisno(vehFinal.getChassisno());
	        dto.setManufactureyear(vehFinal.getManufactureyear());
	        dto.setPurchasedate(vehFinal.getPurchasedate());
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
