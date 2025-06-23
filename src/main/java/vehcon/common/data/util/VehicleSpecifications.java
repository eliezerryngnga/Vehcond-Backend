package vehcon.common.data.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import lombok.NoArgsConstructor;
import vehcon.models.appdata.VehicleFinal;

@NoArgsConstructor
public final class VehicleSpecifications {
	
	// Specification HELPER METHODS
    public static Specification<VehicleFinal> hasProcessCode(int processCode) {
        return SpecificationUtils.nestedPropertyEquals(
            processCode, "processcode", "processcode"
        );
    }
    
    public static Specification<VehicleFinal> isInDepartment(int departmentCode) {
        return SpecificationUtils.nestedPropertyEquals(departmentCode, "departmentCode", "departmentCode");
    }
    
    
    public static Specification<VehicleFinal> isMviAvailable(boolean isAvailable, String mviAvailableStatus) {
        if (isAvailable) {
            return (root, query, cb) -> cb.equal(cb.upper(root.get("mvireportavailable")), mviAvailableStatus.toUpperCase());
        } else {
            Specification<VehicleFinal> isNull = (root, query, cb) -> cb.isNull(root.get("mvireportavailable"));
            Specification<VehicleFinal> isNotY = (root, query, cb) -> cb.notEqual(cb.upper(root.get("mvireportavailable")), mviAvailableStatus.toUpperCase());
            
            return Specification.where(isNull).or(isNotY);
        }
    }
    
    public static Specification<VehicleFinal> hasVehicleTypeIn(List<Integer> typeCodes)
    {
    	return (root, query, cb) -> {
    		if(typeCodes == null || typeCodes.isEmpty())
    		{
    			return cb.conjunction();
    		}
    		
    		return root.get("vehicletypecode").get("vehicleTypeCode").in(typeCodes);
    	};
    }
    
    public static Specification<VehicleFinal> hasSearchTerm(String searchTerm, List<String> searchableFields) {
        return SpecificationUtils.searchInFields(searchTerm, searchableFields);
    }
    
    public static Specification<VehicleFinal> dateIsWithin(Integer year, Integer month, String dateFieldName) {
        return (root, query, cb) -> {
            if (year == null) {
                return cb.conjunction(); // No year provided, no filter applied
            }

            LocalDateTime start;
            LocalDateTime end;

            if (month != null && month >= 1 && month <= 12) {
                // Filter by specific year and month
                start = LocalDate.of(year, month, 1).atStartOfDay();
                end = start.plusMonths(1);
            } else {
                // Filter by entire year
                start = LocalDate.of(year, 1, 1).atStartOfDay();
                end = start.plusYears(1);
            }

            return cb.between(root.get(dateFieldName), start, end);
        };
    }
}
