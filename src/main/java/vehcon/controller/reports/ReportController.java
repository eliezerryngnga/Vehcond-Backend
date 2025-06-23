package vehcon.controller.reports;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vehcon.exception.ReportGenerationException;
import vehcon.exception.ResourceNotFoundException;
import vehcon.models.auth.User;
import vehcon.services.reports.AllotmentLetterReportService;
import vehcon.services.reports.ReportService;
import vehcon.services.reports.VehicleDetailsReportService;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    private final ReportService reportService;
    private final VehicleDetailsReportService vehDetailsReportService;
    private final AllotmentLetterReportService allotmentLetterService;
    
    @GetMapping(value = "/vehicle-report/{applicationCode}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> getVehicleDetailsReport(@PathVariable String applicationCode, 
    		@AuthenticationPrincipal User user,
    		@RequestParam( defaultValue = "attachment") String disposition) 
    {
        try {
        	log.info("User '{}' requested vehicle details report for application code: {} with disposition '{}'", 
                    user.getUsername(), applicationCode, disposition);
        	
            byte[] reportContent = vehDetailsReportService.generateVehicleDetailsReport(applicationCode);
            
            String safeApplicationCode = applicationCode.replaceAll("[^a-zA-Z0-9.-]", "_");
            String filename = "vehicle-report-" + safeApplicationCode + ".pdf";
            
            return createSuccessResponse(reportContent, filename, MediaType.APPLICATION_PDF, disposition);
        } catch (ResourceNotFoundException e) {
        	log.warn("Cannot generate vehicle details report. Reason: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ReportGenerationException e) {
            log.error("A critical error occurred while generating vehicle details report for {}", applicationCode, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating report. Please contact support.");
        }
    }
    
    @GetMapping("/allotment-letter/{applicationCode}")
    public ResponseEntity<?> downloadAllotmentLetter(
    		@PathVariable String applicationCode )
    {
    	try
    	{
    		byte[] allotmentLetterPdf = allotmentLetterService.generateAllotmentLetterReport(applicationCode);
    		
    		 String filename = "Allotment Letter-" + applicationCode + ".pdf";
    		 
    		 return createSuccessResponse(allotmentLetterPdf, filename, MediaType.APPLICATION_PDF, "inline");
    	}
    	catch (ResourceNotFoundException e) {
            log.warn("Cannot generate allotment letter. Reason: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ReportGenerationException e) {
            log.error("A critical error occurred while generating allotment letter for {}", applicationCode, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating report. Please contact support.");
        }
    }
    
//    @GetMapping("/approved-vehicles-report")
//    public ResponseEntity<?> getApprovedVehicleReport(
//    		@RequestParam int year, 
//    		@RequestParam int month,
//    		@RequestParam( defaultValue = "pdf") String format, 
//    		@AuthenticationPrincipal User user
//    	)
//    {
//    	try
//    	{
//    		
//    		byte[] reportContent;
//    		String filename;
//    		MediaType mediaType;
//    		
//    		switch(format.toLowerCase())
//    		{
//    		
//    		case "xlsx":
//    			reportContent = reportService.generateApprovedVehicleReportAsExcel(year, month, user);
//    			mediaType = MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//    			filename = String.format("Approved-Vehicles-%d-%02d.xlsx", year, month);
//    			break;
//    		
//    		case "pdf":
//                reportContent = reportService.getApprovedVehicleReportAsPdf(year, month, user);
//                mediaType = MediaType.APPLICATION_PDF;
//                filename = String.format("Approved-Vehicles-%d-%02d.pdf", year, month);
//                break;
//            default: 
//                log.warn("Invalid format requested: {}", format);
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid format specified. Use 'pdf' or 'xlsx'.");
//            }
//            
//            return createSuccessResponse(reportContent, filename, mediaType);
//
//        } catch (ResourceNotFoundException e) {
//            log.warn("Cannot generate approved vehicles report for {}-{}. Reason: {}", year, month, e.getMessage());
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//        } catch (ReportGenerationException e) {
//            log.error("A critical error occurred while generating approved vehicles report for {}-{}", year, month, e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating report. Please contact support.");
//        }
//    }

    private ResponseEntity<byte[]> createSuccessResponse(byte[] content, String filename, MediaType mediaType, String dispositionType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        
        String finalDisposition = "inline".equalsIgnoreCase(dispositionType) ? "inline" : "attachment";
        
        ContentDisposition contentDisposition = ContentDisposition.builder(finalDisposition)
                .filename(filename)
                .build();
        headers.setContentDisposition(contentDisposition);
        headers.setContentLength(content.length);
        
        log.info("Successfully generated report '{}' with disposition '{}'", filename, content.length);
        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }
}