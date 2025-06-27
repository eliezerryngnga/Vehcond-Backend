package vehcon.services.reports;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportType {
	
	 APPROVED_BY_TRANSPORT(2, "/reports/listApprovedByTransport.jasper", "entrydate"),
	    PLACED_BEFORE_VCC(3, "/reports/listPlacedBeforeVCC.jasper", "entrydate"),
	    PRICE_FIXED_BY_TC(5, "/reports/listPriceFixedByTc.jasper", "letternodate"),
	    ALLOTTED(7, "/reports/listAllotted.jasper", "letternodate"),
	    TENDERED(11, "/reports/listTender.jasper", "letternodate"),
	    LIFTED(9, "/reports/listLifted.jasper", "letternodate"),
	    NON_LIFTED(10, "/reports/listNonLifted.jasper", "letternodate"),
	    SCRAPPED(-2, "/reports/listScrap.jasper", "letternodate");

	    private final int processCode;
	    private final String reportPath;
	    private final String dateFieldName;
}
