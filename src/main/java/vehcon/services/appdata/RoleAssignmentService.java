package vehcon.services.appdata;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vehcon.exception.ResourceNotFoundException;
import vehcon.models.auth.Roles;
import vehcon.models.masters.PageUrl;
import vehcon.models.masters.UserPages;
import vehcon.models.masters.UserPagesId;
import vehcon.repo.appdata.UserPagesRepository;
import vehcon.repo.auth.RolesRepository;
import vehcon.repo.masters.PageUrlRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleAssignmentService {

	private final UserPagesRepository userPagesRepo;
	private final RolesRepository rolesRepo;
	private final PageUrlRepository pageUrlRepo;
	 
	@Transactional(readOnly = true)
	public Set<Integer> getAssignedPagesIdsForRole(Integer roleCode)
	{
		List<UserPages> assignments = userPagesRepo.findByIdRoleCode(roleCode);
		
		return assignments.stream()
				.map(assignment -> assignment.getPageUrl().getUrlCode())
				.collect(Collectors.toSet());
	}
	
	@Transactional
    public void updateAssignedPages(Integer roleCode, Set<Integer> newPageIds) {
        // Step 1: Validate that the role exists to avoid errors.
        Roles role = rolesRepo.findById(roleCode)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with code: " + roleCode));

        // Step 2: Atomically delete all existing assignments for this role.
        userPagesRepo.deleteByIdRoleCode(roleCode);

        // Step 3: If the new list isn't empty, create the new assignment records.
        if (newPageIds != null && !newPageIds.isEmpty()) {
            List<UserPages> newAssignments = newPageIds.stream()
                .map(pageId -> {
                    // Create the composite key for the UserPages entity
                    UserPagesId id = new UserPagesId(roleCode, pageId);
                    
                    // Create a reference to the PageUrl entity. We don't need to fetch it,
                    // JPA just needs the ID to create the foreign key relationship.
                    PageUrl pageUrlRef = new PageUrl();
                    pageUrlRef.setUrlCode(pageId);

                    // Create the new assignment entity
                    UserPages newAssignment = new UserPages();
                    newAssignment.setId(id);
                    newAssignment.setRole(role);
                    newAssignment.setPageUrl(pageUrlRef);
                    
                    // Generate the value for your extra column
                    newAssignment.setUserPageCode(role.getRole() + "_" + pageId);
                    
                    return newAssignment;
                })
                .collect(Collectors.toList());

            // Step 4: Bulk-save all the new assignment records in a single database operation.
            userPagesRepo.saveAll(newAssignments);
        }
    }
}
