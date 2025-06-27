package vehcon.common.data.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SpecificationUtils {

    /**
     * Creates a Specification for searching a term across multiple entity fields (case-insensitive LIKE).
     * Returns null if searchTerm is blank, allowing easy chaining with 'and'.
     *
     * @param searchTerm The term to search for.
     * @param fields List of field names (String) within the entity T to search.
     * @param <T> The entity type.
     * @return A Specification<T> or null.
     */
//    public static <T> Specification<T> searchInFields(String searchTerm, List<String> fields) {
//        if (searchTerm == null || searchTerm.trim().isEmpty() || fields == null || fields.isEmpty()) {
//            return null; // No search term, return null spec
//        }
//        return (root, query, cb) -> {
//            List<Predicate> predicates = new ArrayList<>();
//            String pattern = "%" + searchTerm.toLowerCase() + "%";
//            
//            Integer intSearchValue = null;
//            
//            try {
//            	intSearchValue = Integer.parseInt(searchTerm);
//            }
//            catch(NumberFormatException e)
//            {
//            	throw e;
//            }
//            
//            for (String field : fields) {
//                try {
//                	
//                	Path<?> path = root.get(field);
//                	Class<?> fieldType = path.getJavaType();
//                	
//                	if(String.class.isAssignableFrom(fieldType))
//                	{
//                		// Assumes direct fields, handle nested fields if needed differently
//                        predicates.add(cb.like(cb.lower(path.as(String.class)), pattern));
//                	}
//                	 else if (intSearchValue != null && (Integer.class.isAssignableFrom(fieldType) || int.class.isAssignableFrom(fieldType))) {
//                         predicates.add(cb.equal(path, intSearchValue));
//                     }
//                    
//                } catch (IllegalArgumentException e) {
//                    // Handle cases where a field might not exist or is not comparable
//                    // Log this error appropriately
//                	  log.warn("Could not create search predicate for field '{}'. Field might not exist or is not accessible. Error: {}", field, e.getMessage());
//                     }
//                catch(Exception e)
//                {
//                	log.error("Unexpected error creating predicate for field '{}': {}", field, e.getMessage(), e);
//                }
//            }
//            
//            if (predicates.isEmpty()) {
//                return null; // No valid predicates could be built
//            }
//            
//            // Combine the field searches with OR
//            return cb.or(predicates.toArray(new Predicate[0]));
//        };
//    }
	
	public static <T> Specification<T> searchInFields(String searchTerm, List<String> fields) {
//	    final Logger log = LoggerFactory.getLogger(YourSpecificationClass.class);

	    if (searchTerm == null || searchTerm.trim().isEmpty() || fields == null || fields.isEmpty()) {
	        return null; // No search term or fields, so no specification needed.
	    }

	    return (root, query, cb) -> {
	        List<Predicate> predicates = new ArrayList<>();
	        String pattern = "%" + searchTerm.toLowerCase().trim() + "%";

	        for (String field : fields) {
	            try {
	                Path<?> path = root.get(field); // Get the path to the field
	                Class<?> fieldType = path.getJavaType(); // Get its Java type

	                // 1. Handle String fields
	                if (String.class.isAssignableFrom(fieldType)) {
	                    predicates.add(cb.like(cb.lower(path.as(String.class)), pattern));
	                }
	                // 2. Handle Integer and Long fields
	                else if (Number.class.isAssignableFrom(fieldType)) {
	                    // Try to parse the search term as a number ONLY if the field is numeric.
	                    try {
	                        // Use BigDecimal for safe parsing of any number type
	                        BigDecimal searchNumber = new BigDecimal(searchTerm.trim());
	                        
	                        // Create an 'equal' predicate. This works for Integer, Long, Double, etc.
	                        predicates.add(cb.equal(path, searchNumber));
	                        
	                    } catch (NumberFormatException e) {
	                        // This is expected and OK. It just means the search term isn't a valid number.
	                        // We do nothing and simply don't add a predicate for this numeric field.
	                        // Example: User searches "abc", but we are checking the 'manufactureyear' field.
	                    }
	                }
	                // 3. (Optional) Handle other types like Boolean, Enums, etc.
	                // else if (Boolean.class.isAssignableFrom(fieldType)) { ... }

	            } catch (IllegalArgumentException e) {
	                // This happens if the field name in the list is invalid.
	                log.warn("Search field '{}' not found in entity {}. It will be ignored.", field, root.getJavaType().getSimpleName());
	            } catch (Exception e) {
	                // Catch any other unexpected errors during predicate creation.
	                log.error("Unexpected error creating search predicate for field '{}': {}", field, e.getMessage(), e);
	            }
	        }

	        if (predicates.isEmpty()) {
	            // This can happen if the search term is not a number and all searchable fields are numeric,
	            // or if all field names were invalid. Returning a "no-op" predicate is safer than null.
	            return cb.conjunction(); // This means "WHERE 1=1", effectively no filter.
	        }

	        // Combine all the individual field search predicates with an OR.
	        return cb.or(predicates.toArray(new Predicate[0]));
	    };
	}

    /**
     * Creates a Specification for an exact match on a potentially nested property.
     * Example propertyPath for entity.relatedEntity.id: "relatedEntity", "id"
     *
     * @param value The value to match against.
     * @param propertyPath Sequence of property names to navigate to the target field.
     * @param <T> The entity type.
     * @return A Specification<T> or null if value or path is invalid.
     */
    public static <T> Specification<T> nestedPropertyEquals(Object value, String... propertyPath) {
        if (value == null || propertyPath == null || propertyPath.length == 0) {
            return null; // If value is null, don't filter by it (unless specifically required)
        }
        return (root, query, cb) -> {
            try {
                Path<?> path = root;
                for (String property : propertyPath) {
                    path = path.get(property);
                }
                return cb.equal(path, value);
            } catch (IllegalArgumentException e) {
                 log.warn("Warning: Could not create equality predicate for path '" + String.join(".", propertyPath) + "'. Error: " + e.getMessage());
                 return null; // Or maybe cb.conjunction() if ANDing, cb.disjunction() if ORing later
            }
        };
    }

     /**
     * Creates a Specification for an exact match on a direct property.
     *
     * @param field The direct field name in entity T.
     * @param value The value to match.
     * @param <T> The entity type.
     * @return A Specification<T> or null.
     */
    public static <T> Specification<T> propertyEquals(String field, Object value) {
         if (value == null || field == null || field.trim().isEmpty()) {
             return null;
         }
        return (root, query, cb) -> {
             try {
                 return cb.equal(root.get(field), value);
             } catch (IllegalArgumentException e) {
                  log.warn("Warning: Could not create equality predicate for field '" + field + "'. Error: " + e.getMessage());
                  return null;
             }
        };
    }
}