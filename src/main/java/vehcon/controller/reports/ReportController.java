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
    
    
    @GetMapping("/approved-vehicles-report/pdf")
    public ResponseEntity<?> getApprovedVehicleReportAsPdf(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @AuthenticationPrincipal User user) {
        
        try {
            log.info("User '{}' requesting Approved Vehicles PDF for {}/{}", user.getUsername(), year, month);
            byte[] reportContent = reportService.generateApprovedVehicleReportAsPdf(year, month, user);
            
            String dateString = (month != null) ? String.format("%d-%02d", year, month) : String.valueOf(year);
            String filename = String.format("Approved-Vehicles-%s.pdf", dateString);

            return createSuccessResponse(reportContent, filename, MediaType.APPLICATION_PDF, "attachment");

        } catch (ResourceNotFoundException e) {
            log.warn("Cannot generate Approved Vehicles PDF for {}-{}. Reason: {}", year, month, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error generating Approved Vehicles PDF for {}-{}", year, month, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating report.");
        }
    }

    // --- Endpoint for Approved Vehicles XLSX ---
    @GetMapping("/approved-vehicles/xlsx")
    public ResponseEntity<?> getApprovedVehicleReportAsExcel(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @AuthenticationPrincipal User user) {
        
        try {
            log.info("User '{}' requesting Approved Vehicles Excel for {}/{}", user.getUsername(), year, month);
            byte[] reportContent = reportService.generateApprovedVehicleReportAsExcel(year, month, user);

            String dateString = (month != null) ? String.format("%d-%02d", year, month) : String.valueOf(year);
            String filename = String.format("Approved-Vehicles-%s.xlsx", dateString);
            MediaType mediaType = MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            return createSuccessResponse(reportContent, filename, mediaType, "attachment");

        } catch (ResourceNotFoundException e) {
            log.warn("Cannot generate Approved Vehicles Excel for {}-{}. Reason: {}", year, month, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error generating Approved Vehicles Excel for {}-{}", year, month, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating report.");
        }
    }
    
    @GetMapping("/condemn-vehicles-report/pdf")
    public ResponseEntity<?> getCondemnVehiclesReportAsPdf(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @AuthenticationPrincipal User user) {
        
        try {
            log.info("User '{}' requesting Vehicles Recommended for Condemnation PDF for {}/{}", user.getUsername(), year, month);
            byte[] reportContent = reportService.generatePlacedBeforeVCCReportAsPdf(year, month, user);
            
            String dateString = (month != null) ? String.format("%d-%02d", year, month) : String.valueOf(year);
            String filename = String.format("Condem-Vehicles-%s.pdf", dateString);

            return createSuccessResponse(reportContent, filename, MediaType.APPLICATION_PDF, "attachment");

        } catch (ResourceNotFoundException e) {
            log.warn("Cannot generate Condem Vehicles PDF for {}-{}. Reason: {}", year, month, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error generating Condem Vehicles PDF for {}-{}", year, month, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating report.");
        }
    }

    // --- Endpoint for Approved Vehicles XLSX ---
    @GetMapping("/condemn-vehicles-report/xlsx")
    public ResponseEntity<?> getCondemnVehiclesReportAsExcel(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @AuthenticationPrincipal User user) {
        
        try {
            log.info("User '{}' requesting Recommended for Condemnation Excel for {}/{}", user.getUsername(), year, month);
            byte[] reportContent = reportService.generatePlacedBeforeVCCReportAsExcel(year, month, user);

            String dateString = (month != null) ? String.format("%d-%02d", year, month) : String.valueOf(year);
            String filename = String.format("Condem-Vehicles-%s.xlsx", dateString);
            MediaType mediaType = MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            return createSuccessResponse(reportContent, filename, mediaType, "attachment");

        } catch (ResourceNotFoundException e) {
            log.warn("Cannot generate Condem Vehicles Excel for {}-{}. Reason: {}", year, month, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error generating Condem Vehicles Excel for {}-{}", year, month, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating report.");
        }
    }
    
    @GetMapping("/circulated-vehicles-report/pdf")
    public ResponseEntity<?> getCirculatedVehiclesReportAsPdf(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @AuthenticationPrincipal User user) {
        
        try {
            log.info("User '{}' requesting Vehicles in Circulation List PDF for {}/{}", user.getUsername(), year, month);
            byte[] reportContent = reportService.generatePriceFixedByTCReportAsPdf(year, month, user);
            
            String dateString = (month != null) ? String.format("%d-%02d", year, month) : String.valueOf(year);
            String filename = String.format("Circulated-Vehicles-%s.pdf", dateString);

            return createSuccessResponse(reportContent, filename, MediaType.APPLICATION_PDF, "attachment");

        } catch (ResourceNotFoundException e) {
            log.warn("Cannot generate Circulated Vehicles PDF for {}-{}. Reason: {}", year, month, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error generating Circulated Vehicles PDF for {}-{}", year, month, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating report.");
        }
    }

    // --- Endpoint for Approved Vehicles XLSX ---
    @GetMapping("/circulated-vehicles-report/xlsx")
    public ResponseEntity<?> getCirculatedVehiclesReportAsExcel(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @AuthenticationPrincipal User user) {
        
        try {
            log.info("User '{}' requesting Recommended for Condemnation Excel for {}/{}", user.getUsername(), year, month);
            byte[] reportContent = reportService.generatePriceFixedByTCReportAsExcel(year, month, user);

            String dateString = (month != null) ? String.format("%d-%02d", year, month) : String.valueOf(year);
            String filename = String.format("Circulated-Vehicles-%s.xlsx", dateString);
            MediaType mediaType = MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            return createSuccessResponse(reportContent, filename, mediaType, "attachment");

        } catch (ResourceNotFoundException e) {
            log.warn("Cannot generate Circulated Vehicles Excel for {}-{}. Reason: {}", year, month, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error generating Circulated Vehicles Excel for {}-{}", year, month, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating report.");
        }
    }
    
    @GetMapping("allotted-vehicles-report/pdf")
    public ResponseEntity<?> getAllottedVehiclesReportAsPdf(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @AuthenticationPrincipal User user) {
        
        try {
            log.info("User '{}' requesting Allotted Vehicles PDF for {}/{}", user.getUsername(), year, month);
            byte[] reportContent = reportService.generateAllottedVehicleReportAsPdf(year, month, user);
            
            String dateString = (month != null) ? String.format("%d-%02d", year, month) : String.valueOf(year);
            String filename = String.format("Allotted-Vehicles-%s.pdf", dateString);

            return createSuccessResponse(reportContent, filename, MediaType.APPLICATION_PDF, "attachment");

        } catch (ResourceNotFoundException e) {
            log.warn("Cannot generate Allotted Vehicles PDF for {}-{}. Reason: {}", year, month, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error generating Allotted Vehicles PDF for {}-{}", year, month, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating report.");
        }
    }

    // --- Endpoint for Approved Vehicles XLSX ---
    @GetMapping("allotted-vehicles-report/xlsx")
    public ResponseEntity<?> getAllottedVehiclesReportAsExcel(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @AuthenticationPrincipal User user) {
        
        try {
            log.info("User '{}' requesting Allotted Vehicles Excel for {}/{}", user.getUsername(), year, month);
            byte[] reportContent = reportService.generateAllottedVehicleReportAsExcel(year, month, user);

            String dateString = (month != null) ? String.format("%d-%02d", year, month) : String.valueOf(year);
            String filename = String.format("Allotted-Vehicles-%s.xlsx", dateString);
            MediaType mediaType = MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            return createSuccessResponse(reportContent, filename, mediaType, "attachment");

        } catch (ResourceNotFoundException e) {
            log.warn("Cannot generate Allotted Vehicles Excel for {}-{}. Reason: {}", year, month, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error generating Circulated Vehicles Excel for {}-{}", year, month, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating report.");
        }
    }
    
    @GetMapping("/tendered-vehicles-report/pdf")
    public ResponseEntity<?> getTenderdVehiclesReportAsPdf(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @AuthenticationPrincipal User user) {
        
        try {
            log.info("User '{}' requesting Tendered Vehicles PDF for {}/{}", user.getUsername(), year, month);
            byte[] reportContent = reportService.generateTenderedVehicleReportAsPdf(year, month, user);
            
            String dateString = (month != null) ? String.format("%d-%02d", year, month) : String.valueOf(year);
            String filename = String.format("Tendered-Vehicles-%s.pdf", dateString);

            return createSuccessResponse(reportContent, filename, MediaType.APPLICATION_PDF, "attachment");

        } catch (ResourceNotFoundException e) {
            log.warn("Cannot generate Tendered Vehicles PDF for {}-{}. Reason: {}", year, month, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error generating Tendered Vehicles PDF for {}-{}", year, month, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating report.");
        }
    }

    // --- Endpoint for Approved Vehicles XLSX ---
    @GetMapping("/tendered-vehicles-report/xlsx")
    public ResponseEntity<?> getTenderedVehiclesReportAsExcel(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @AuthenticationPrincipal User user) {
        
        try {
            log.info("User '{}' requesting Tendered Vehicles Excel for {}/{}", user.getUsername(), year, month);
            byte[] reportContent = reportService.generateTenderedVehicleReportAsExcel(year, month, user);

            String dateString = (month != null) ? String.format("%d-%02d", year, month) : String.valueOf(year);
            String filename = String.format("Tendered-Vehicles-%s.xlsx", dateString);
            MediaType mediaType = MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            return createSuccessResponse(reportContent, filename, mediaType, "attachment");

        } catch (ResourceNotFoundException e) {
            log.warn("Cannot generate Tendered Vehicles Excel for {}-{}. Reason: {}", year, month, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error generating Tendered Vehicles Excel for {}-{}", year, month, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating report.");
        }
    }
    
    @GetMapping("/lifted-vehicles-report/pdf")
    public ResponseEntity<?> getLiftedVehiclesReportAsPdf(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @AuthenticationPrincipal User user) {
        
        try {
            log.info("User '{}' requesting Lifted Vehicles PDF for {}/{}", user.getUsername(), year, month);
            byte[] reportContent = reportService.generateLiftedVehicleReportAsPdf(year, month, user);
            
            String dateString = (month != null) ? String.format("%d-%02d", year, month) : String.valueOf(year);
            String filename = String.format("Lifted-Vehicles-%s.pdf", dateString);

            return createSuccessResponse(reportContent, filename, MediaType.APPLICATION_PDF, "attachment");

        } catch (ResourceNotFoundException e) {
            log.warn("Cannot generate Lifted Vehicles PDF for {}-{}. Reason: {}", year, month, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error generating Lifted Vehicles PDF for {}-{}", year, month, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating report.");
        }
    }

    @GetMapping("/lifted-vehicles-report/xlsx")
    public ResponseEntity<?> getLiftedVehiclesReportAsExcel(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @AuthenticationPrincipal User user) {
        
        try {
            log.info("User '{}' requesting Lifted Vehicles Excel for {}/{}", user.getUsername(), year, month);
            byte[] reportContent = reportService.generateLiftedVehicleReportAsExcel(year, month, user);

            String dateString = (month != null) ? String.format("%d-%02d", year, month) : String.valueOf(year);
            String filename = String.format("Lifted-Vehicles-%s.xlsx", dateString);
            MediaType mediaType = MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            return createSuccessResponse(reportContent, filename, mediaType, "attachment");

        } catch (ResourceNotFoundException e) {
            log.warn("Cannot generate Lifted Vehicles Excel for {}-{}. Reason: {}", year, month, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error generating Lifted Vehicles Excel for {}-{}", year, month, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating report.");
        }
    }
    
    @GetMapping("/scrapped-vehicles-report/pdf")
    public ResponseEntity<?> getScrappedVehiclesReportAsPdf(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @AuthenticationPrincipal User user) {
        
        try {
            log.info("User '{}' requesting Scrapped Vehicles PDF for {}/{}", user.getUsername(), year, month);
            byte[] reportContent = reportService.generateScrappedVehicleReportAsPdf(year, month, user);
            
            String dateString = (month != null) ? String.format("%d-%02d", year, month) : String.valueOf(year);
            String filename = String.format("Scrapped-Vehicles-%s.pdf", dateString);

            return createSuccessResponse(reportContent, filename, MediaType.APPLICATION_PDF, "attachment");

        } catch (ResourceNotFoundException e) {
            log.warn("Cannot generate Scrapped Vehicles PDF for {}-{}. Reason: {}", year, month, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error generating Lifted Vehicles PDF for {}-{}", year, month, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating report.");
        }
    }

    @GetMapping("/scrapped-vehicles-report/xlsx")
    public ResponseEntity<?> getScrappedVehiclesReportAsExcel(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @AuthenticationPrincipal User user) {
        
        try {
            log.info("User '{}' requesting Scrapped Vehicles Excel for {}/{}", user.getUsername(), year, month);
            byte[] reportContent = reportService.generateScrappedVehicleReportAsExcel(year, month, user);

            String dateString = (month != null) ? String.format("%d-%02d", year, month) : String.valueOf(year);
            String filename = String.format("Scrapped-Vehicles-%s.xlsx", dateString);
            MediaType mediaType = MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            return createSuccessResponse(reportContent, filename, mediaType, "attachment");

        } catch (ResourceNotFoundException e) {
            log.warn("Cannot generate Scrapped Vehicles Excel for {}-{}. Reason: {}", year, month, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error generating Scrapped Vehicles Excel for {}-{}", year, month, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating report.");
        }
    }
    
    @GetMapping("/non-lifted-vehicles-report/pdf")
    public ResponseEntity<?> getNonLiftedVehiclesReportAsPdf(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @AuthenticationPrincipal User user) {
        
        try {
            log.info("User '{}' requesting Non Lifted Vehicles PDF for {}/{}", user.getUsername(), year, month);
            byte[] reportContent = reportService.generateNonLiftedVehicleReportAsPdf(year, month, user);
            
            String dateString = (month != null) ? String.format("%d-%02d", year, month) : String.valueOf(year);
            String filename = String.format("Non-Lifted-Vehicles-%s.pdf", dateString);

            return createSuccessResponse(reportContent, filename, MediaType.APPLICATION_PDF, "attachment");

        } catch (ResourceNotFoundException e) {
            log.warn("Cannot generate Non Lifted Vehicles PDF for {}-{}. Reason: {}", year, month, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error generating Non Lifted Vehicles PDF for {}-{}", year, month, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating report.");
        }
    }

    @GetMapping("/non-lifted-vehicles-report/xlsx")
    public ResponseEntity<?> getNonLiftedVehiclesReportAsExcel(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @AuthenticationPrincipal User user) {
        
        try {
            log.info("User '{}' requesting Non Lifted Vehicles Excel for {}/{}", user.getUsername(), year, month);
            byte[] reportContent = reportService.generateNonLiftedVehicleReportAsExcel(year, month, user);

            String dateString = (month != null) ? String.format("%d-%02d", year, month) : String.valueOf(year);
            String filename = String.format("Non-Lifted-Vehicles-%s.xlsx", dateString);
            MediaType mediaType = MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            return createSuccessResponse(reportContent, filename, mediaType, "attachment");

        } catch (ResourceNotFoundException e) {
            log.warn("Cannot generate Non Lifted Vehicles Excel for {}-{}. Reason: {}", year, month, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error generating Non Lifted Vehicles Excel for {}-{}", year, month, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating report.");
        }
    }
   
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