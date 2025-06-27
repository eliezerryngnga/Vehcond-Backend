package vehcon.services.reports;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

	private static final String ALLOTMENT_LETTER_REPORT_PATH = "/reports/allotmentLetter.jasper";
	
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
    		InputStream reportStream = this.getClass().getResourceAsStream(ALLOTMENT_LETTER_REPORT_PATH);
            if (reportStream == null) {
            	log.error("Allotment Letter template not found at path: {}", ALLOTMENT_LETTER_REPORT_PATH);
            	throw new ReportGenerationException("Allotment Letter template not found at path: " + ALLOTMENT_LETTER_REPORT_PATH);
            }
            
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportStream);
            log.info("Allotment Letter loaded successfully from .jasper file.");

            Map<String, Object> parameters = new HashMap<>();

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

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
	
	private AllotmentLetterDTO toAllotmentLetterDTO(AllottedVehicle allottedVehicle, Vcctc vcctc, VehicleFinal vehFinal) {
	    AllotmentLetterDTO dto = new AllotmentLetterDTO();

	    String registrationNo = vehFinal.getRegistrationNo();
	    String officeName = vehFinal.getOfficeName();
	    String locations = vehFinal.getLocations() != null ? vehFinal.getLocations().replace("|", ", ") : "";
	    String allotteeName = allottedVehicle.getAllotteesName();
	    String allotteeAddress = allottedVehicle.getAllotteesAddress();
	    Integer price = vcctc.getVehiclePrice();
	    String departmentName = (vehFinal.getDepartmentCode() != null) ? vehFinal.getDepartmentCode().getDepartmentName() : "";
	    String letterNoDate = allottedVehicle.getLetternodate();

	    String letterNumber = "";
	    if (letterNoDate != null && letterNoDate.contains("|")) {
	        String[] letterParts = letterNoDate.split("\\|", 2);
	        letterNumber = (letterParts.length > 0 && letterParts[0] != null) ? letterParts[0].trim() : "";
	        
	        if (letterParts.length > 1 && letterParts[1] != null && !letterParts[1].trim().isEmpty()) {
	            
	            // --- THE FIX IS HERE ---
	            String dateString = letterParts[1].trim();
	            LocalDate parsedDate = null; // Initialize to null
	            
	            // Define formatters for the patterns you want to support
	            DateTimeFormatter dmyFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	            DateTimeFormatter ymdFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	            try {
	                // Try the first format
	                parsedDate = LocalDate.parse(dateString, dmyFormatter);
	            } catch (DateTimeParseException e1) {
	                // If it fails, try the second format
	                try {
	                    parsedDate = LocalDate.parse(dateString, ymdFormatter);
	                } catch (DateTimeParseException e2) {
	                    // If both fail, log an error
	                    log.error("Could not parse date string '{}' with any known format for application code {}", 
	                              dateString, vehFinal.getApplicationCode());
	                }
	            }
	            
	            // Proceed only if parsing was successful
	            if (parsedDate != null) {
	                Date utilDate = Date.from(parsedDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	                dto.setLetterDate(utilDate);
	                
	                // Use a different formatter for the desired output format "dd-MMM-yyyy"
	                SimpleDateFormat memoDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
	                dto.setMemoDateFormatted("'Dated :' " + memoDateFormat.format(utilDate));
	            }
	        }
	    }
	    dto.setLetterNumberFormatted("No. " + letterNumber);

	    dto.setMainParagraph(String.format(
	        "A Government condemned vehicle <b>No. : %s</b> belonging to %s department located at %s is allotted to <b>%s</b> for his/her personal use on the following conditions.",
	        registrationNo, officeName, locations, allotteeName
	    ));
	    
	    NumberFormat numberFormatter = NumberFormat.getNumberInstance(Locale.US);
	    String formattedPrice = (price != null) ? numberFormatter.format(price) : "0";
	    dto.setPriceParagraph(String.format(
	        "The Vehicle will be released to him/her by the Department/Office concerned on payment of the amount of <b>` %s/-</b> being the Upset price of the vehicle fixed by the Vehicle Condemnation Committee minus 10%% approved by Government.",
	        formattedPrice
	    ));
	    
	    dto.setLiftingParagraph(
	        "The Vehicle should be lifted within <b>1 (one) month</b> from the date of receipt of the allotment orders and if they fail to do so the allotment will stand automatically cancelled. No application for reduction of price or for extending the time of lifting will be entertained."
	    );

	    dto.setUnderSecretaryLine("Under Secretary to the Govt. of Meghalaya,\nTransport Department");
	    dto.setMemoNumberFormatted("Memo. No. " + letterNumber + " (A)");
	    dto.setMemoRecipient1("Under Secretary, " + departmentName);
	    dto.setMemoRecipient2(officeName);
	    dto.setMemoRecipient3Address(allotteeAddress);
	    dto.setAllotteesname(allotteeName);
	    
	    return dto;
	}
}