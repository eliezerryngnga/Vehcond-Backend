package vehcon.services.auth;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vehcon.common.data.util.SpecificationUtils;
import vehcon.dto.auth.UserListDTO;
import vehcon.models.auth.User;
import vehcon.repo.auth.UserRepository;

@Service
@RequiredArgsConstructor
public class UsersService {
	
	private final UserRepository userRepo;
	
	@Transactional(readOnly = true)
	public Page<UserListDTO> getAllEnabledUsers(
			String globalSearchTerm,
			Pageable pageable
			)
	{
		Specification<User> spec = Specification.where(hasUserAccessTrue());

        if (globalSearchTerm != null && !globalSearchTerm.trim().isEmpty()) {
            List<String> globalSearchFields = List.of("username","name");
            Specification<User> globalSearchSpec = SpecificationUtils.searchInFields(globalSearchTerm, globalSearchFields);
            if (globalSearchSpec != null) 
            {
                spec = spec.and(globalSearchSpec);
            }
        }
        return userRepo.findAll(spec, pageable)
        		.map(this::mapToUserListDTO);
    }
	
	@Transactional(readOnly = true)
	public Page<UserListDTO> getAllDisabledUsers(
			String globalSearchTerm,
			Pageable pageable
			)
	{
		Specification<User> spec = Specification.where(hasUserAccessFalse());

        if (globalSearchTerm != null && !globalSearchTerm.trim().isEmpty()) {
            List<String> globalSearchFields = List.of("username","name");
            Specification<User> globalSearchSpec = SpecificationUtils.searchInFields(globalSearchTerm, globalSearchFields);
            if (globalSearchSpec != null) 
            {
                spec = spec.and(globalSearchSpec);
            }
        }
        return userRepo.findAll(spec, pageable)
        		.map(this::mapToUserListDTO);
    }
	
	private Specification<User> hasUserAccessTrue()
	{
		return (root, query, cb) -> cb.equal(root.get("userAccess"),true);
	}
	
	private Specification<User> hasUserAccessFalse()
	{
		return (root, query, cb) -> cb.equal(root.get("userAccess"),false);
	}
	
	private UserListDTO mapToUserListDTO(User user)
	{
		UserListDTO dto = new UserListDTO();
		
		dto.setUsercode(user.getUsercode());
		
		dto.setUsername(user.getUsername());
		dto.setName(user.getName());
		
		if(user.getDepartment() != null)
		{
			dto.setDepartmentCode(user.getDepartment().getDepartmentCode());
			dto.setDepartmentName(user.getDepartment().getDepartmentName());
		}
		
		if (user.getRole() != null)
		{
			dto.setRoleName(user.getRole().name());
		}
		
		dto.setUseraccess(user.isUserAccess());
		
		return dto;
	}
		
}
