package vehcon.services.reports;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
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
import vehcon.exception.ResourceNotFoundException;
import vehcon.models.appdata.VehicleFinal;
import vehcon.models.auth.User;
import vehcon.repo.appdata.AllottedVehiclesRepository;
import vehcon.repo.appdata.VccTcRepository;
import vehcon.repo.appdata.VehicleFinalRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {

	private final VehicleFinalRepository vehFinalRepo;
	private final AllottedVehiclesRepository allottedVehicleRepo;
	private final VccTcRepository vcctcRepo;
	
	
	private static final String APPROVED_VEHICLES_REPORT_PATH = "/reports/listApprovedByTransport.jrxml";
	
	private static final String PLACED_BEFORE_VCC_REPORT_PATH = "/reports/listPlacedBeforeVCC.jrxml";
	private static final String PRICE_FIXED_BY_TC_REPORT_PATH = "/reports/listPriceFixedByTc.jrxml";
	private static final String ALLOTTED_VEHICLE_REPORT_PATH = "/reports/listAllotted.jrxml";
	private static final String TENDER_VEHICLE_REPORT_PATH = "/reports/listTender.jrxml";
	private static final String LIFTED_VEHICLE_REPORT_PATH = "/reports/listLifted.jrxml";
	private static final String SCRAPPED_VEHICLE_REPORT_PATH = "/reports/listScrap.jrxml";
	private static final String NON_LIFTED_VEHICLE_REPORT_PATH = "/reports/listNonLifted.jrxml";
	
	private static final int PROCESS_CODE_REJECT_VEHICLE = 14;	
	private static final int PROCESS_CODE_SUBMITTED_TO_TRANSPORT = 15;
	private static final int PROCESS_CODE_APPROVED_BY_TRANSPORT = 2; //For 3a.
	private static final int PROCESS_CODE_PLACED_BEFORE_VCC = 3; //3b.
	private static final int PROCESS_CODE_PRICE_FIXED_BY_TC = 5; //3c.
	private static final int PROCESS_CODE_ALLOTMENT_OF_VEHICLE = 7; //3e.
	private static final int PROCESS_CODE_FOR_TENDER = 11;
	private static final int PROCESS_CODE_FOR_SCRAP_TC = -2;
	private static final int PROCESS_CODE_LIFTED_VEHICLE = 9;
	private static final int PROCESS_CODE_NON_LIFTED_VEHICLE = 10;
	
	private enum ReportFormat {
		PDF, 
		XLSX
	}
	
	
    public byte[] getApprovedVehicleReportAsPdf(int year, int month, User user) 
    {
    	return generateMonthlyVehicleReport(year, month, ReportFormat.PDF, user);
    	
    }
    
    public byte[] generateApprovedVehicleReportAsExcel(int year, int month, User user) 
    {
    	return generateMonthlyVehicleReport(year, month, ReportFormat.XLSX, user);
    }
    
//    Recommended for Condemnationation
    public byte[] getPlacedBeforeVCCReportAsPdf(int year, int month, User user) 
    {
    	return generateMonthlyVehicleReport(year, month, ReportFormat.PDF, user);
    	
    }
    
    public byte[] generatePlacedBeforeVCCReportAsExcel(int year, int month, User user) 
    {
    	return generateMonthlyVehicleReport(year, month, ReportFormat.XLSX, user);
    }
    
//    Price Fixed By TC
    public byte[] getPriceFixedByTCReportAsPdf(int year, int month, User user) 
    {
    	return generateMonthlyVehicleReport(year, month, ReportFormat.PDF, user);
    	
    }
    
    public byte[] generatePriceFixedByTCReportAsExcel(int year, int month, User user) 
    {
    	return generateMonthlyVehicleReport(year, month, ReportFormat.XLSX, user);
    }
    
//    Allotted 
    public byte[] getAllottedVehicleReportAsPdf(int year, int month, User user) 
    {
    	return generateMonthlyVehicleReport(year, month, ReportFormat.PDF, user);
    	
    }
    
    public byte[] generateAllottedVehicleReportAsExcel(int year, int month, User user) 
    {
    	return generateMonthlyVehicleReport(year, month, ReportFormat.XLSX, user);
    }
    
//    Tendered
    public byte[] getTenderedVehicleReportAsPdf(int year, int month, User user) 
    {
    	return generateMonthlyVehicleReport(year, month, ReportFormat.PDF, user);
    	
    }
    
    public byte[] generateTenderedVehicleReportAsExcel(int year, int month, User user) 
    {
    	return generateMonthlyVehicleReport(year, month, ReportFormat.XLSX, user);
    }
//    Lifted
    public byte[] getLiftedVehicleReportAsPdf(int year, int month, User user) 
    {
    	return generateMonthlyVehicleReport(year, month, ReportFormat.PDF, user);
    	
    }
    
    public byte[] generateLiftedVehicleReportAsExcel(int year, int month, User user) 
    {
    	return generateMonthlyVehicleReport(year, month, ReportFormat.XLSX, user);
    }
//    Scrapped
    public byte[] getScrappedVehicleReportAsPdf(int year, int month, User user) 
    {
    	return generateMonthlyVehicleReport(year, month, ReportFormat.PDF, user);
    	
    }
    
    public byte[] generateScrappedVehicleReportAsExcel(int year, int month, User user) 
    {
    	return generateMonthlyVehicleReport(year, month, ReportFormat.XLSX, user);
    }
//    Non Lifted
    public byte[] getNonLiftedVehicleReportAsPdf(int year, int month, User user) 
    {
    	return generateMonthlyVehicleReport(year, month, ReportFormat.PDF, user);
    	
    }
    
    public byte[] generateNonLiftedVehicleReportAsExcel(int year, int month, User user) 
    {
    	return generateMonthlyVehicleReport(year, month, ReportFormat.XLSX, user);
    }
    
    private byte[] generateMonthlyVehicleReport(int year, int month, ReportFormat format, User user)
    {
    	List<VehicleReportsDTO> dtoList = getMonthlyVehicleReportData(year, month, user);
    	
    	JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dtoList);
    	
    	Map<String, Object> parameters = new HashMap<>();
    	
    	return generateReport(APPROVED_VEHICLES_REPORT_PATH, parameters, dataSource, format);
    }
    
    private JasperReport compileReport(String reportPath) throws JRException {
        try (InputStream reportStream = this.getClass().getResourceAsStream(reportPath)) {
            if (reportStream == null) {
                throw new ReportGenerationException("Report template not found at path: " + reportPath, null);
            }
            return JasperCompileManager.compileReport(reportStream);
        } catch (Exception e) {
            log.error("Failed to compile report template: {}", reportPath, e);
            throw new ReportGenerationException("Could not compile report template: " + reportPath, e);
        }
    }
    
    private byte[] generateReport(String reportPath, Map<String, Object> parameters, JRBeanCollectionDataSource dataSource, ReportFormat format)
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
    
    private List<VehicleReportsDTO> getMonthlyVehicleReportData(int year, int month, User user) {
        Specification<VehicleFinal> spec = Specification
        		.where(VehicleSpecifications.hasProcessCode(PROCESS_CODE_APPROVED_BY_TRANSPORT))
        		.and(VehicleSpecifications.dateIsWithin(year, month, "entrydate"));
        
        if(!userHasAnyRole(user, "ADMIN", "TD"))
        {
        	Integer deptCode = user.getDepartment() != null ? user.getDepartment().getDepartmentCode() : null;
        	if(deptCode == null)
        	{
        		throw new AccessDeniedException("User does not have a department assigned.");
        	}
        	
        	spec = spec.and(VehicleSpecifications.isInDepartment(deptCode));
        }
        
        List<VehicleFinal> vehicles = vehFinalRepo.findAll(spec);

        if (vehicles == null || vehicles.isEmpty()) {
            throw new ResourceNotFoundException("No vehicle data found for the specified criteria");
        }

        List<VehicleReportsDTO> dtoList = new ArrayList<>();
        long slnoCounter = 1;
        for (VehicleFinal vehicle : vehicles) {
            dtoList.add(toVehicleReportDTO(vehicle, slnoCounter++));
        }

        return dtoList;
    }
    
    private boolean userHasAnyRole(User user, String... roles) {
        Set<String> roleSet = Arrays.stream(roles)
                .map(String::toUpperCase)
                .collect(Collectors.toSet());

        return user.getAuthorities().stream()
                .map(granted -> granted.getAuthority().toUpperCase())
                .anyMatch(roleSet::contains);
    }
    

	private VehicleReportsDTO toVehicleReportDTO(VehicleFinal vehFinal, long slno)
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
		return dto;
	}

}

