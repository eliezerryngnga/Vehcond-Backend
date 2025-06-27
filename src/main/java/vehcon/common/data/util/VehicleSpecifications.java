package vehcon.common.data.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.NoArgsConstructor;
import vehcon.models.appdata.AllottedVehicle;
import vehcon.models.appdata.LiftedVehicles;
import vehcon.models.appdata.Scrap;
import vehcon.models.appdata.TenderVehicles;
import vehcon.models.appdata.Vcctc;
import vehcon.models.appdata.VcctcTemp;
import vehcon.models.appdata.VehicleFinal;
import vehcon.models.appdata.Verification;

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
    
    public static Specification<VehicleFinal> dateIsWithin(Integer year, Integer month) {
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

            return cb.between(root.get("entrydate"), start, end);
        };
    }

    public static Specification<VehicleFinal> hasVerificationDateIn(Integer year, Integer month) {
    	 return (root, query, criteriaBuilder) -> {
    		 
             if (year == null && month == null) {
                 return criteriaBuilder.conjunction();
             }

             List<Predicate> predicates = new ArrayList<>();
             Join<VehicleFinal, Verification> allotJoin = root.join("verification");
             Expression<Date> entryDates = allotJoin.get("entryDate");

             // 1. Conditionally add the YEAR predicate
             if (year != null) {
                 Predicate yearPredicate = criteriaBuilder.equal(
                     criteriaBuilder.function("date_part", Integer.class, criteriaBuilder.literal("year"), entryDates),
                     year
                 );
                 predicates.add(yearPredicate);
             }

             // 2. Conditionally add the MONTH predicate
             if (month != null) {
                 Predicate monthPredicate = criteriaBuilder.equal(
                     criteriaBuilder.function("date_part", Integer.class, criteriaBuilder.literal("month"), entryDates),
                     month
                 );
                 predicates.add(monthPredicate);
             }

             // 3. Combine all collected predicates with AND
             // This works even if there's only one predicate in the list.
             // If the list is empty (which we guard against above), it returns a conjunction.
             return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
         };    	
    }
    
    public static Specification<VehicleFinal> hasAllottedVehiclesDateIn(Integer year, Integer month) {
        return (root, query, criteriaBuilder) -> {
            // If both are null, we don't need to do anything.
            if (year == null && month == null) {
                return criteriaBuilder.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();
            Join<VehicleFinal, AllottedVehicle> allotJoin = root.join("allottedVehicle");
            Expression<Date> allottedDates = allotJoin.get("allottedDate");

            // 1. Conditionally add the YEAR predicate
            if (year != null) {
                Predicate yearPredicate = criteriaBuilder.equal(
                    criteriaBuilder.function("date_part", Integer.class, criteriaBuilder.literal("year"), allottedDates),
                    year
                );
                predicates.add(yearPredicate);
            }

            // 2. Conditionally add the MONTH predicate
            if (month != null) {
                Predicate monthPredicate = criteriaBuilder.equal(
                    criteriaBuilder.function("date_part", Integer.class, criteriaBuilder.literal("month"), allottedDates),
                    month
                );
                predicates.add(monthPredicate);
            }

            // 3. Combine all collected predicates with AND
            // This works even if there's only one predicate in the list.
            // If the list is empty (which we guard against above), it returns a conjunction.
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    public static Specification<VehicleFinal> hasLiftedVehiclesDateIn(Integer year, Integer month) {
        return (root, query, criteriaBuilder) -> {
            // If both are null, we don't need to do anything.
            if (year == null && month == null) {
                return criteriaBuilder.conjunction(); // Return a "match all" predicate
            }

            List<Predicate> predicates = new ArrayList<>();
            Join<VehicleFinal, LiftedVehicles> lifterJoin = root.join("liftedVehicles");
            Expression<Date> liftedDates = lifterJoin.get("lifteddate");

            // 1. Conditionally add the YEAR predicate
            if (year != null) {
                Predicate yearPredicate = criteriaBuilder.equal(
                    criteriaBuilder.function("date_part", Integer.class, criteriaBuilder.literal("year"), liftedDates),
                    year
                );
                predicates.add(yearPredicate);
            }

            // 2. Conditionally add the MONTH predicate
            if (month != null) {
                Predicate monthPredicate = criteriaBuilder.equal(
                    criteriaBuilder.function("date_part", Integer.class, criteriaBuilder.literal("month"), liftedDates),
                    month
                );
                predicates.add(monthPredicate);
            }

            // 3. Combine all collected predicates with AND
            // This works even if there's only one predicate in the list.
            // If the list is empty (which we guard against above), it returns a conjunction.
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    
//    public static Specification<VehicleFinal> hasVcctcTempDateIn(Integer year, Integer month) {
//        return (root, query, criteriaBuilder) -> {
//            
//            List<Predicate> predicates = new ArrayList<>();
//            Join<VehicleFinal, VcctcTemp> vcctcJoin = root.join("vcctcTemp");
//
//            // Unconditional Predicate: We always want to ensure the record has the correct format.
//            predicates.add(criteriaBuilder.like(vcctcJoin.get("letterNoDate"), "%|%"));
//
//            // Only perform date parsing and filtering if a year or month is actually provided.
//            if (year != null || month != null) {
//                
//                // This logic is now inside the condition, so it only runs when needed.
//                Expression<String> dateStringExpr = criteriaBuilder.function(
//                    "SPLIT_PART",
//                    String.class,
//                    vcctcJoin.get("letterNoDate"),
//                    criteriaBuilder.literal("|"),
//                    criteriaBuilder.literal(2)
//                );
//
//                // It's generally safer to cast to a string and then to the date type
//                // that matches your database dialect for DATE_PART (e.g., 'date' or 'timestamp').
//                Expression<Date> dateExpr = dateStringExpr.as(Date.class);
//
//                // Conditionally add the YEAR predicate
//                if (year != null) {
//                    Expression<Integer> yearExpr = criteriaBuilder.function("DATE_PART", Integer.class, criteriaBuilder.literal("year"), dateExpr);
//                    predicates.add(criteriaBuilder.equal(yearExpr, year));
//                }
//
//                // Conditionally add the MONTH predicate
//                if (month != null) {
//                    Expression<Integer> monthExpr = criteriaBuilder.function("DATE_PART", Integer.class, criteriaBuilder.literal("month"), dateExpr);
//                    predicates.add(criteriaBuilder.equal(monthExpr, month));
//                }
//            }
//            
//            // Combine all collected predicates with AND.
//            // This will work correctly if only the 'like' predicate is present.
//            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
//        };
//    }
// 
    
    public static Specification<VehicleFinal> hasVcctcTempDateIn(Integer year, Integer month) {
        return (root, query, criteriaBuilder) -> {
            
            List<Predicate> predicates = new ArrayList<>();
            Join<VehicleFinal, VcctcTemp> vcctcJoin = root.join("vcctcTemp");
            predicates.add(criteriaBuilder.like(vcctcJoin.get("letterNoDate"), "%|%"));

            if (year != null || month != null) {
                
                Expression<String> dateStringExpr = criteriaBuilder.function(
                    "SPLIT_PART", String.class, vcctcJoin.get("letterNoDate"), criteriaBuilder.literal("|"), criteriaBuilder.literal(2)
                );

                // Build the generic CASE expression first...
                Expression<Date> dateExpr = criteriaBuilder.selectCase()
                    .when(
                        criteriaBuilder.like(dateStringExpr, "____-__-__"), // YYYY-MM-DD
                        criteriaBuilder.function("to_date", Date.class, dateStringExpr, criteriaBuilder.literal("YYYY-MM-DD"))
                    )
                    .when(
                        criteriaBuilder.like(dateStringExpr, "__-__-____"), // DD-MM-YYYY
                        criteriaBuilder.function("to_date", Date.class, dateStringExpr, criteriaBuilder.literal("DD-MM-YYYY"))
                    )
                    .otherwise(criteriaBuilder.nullLiteral(Date.class)) 
                    .as(Date.class); // <-- THE FIX IS HERE: Cast the entire CASE expression to a Date expression.

                predicates.add(criteriaBuilder.isNotNull(dateExpr));

                if (year != null) {
                    Expression<Integer> yearExpr = criteriaBuilder.function("DATE_PART", Integer.class, criteriaBuilder.literal("year"), dateExpr);
                    predicates.add(criteriaBuilder.equal(yearExpr, year));
                }
                
                if (month != null) {
                    Expression<Integer> monthExpr = criteriaBuilder.function("DATE_PART", Integer.class, criteriaBuilder.literal("month"), dateExpr);
                    predicates.add(criteriaBuilder.equal(monthExpr, month));
                }
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    public static Specification<VehicleFinal> hasVcctcDateIn(Integer year, Integer month) {
        return (root, query, criteriaBuilder) -> {
            
            List<Predicate> predicates = new ArrayList<>();
            Join<VehicleFinal, Vcctc> vcctcJoin = root.join("vcctc");

            // Unconditional Predicate for format
            predicates.add(criteriaBuilder.like(vcctcJoin.get("letterNoDate"), "%|%"));

            // Only perform date filtering if a year or month is provided.
            if (year != null || month != null) {
                
                Expression<String> dateStringExpr = criteriaBuilder.function(
                    "SPLIT_PART",
                    String.class,
                    vcctcJoin.get("letterNoDate"),
                    criteriaBuilder.literal("|"),
                    criteriaBuilder.literal(2)
                );
                Expression<Date> dateExpr = dateStringExpr.as(Date.class);

                // Conditionally add the YEAR predicate
                if (year != null) {
                    Expression<Integer> yearExpr = criteriaBuilder.function("DATE_PART", Integer.class, criteriaBuilder.literal("year"), dateExpr);
                    predicates.add(criteriaBuilder.equal(yearExpr, year));
                }

                // Conditionally add the MONTH predicate
                if (month != null) {
                    Expression<Integer> monthExpr = criteriaBuilder.function("DATE_PART", Integer.class, criteriaBuilder.literal("month"), dateExpr);
                    predicates.add(criteriaBuilder.equal(monthExpr, month));
                }
            }
            
            // Combine all collected predicates with AND.
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
        
        public static Specification<VehicleFinal> hasScrapperDateIn(Integer year, Integer month) {
            return (root, query, criteriaBuilder) -> {
                
                List<Predicate> predicates = new ArrayList<>();
                Join<VehicleFinal, Scrap> vcctcJoin = root.join("scrapVehicles");

                // Unconditional Predicate: We always want to ensure the record has the correct format.
                predicates.add(criteriaBuilder.like(vcctcJoin.get("letterNoDate"), "%|%"));

                // Only perform date parsing and filtering if a year or month is actually provided.
                if (year != null || month != null) {
                    
                    // This logic is now inside the condition, so it only runs when needed.
                    Expression<String> dateStringExpr = criteriaBuilder.function(
                        "SPLIT_PART",
                        String.class,
                        vcctcJoin.get("letterNoDate"),
                        criteriaBuilder.literal("|"),
                        criteriaBuilder.literal(2)
                    );

                    // It's generally safer to cast to a string and then to the date type
                    // that matches your database dialect for DATE_PART (e.g., 'date' or 'timestamp').
                    Expression<Date> dateExpr = dateStringExpr.as(Date.class);

                    // Conditionally add the YEAR predicate
                    if (year != null) {
                        Expression<Integer> yearExpr = criteriaBuilder.function("DATE_PART", Integer.class, criteriaBuilder.literal("year"), dateExpr);
                        predicates.add(criteriaBuilder.equal(yearExpr, year));
                    }

                    // Conditionally add the MONTH predicate
                    if (month != null) {
                        Expression<Integer> monthExpr = criteriaBuilder.function("DATE_PART", Integer.class, criteriaBuilder.literal("month"), dateExpr);
                        predicates.add(criteriaBuilder.equal(monthExpr, month));
                    }
                }
                
                // Combine all collected predicates with AND.
                // This will work correctly if only the 'like' predicate is present.
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };
        }
        
        public static Specification<VehicleFinal> hasTenderedDateIn(Integer year, Integer month) {
            return (root, query, criteriaBuilder) -> {
                
                List<Predicate> predicates = new ArrayList<>();
                Join<VehicleFinal, TenderVehicles> tenderJoin = root.join("tenderVehicles");

                // Unconditional Predicate: We always want to ensure the record has the correct format.
                predicates.add(criteriaBuilder.like(tenderJoin.get("letternodate"), "%|%"));

                // Only perform date parsing and filtering if a year or month is actually provided.
                if (year != null || month != null) {
                    
                    // This logic is now inside the condition, so it only runs when needed.
                    Expression<String> dateStringExpr = criteriaBuilder.function(
                        "SPLIT_PART",
                        String.class,
                        tenderJoin.get("letternodate"),
                        criteriaBuilder.literal("|"),
                        criteriaBuilder.literal(2)
                    );

                    // It's generally safer to cast to a string and then to the date type
                    // that matches your database dialect for DATE_PART (e.g., 'date' or 'timestamp').
                    Expression<Date> dateExpr = dateStringExpr.as(Date.class);

                    // Conditionally add the YEAR predicate
                    if (year != null) {
                        Expression<Integer> yearExpr = criteriaBuilder.function("DATE_PART", Integer.class, criteriaBuilder.literal("year"), dateExpr);
                        predicates.add(criteriaBuilder.equal(yearExpr, year));
                    }

                    // Conditionally add the MONTH predicate
                    if (month != null) {
                        Expression<Integer> monthExpr = criteriaBuilder.function("DATE_PART", Integer.class, criteriaBuilder.literal("month"), dateExpr);
                        predicates.add(criteriaBuilder.equal(monthExpr, month));
                    }
                }
                
                // Combine all collected predicates with AND.
                // This will work correctly if only the 'like' predicate is present.
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };
        }
                
    }


