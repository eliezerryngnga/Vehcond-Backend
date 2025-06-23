package vehcon.services.auth;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vehcon.common.data.util.SpecificationUtils;
import vehcon.dto.auth.RolesDTO;
import vehcon.models.auth.Roles;
import vehcon.repo.auth.RolesRepository;

@Service
@RequiredArgsConstructor
public class RolesService {
	

	private final RolesRepository roleRepo;
	
	public List<Roles> getRoles()
	{
		return roleRepo.findAll();
	}
	
	public Page<Roles> getAllRoles(
			String globalSearchTerm,
			Pageable pageable
			)
	{
		Specification<Roles> spec = Specification.where(null);

        if (globalSearchTerm != null && !globalSearchTerm.trim().isEmpty()) {
            List<String> globalSearchFields = List.of("rolecode","rolename","description");
            Specification<Roles> globalSearchSpec = SpecificationUtils.searchInFields(globalSearchTerm, globalSearchFields);
            if (globalSearchSpec != null) 
            {
                spec = spec.and(globalSearchSpec);
            }
        }
        return roleRepo.findAll(spec, pageable);
    }
	
	public Roles addRoles(RolesDTO dto) {
        Roles role = new Roles();
        role.setRole(dto.getRolename());
        //role.setDescription(dto.getDescription());
        return roleRepo.save(role);
    }
}
