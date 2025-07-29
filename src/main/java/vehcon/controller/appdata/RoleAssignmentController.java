package vehcon.controller.appdata;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vehcon.dto.appdata.AssignedPagesDTO;
import vehcon.services.appdata.RoleAssignmentService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RoleAssignmentController {

	
	private final RoleAssignmentService roleAssignmentService;
	
	 @GetMapping("/assigned-pages/{roleCode}")
	    public ResponseEntity<Set<Integer>> getAssignedPageIds(@PathVariable Integer roleCode) {
	        Set<Integer> assignedIds = roleAssignmentService.getAssignedPagesIdsForRole(roleCode);
	        return ResponseEntity.ok(assignedIds);
	    }
	 
	 @PostMapping("/assign-pages/{roleCode}")
	    public ResponseEntity<Void> updateAssignmentsForRole(
	            @PathVariable Integer roleCode,
	            @RequestBody AssignedPagesDTO assignedPagesDTO) 
	    {
	        
	    	roleAssignmentService.updateAssignedPages(roleCode, assignedPagesDTO.getPageIds());
	        
	        return ResponseEntity.noContent().build();
	    }

	
	
	
}
