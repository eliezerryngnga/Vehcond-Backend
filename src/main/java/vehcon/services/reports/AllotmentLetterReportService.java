package vehcon.services.reports;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import vehcon.dto.reports.AllotmentLetterDTO;
import vehcon.exception.ReportGenerationException;
import vehcon.exception.ResourceNotFoundException;
import vehcon.models.appdata.AllottedVehicle;
import vehcon.models.appdata.Vcctc;
import vehcon.models.appdata.VehicleFinal;
import vehcon.repo.appdata.AllottedVehiclesRepository;
import vehcon.repo.appdata.VccTcRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class AllotmentLetterReportService {

	private static final String ALLOTMENT_LETTER_REPORT_PATH = "/reports/allotmentLetter.jrxml";
	
	private final AllottedVehiclesRepository allottedVehicleRepo;
	private final VccTcRepository vcctcRepo;
	
	public byte[] generateAllotmentLetterReport(String applicationCode) 
    {
    	
    	AllottedVehicle allottedVehicle = allottedVehicleRepo.findDetailsByApplicationCode(applicationCode)
    			.orElseThrow(() -> new ResourceNotFoundException("Allotment details not found for application code: " + applicationCode));
    	
    	VehicleFinal vehFinal = allottedVehicle.getVehicleFinal();
    	
    	Vcctc vcctc = vcctcRepo.findByApplicationCode(applicationCode)
    			.orElseThrow(() -> new ResourceNotFoundException("VCCTC details not found for application code: " + applicationCode));
    	
    	AllotmentLetterDTO dto = toAllotmentLetterDTO(allottedVehicle, vcctc, vehFinal);
    	
    	JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(List.of(dto));
    	try {
            // --- 3. Compile the Main Report Template ---
            JasperReport mainReport;
            try (InputStream reportStream = this.getClass().getResourceAsStream(ALLOTMENT_LETTER_REPORT_PATH)) {
                if (reportStream == null) {
                    throw new ReportGenerationException("Main report template not found at path: " + ALLOTMENT_LETTER_REPORT_PATH);
                }
                mainReport = JasperCompileManager.compileReport(reportStream);
            } catch (Exception e) {
                log.error("Failed to compile main report template: {}", ALLOTMENT_LETTER_REPORT_PATH, e);
                throw new ReportGenerationException("Could not compile main report template: " + ALLOTMENT_LETTER_REPORT_PATH, e);
            }

            // --- 4. Prepare Parameters and Fill Report ---
            // This report doesn't use parameters, but an empty map is still needed.
            Map<String, Object> parameters = new HashMap<>();

            JasperPrint jasperPrint = JasperFillManager.fillReport(mainReport, parameters, dataSource);

            // --- 5. Export Report to PDF ---
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            JRPdfExporter pdfExporter = new JRPdfExporter();
            pdfExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            pdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));
            
            pdfExporter.exportReport();

            return byteArrayOutputStream.toByteArray();

        } catch (JRException | ReportGenerationException e) {
            log.error("Error generating allotment letter for application code: {}", applicationCode, e);
            throw new ReportGenerationException("Failed to generate allotment letter report due to a JasperReports error.", e);
        }
    }
	
    private AllotmentLetterDTO toAllotmentLetterDTO(AllottedVehicle allottedVehicle, Vcctc vcctc, VehicleFinal vehFinal)
    {
    	AllotmentLetterDTO dto = new AllotmentLetterDTO();
    	
    	dto.setAllotteesaddress(allottedVehicle.getAllotteesAddress());
    	dto.setAllotteesname(allottedVehicle.getAllotteesName());
    	
    	if(vehFinal.getDepartmentCode() != null)
    	{
    		dto.setDepartmentname(vehFinal.getDepartmentCode().getDepartmentName());    		
    	}
    	
    	dto.setLocations(vehFinal.getLocations());
    	dto.setOfficename(vehFinal.getOfficeName());
    	dto.setRegistrationno(vehFinal.getRegistrationNo());
    	dto.setVcctcprice(vcctc.getVehiclePrice());
    	
    	dto.setLetterNumber("");
    	String letterNoDate = allottedVehicle.getLetternodate();
    	
    	if (letterNoDate != null && letterNoDate.contains("|")) 
    	{
            String[] letterParts = allottedVehicle.getLetternodate().split("\\|", 2);
            
            dto.setLetterNumber(letterParts[0] != null ? letterParts[0].trim() : "");
            
            if (letterParts.length > 1 && letterParts[1] != null && !letterParts[1].trim().isEmpty()) {
                try {
                	
                	LocalDate parsedDate = LocalDate.parse(letterParts[1].trim());
                	
//                	DateTimeFormatter reportFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//                
//                    dto.setLetterDate(parsedDate.format(reportFormatter));
                	dto.setLetterDate(parsedDate);
                } catch (DateTimeParseException e) {
                    log.warn("Could not parse government forwarding letter date: '{}'. Invalid format.", letterParts[1], e);
                }
            }
        }
    	
    	return dto;
    }
   
}
