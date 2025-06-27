package vehcon.services.reports;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import net.sf.jasperreports.pdf.JRPdfExporter;
import vehcon.common.data.util.VehicleSpecifications;
import vehcon.dto.reports.VehicleReportsDTO;
import vehcon.exception.ReportGenerationException;
import vehcon.models.appdata.AllottedVehicle;
import vehcon.models.appdata.LiftedVehicles;
import vehcon.models.appdata.Scrap;
import vehcon.models.appdata.TenderVehicles;
import vehcon.models.appdata.Vcctc;
import vehcon.models.appdata.VcctcTemp;
import vehcon.models.appdata.VehicleFinal;
import vehcon.models.auth.User;
import vehcon.repo.appdata.AllottedVehiclesRepository;
import vehcon.repo.appdata.LiftedVehiclesRepository;
import vehcon.repo.appdata.ScrapRepository;
import vehcon.repo.appdata.TenderVehiclesRepository;
import vehcon.repo.appdata.VccTcRepository;
import vehcon.repo.appdata.VcctcTempRepository;
import vehcon.repo.appdata.VehicleFinalRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {

	private final VehicleFinalRepository vehFinalRepo;
	private final AllottedVehiclesRepository allottedVehicleRepo;
	private final VccTcRepository vcctcRepo;
	private final VcctcTempRepository vcctcTempRepo;
	private final ScrapRepository scrapRepo;
	private final TenderVehiclesRepository tenderRepo;
	private final LiftedVehiclesRepository liftedRepo;
	
	private enum ReportFormat {
		PDF, 
		XLSX
	}
	
//	PDF
    public byte[] generateApprovedVehicleReportAsPdf(Integer year, Integer month, User user) 
    {
    	return generateMonthlyReport(2, year, month, ReportFormat.PDF, user, "approved");
    } 
    
    public byte[] generateApprovedVehicleReportAsExcel(Integer year, Integer month, User user) 
    {
    	return generateMonthlyReport(2, year, month, ReportFormat.XLSX, user, "approved");
    }
    
    public byte[] generatePlacedBeforeVCCReportAsPdf(Integer year, Integer month, User user) 
    {
    	return generateMonthlyReport(3, year, month, ReportFormat.PDF, user, "vcctcTemp");
    }
    
    public byte[] generatePlacedBeforeVCCReportAsExcel(Integer year, Integer month, User user) 
    {
    	return generateMonthlyReport(3, year, month, ReportFormat.XLSX, user, "vcctcTemp");
    }
    
//    Price Fixed By TC
    public byte[] generatePriceFixedByTCReportAsPdf(Integer year, Integer month, User user) 
    {
    	return generateMonthlyReport(5 , year, month, ReportFormat.PDF, user, "vcctc");
    }
    
    public byte[] generatePriceFixedByTCReportAsExcel(Integer year, Integer month, User user) 
    {
    	return generateMonthlyReport(5, year, month, ReportFormat.XLSX, user, "vcctc");
    }
    
//    Allotted 
    public byte[] generateAllottedVehicleReportAsPdf(Integer year, Integer month, User user) 
    {
    	return generateMonthlyReport(7,year, month, ReportFormat.PDF, user, "allotted");
    	
    }
    
    public byte[] generateAllottedVehicleReportAsExcel(Integer year, Integer month, User user) 
    {
    	return generateMonthlyReport(7,year, month, ReportFormat.XLSX, user,"allotted");
    }
    
//    Tendered
    public byte[] generateTenderedVehicleReportAsPdf(Integer year, Integer month, User user) 
    {
    	return generateMonthlyReport(11,year, month, ReportFormat.PDF, user, "tendered");
    	
    }
    
    public byte[] generateTenderedVehicleReportAsExcel(Integer year, Integer month, User user) 
    {
    	return generateMonthlyReport(11,year, month, ReportFormat.XLSX, user, "tendered");
    }
//    Lifted
    public byte[] generateLiftedVehicleReportAsPdf(Integer year, Integer month, User user) 
    {
    	return generateMonthlyReport(9,year, month, ReportFormat.PDF, user, "lifted");
    	
    }
    
    public byte[] generateLiftedVehicleReportAsExcel(Integer year, Integer month, User user) 
    {
    	return generateMonthlyReport(9,year, month, ReportFormat.XLSX, user, "lifted");
    }
//    Scrapped
    public byte[] generateScrappedVehicleReportAsPdf(Integer year, Integer month, User user) 
    {
    	return generateMonthlyReport(-2,year, month, ReportFormat.PDF, user, "scrapped");
    	
    }
    
    public byte[] generateScrappedVehicleReportAsExcel(Integer year, Integer month, User user) 
    {
    	return generateMonthlyReport(-2,year, month, ReportFormat.XLSX, user, "scrapped");
    }
//    Non Lifted
    public byte[] generateNonLiftedVehicleReportAsPdf(Integer year, Integer month, User user) 
    {
    	return generateMonthlyReport(10,year, month, ReportFormat.PDF, user, "nonLifted");
    	
    }
    
    public byte[] generateNonLiftedVehicleReportAsExcel(Integer year, Integer month, User user) 
    {
    	return generateMonthlyReport(10,year, month, ReportFormat.XLSX, user, "nonLifted");
    }
    
    private byte[] generateMonthlyReport(int processCode, Integer year, Integer month, ReportFormat format, User user, String reportType ) {
        
        String reportPath = getReportPathForProcessCode(processCode);

        List<VehicleReportsDTO> dtoList = getMonthlyVehicleReportData(processCode, year, month, user, reportType);

        JRBeanCollectionDataSource mainDataSource = new JRBeanCollectionDataSource(dtoList);
        
        Map<String, Object> parameters = new HashMap<>();
//        parameters.put("VehicleDataset", dtoList);
        return generateReport(reportPath, parameters, mainDataSource, format);
    }
    
    /**
     * Fetches and maps the vehicle data based on the provided criteria.
     */
    private List<VehicleReportsDTO> getMonthlyVehicleReportData(int processCode, Integer year, Integer month, User user, String reportType) {
        
    	Specification<VehicleFinal> spec = Specification
            .where(VehicleSpecifications.hasProcessCode(processCode));
            
            switch(reportType)
            {
//            case "approved":
//            	spec = spec.and(VehicleSpecifications.hasVerificationDateIn(year, month))
//            	break;
            case "allotted":
            	spec = spec.and(VehicleSpecifications.hasAllottedVehiclesDateIn(year, month));
            	break;
            case "approved":
            	spec = spec.and(VehicleSpecifications.hasVerificationDateIn(year, month));
            	break;
            case "lifted":
            	spec = spec.and(VehicleSpecifications.hasLiftedVehiclesDateIn(year, month));
            	break;
            case "scrapped":
            	spec = spec.and(VehicleSpecifications.hasScrapperDateIn(year, month));
            	break;
            case "tendered":
            	spec = spec.and(VehicleSpecifications.hasTenderedDateIn(year, month));
            break;
            case "vcctc":
            	spec = spec.and(VehicleSpecifications.hasVcctcDateIn(year, month));
            	break;
            case "vcctcTemp":
            	spec = spec.and(VehicleSpecifications.hasVcctcTempDateIn(year, month));
            	break;
        
//            case "nonLifted":
//            	spec = spec.and(VehicleSpecifications.hasNonLiftedVehiclesDateIn(year, month));
//            
            default:
            	throw new IllegalArgumentException("Unknown report type: " + reportType);
            }

        if (!userHasAnyRole(user, "ADMIN", "TD")) {
            Integer deptCode = user.getDepartment() != null ? user.getDepartment().getDepartmentCode() : null;
            if (deptCode == null) {
                throw new AccessDeniedException("User does not have a department assigned.");
            }
            spec = spec.and(VehicleSpecifications.isInDepartment(deptCode));
        }

        List<VehicleFinal> vehicles = vehFinalRepo.findAll(spec);
        log.info("Found {} vehicle records in the database for the report.", vehicles.size());
        
        if (vehicles.isEmpty()) {
        	log.warn("No vehicle data found for the specified criteria. An empty report will be generated.");
            return new ArrayList<>(); 
        }

        List<String> applicationCode = vehicles.stream()
        		.map(VehicleFinal::getApplicationCode)
        		.distinct()
        		.collect(Collectors.toList());
        
        Map<String, VcctcTemp> vcctcTempMap = vcctcTempRepo.findAllById(applicationCode)
        		.stream().collect(Collectors.toMap(VcctcTemp::getApplicationCode, Function.identity(), (first, second) -> first));
        
        Map<String, Vcctc> vcctcMap = vcctcRepo.findAllById(applicationCode)
        		.stream().collect(Collectors.toMap(Vcctc::getApplicationCode, Function.identity(),(first, second) -> first));
        
        Map<String, AllottedVehicle> allotVehicleMap = allottedVehicleRepo.findAllById(applicationCode)
        		.stream().collect(Collectors.toMap(AllottedVehicle::getApplicationCode, Function.identity(), (first, second) -> first));
        Map<String, LiftedVehicles> liftedVehicleMap = liftedRepo.findAllById(applicationCode)
        		.stream().collect(Collectors.toMap(LiftedVehicles::getApplicationCode, Function.identity(), (first, second) -> first));
        Map<String, Scrap> scrapMap = scrapRepo.findAllById(applicationCode)
        		.stream().collect(Collectors.toMap(Scrap::getApplicationCode, Function.identity(), (first, second) -> first));
        Map<String, TenderVehicles> tenderMap = tenderRepo.findAllById(applicationCode)
        		.stream().collect(Collectors.toMap(TenderVehicles::getApplicationCode, Function.identity(), (first, second) -> first));
        // Map the database entities to DTOs for the report
        List<VehicleReportsDTO> dtoList = new ArrayList<>();
        long slnoCounter = 1;
        try
        {
        	for (VehicleFinal vehicle : vehicles) {
                log.info("Mapping vehicle with Registration No: {}", vehicle.getRegistrationNo());
                String appCode = vehicle.getApplicationCode();
                
                VcctcTemp vcctcTemp = vcctcTempMap.get(appCode);
                Vcctc vcctc = vcctcMap.get(appCode);
                AllottedVehicle allotVehicle = allotVehicleMap.get(appCode);
                LiftedVehicles lift = liftedVehicleMap.get(appCode);
                Scrap scrap = scrapMap.get(appCode);
                
                dtoList.add(toVehicleReportDTO(vehicle, slnoCounter++, vcctcTemp, vcctc, allotVehicle, lift, scrap));
            }
        }
        catch(Exception e)
        {
        	  log.error("CRITICAL ERROR: Failed to map vehicle during report generation. The error is: ", e);
              
              throw new RuntimeException("Failed during DTO mapping for reports", e);
        }
        
        log.info("Successfully mapped all vehicles. Final DTO list size for report: {}", dtoList.size());
        
        return dtoList;
    }

    private String getReportPathForProcessCode(int processCode) {
        switch (processCode) {
            case 2:  return "/reports/listApprovedByTransport.jasper";
            case 3:  return "/reports/listPlacedBeforeVCC.jasper";
            case 5:  return "/reports/listPriceFixedByTc.jasper";
            case 7:  return "/reports/listAllotted.jasper";
            case 11: return "/reports/listTender.jasper";
            case 9:  return "/reports/listLifted.jasper";
            case 10: return "/reports/listNonLifted.jasper";
            case -2: return "/reports/listScrap.jasper";
            default:
                log.error("No report template found for unknown process code: {}", processCode);
                throw new IllegalArgumentException("Invalid process code for report generation: " + processCode);
        }
    }
    
    private byte[] generateReport(String reportPath, Map<String, Object> parameters, JRDataSource dataSource, ReportFormat format)
    {
    	try(InputStream reportStream = this.getClass().getResourceAsStream(reportPath)){
    		if(reportStream == null)
    		{
    			throw new ReportGenerationException("Report template not found at path" + reportPath, null);
    		}
    		
    		JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportStream);
    		
    		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
    		
    		return exportReport(jasperPrint, format);
    	}catch (JRException e) {
            log.error("Error generating JasperReport at path: {}", reportPath, e);
            throw new ReportGenerationException("Failed to generate report due to a JasperReports error.", e);
        } catch (Exception e) {
            log.error("An unexpected error occurred during report generation for path: {}", reportPath, e);
            throw new ReportGenerationException("An unexpected error occurred during report generation.", e);
        }
    }
    
    private byte[] exportReport(JasperPrint jasperPrint, ReportFormat format) throws JRException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        switch (format) {
            case PDF:
            	JRPdfExporter pdfExporter = new JRPdfExporter();
                pdfExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                pdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));
                
                pdfExporter.exportReport();
                break;
            case XLSX:
                JRXlsxExporter exporter = new JRXlsxExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));
                
                SimpleXlsxReportConfiguration config = new SimpleXlsxReportConfiguration();
                config.setDetectCellType(true);
                config.setCollapseRowSpan(false);
                config.setWhitePageBackground(false);
                config.setRemoveEmptySpaceBetweenRows(true);
                exporter.setConfiguration(config);

                exporter.exportReport();
                break;
            default:
                throw new IllegalArgumentException("Unsupported report format: " + format);
        }

        return byteArrayOutputStream.toByteArray();
    }
        
    private boolean userHasAnyRole(User user, String... roles) {
        Set<String> roleSet = Arrays.stream(roles)
                .map(String::toUpperCase)
                .collect(Collectors.toSet());

        return user.getAuthorities().stream()
                .map(granted -> granted.getAuthority().toUpperCase())
                .anyMatch(roleSet::contains);
    }
    
    private Date parseDateFromCombinedField(String combinedField, String applicationCode) {
        // Check for invalid input upfront
        if (combinedField == null || combinedField.isEmpty() || !combinedField.contains("|")) {
            return null; 
        }

        String[] parts = combinedField.split("\\|");
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            log.warn("Invalid format: No date part found after '|' in '{}' for application code: {}", 
                      combinedField, applicationCode);
            return null;
        }

        String dateStr = parts[1].trim();

        // Define formatters for the patterns you want to support
        DateTimeFormatter dmyFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter ymdFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            // --- THE FIX IS HERE ---
            // First, try parsing with the "dd-MM-yyyy" format
            LocalDate localDate = LocalDate.parse(dateStr, dmyFormatter);
            return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        } catch (DateTimeParseException e1) {
            // If the first format fails, log it (optional) and then try the second format
            log.trace("Could not parse date '{}' with format dd-MM-yyyy, trying yyyy-MM-dd. Reason: {}", dateStr, e1.getMessage());

            try {
                LocalDate localDate = LocalDate.parse(dateStr, ymdFormatter);
                return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            
            } catch (DateTimeParseException e2) {
                // If both formats fail, log a more serious error and return null
                log.error("Could not parse date string '{}' with any known format for application code {}. Full field: '{}'", 
                          dateStr, applicationCode, combinedField);
                return null;
            }
        }
    }
    
    private String parseLetterNumberFromCombinedField(String combinedField) {
        if (combinedField == null || combinedField.isEmpty() || !combinedField.contains("|")) {
            return null;
        }
        // No need for a try-catch here, as split is safe.
        String[] parts = combinedField.split("\\|");
        if (parts.length > 0) {
            return parts[0].trim();
        }
        return null;
    }

	private VehicleReportsDTO toVehicleReportDTO(VehicleFinal vehFinal, long slno, VcctcTemp vcctcTemp, Vcctc vcctc, AllottedVehicle allotVehicle, LiftedVehicles lifter, Scrap scrap)
	{
		VehicleReportsDTO dto = new VehicleReportsDTO();
		
		dto.setApplicationcode(vehFinal.getApplicationCode());
		
		dto.setSlno(slno);
				
		if(vehFinal.getDepartmentCode() != null)
		{

			dto.setDepartmentname(vehFinal.getDepartmentCode().getDepartmentName());
		}
		
		dto.setRegistrationno(vehFinal.getRegistrationNo());
		
		dto.setOfficename(vehFinal.getOfficeName());
		
		if(vehFinal.getVehicletypecode() != null)
		{

			dto.setVehicletypedescription(vehFinal.getVehicletypecode().getVehicletypedescription());
		}
		
		dto.setManufactureyear(vehFinal.getManufactureyear());
		
		dto.setPurchasedate(vehFinal.getPurchasedate());
		
		dto.setVehicleprice(vehFinal.getVehicleprice());
		
		dto.setTotalkms(vehFinal.getTotalkms()); 	
		
		dto.setDepreciatedamount(vehFinal.getDepreciatedamount());
		
		String accidentResolvedText = (vehFinal.getAccidentcaseresolved() != null && vehFinal.getAccidentcaseresolved().equals("Y")) ? "YES" : "NO";
		dto.setAccidentcaseresolved(accidentResolvedText);
		
		String mviReportAvailableText = (vehFinal.getMvireportavailable() != null && vehFinal.getMvireportavailable().equals("Y")) ? "YES" : "NO";
		dto.setMvireportavailable(mviReportAvailableText);
		
		dto.setRemarks(vehFinal.getRemarks());
		
		// --- Properties from VccTcTemp ---
		  if (vcctcTemp != null) {
		        String letterNoDate = vcctcTemp.getLetterNoDate();
		        dto.setLetterNumber(parseLetterNumberFromCombinedField(letterNoDate));
		        dto.setLetterDate(parseDateFromCombinedField(letterNoDate, vehFinal.getApplicationCode()));
		    }

		    // --- Properties from VccTc ---
		    // Same simplification here. All the parsing complexity is hidden away.
		    if (vcctc != null) {
		        String letterNoDate = vcctc.getLetterNoDate();
		        dto.setTcLetterNo(parseLetterNumberFromCombinedField(letterNoDate));
		        dto.setTcLetterDate(parseDateFromCombinedField(letterNoDate, vehFinal.getApplicationCode()));
		        dto.setTcvehicleprice(vcctc.getVehiclePrice());
		    }

		    // --- Properties from AllotVehicle ---
		    if (allotVehicle != null) {
		        dto.setAllotteddate(allotVehicle.getAllottedDate());
		        dto.setAllotteesname(allotVehicle.getAllotteesName());
		        dto.setAllotteesaddress(allotVehicle.getAllotteesAddress());
		    }
		    
		    // --- Properties from Lifter ---
		    if (lifter != null) {
		        dto.setLiftersname(lifter.getLiftersName());
		        dto.setLiftersaddress(lifter.getLiftersAddress());
		        dto.setLifteddate(lifter.getLifteddate());
		    }
		    
		    // --- Properties from Scrap ---
		    if (scrap != null) {
		        dto.setScrapAmount(scrap.getAmount());
		    }
		
		return dto;
	}
	
}