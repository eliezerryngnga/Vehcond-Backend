package vehcon.services.appdata;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import vehcon.dto.masters.UserPagesDTO;
import vehcon.models.auth.Roles;
import vehcon.models.masters.PageUrl;
import vehcon.models.masters.UserPages;
import vehcon.repo.appdata.UserPagesRepository;
import vehcon.repo.auth.RolesRepository;
import vehcon.repo.masters.PageUrlRepository;

@Service
@RequiredArgsConstructor
public class UserPagesService {

	private final UserPagesRepository userPagesRepo;
	private final RolesRepository rolesRepo;
	private final PageUrlRepository pageUrlRepo;
	 
	@Transactional
	public UserPages assignUserPages(UserPagesDTO dto) {
		
	    Roles roleEntity = rolesRepo.findByRolecode(dto.getRoleCode())
	                .orElseThrow(() -> new EntityNotFoundException("Role not found with code: " + dto.getRoleCode()));
	    
	    PageUrl pageUrlEntity = pageUrlRepo.findByUrlCode(dto.getUrlCode())
	                .orElseThrow(() -> new EntityNotFoundException("PageUrl not found with code: " + dto.getUrlCode()));

	        UserPages assignPage = new UserPages();
	        assignPage.setRole(roleEntity);      
	        assignPage.setPageUrl(pageUrlEntity);

	        String constructedUserPageCode = dto.getRoleCode() + "U" + dto.getUrlCode();
	        
	        assignPage.setUserPageCode(constructedUserPageCode);

	        return userPagesRepo.save(assignPage);
	    }
	
//	@Transactional
//	public UserPages unassignUserPages(UserPagesDTO dto)
//	{
//		
//	}
}
