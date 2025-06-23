package vehcon.services.masters;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vehcon.annotations.Auditable;
import vehcon.common.data.util.SpecificationUtils;
import vehcon.dto.masters.DepartmentDTO;
import vehcon.models.auth.User;
import vehcon.models.masters.Departments;
import vehcon.repo.masters.DepartmentsRepository;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    
	private final DepartmentsRepository departmentRepo;
	
	@Auditable
	@Transactional
    public Departments addDepartment(DepartmentDTO dto) {
        Departments dept = new Departments();
        dept.setDepartmentName(dto.getDepartmentName());
        return departmentRepo.save(dept);
    }

	@Transactional
    public List<Departments> getByDepartment(User user) {
        Integer departmentCode = user.getDepartment().getDepartmentCode();
        if (departmentCode != null) {
            return departmentRepo.getByDepartmentCode(departmentCode);
        }
        return List.of();
    }
    
	@Transactional
    public List<Departments> getDepartments()
	{
		return departmentRepo.findAll();
	}

    public Page<Departments> getAllDepartments(
            String globalSearchTerm,
            Integer filterByDepartmentCode,         
            String filterByDepartmentName,          
            Integer filterByDepartmentCodeIobs,
            Pageable pageable
    		) 
    {
        Specification<Departments> spec = Specification.where(null);

        if (globalSearchTerm != null && !globalSearchTerm.trim().isEmpty()) {
            List<String> globalSearchFields = List.of("departmentName");
            Specification<Departments> globalSearchSpec = SpecificationUtils.searchInFields(globalSearchTerm, globalSearchFields);
            if (globalSearchSpec != null) 
            {
                spec = spec.and(globalSearchSpec);
            }
        }

        if (filterByDepartmentCode != null) 
        {
            Specification<Departments> deptCodeSpec = SpecificationUtils.propertyEquals("departmentCode", filterByDepartmentCode);
            if (deptCodeSpec != null) 
            {
                spec = spec.and(deptCodeSpec);
            }
        }

        if (filterByDepartmentName != null && !filterByDepartmentName.trim().isEmpty()) 
        {    
        	Specification<Departments> deptNameSpec = SpecificationUtils.searchInFields(filterByDepartmentName, List.of("departmentName"));
            
        	if (deptNameSpec != null) 
        	{
                spec = spec.and(deptNameSpec);
            }
        }

        if (filterByDepartmentCodeIobs != null) 
        {
            Specification<Departments> iobsSpec = SpecificationUtils.propertyEquals("departmentCode_Iobs", filterByDepartmentCodeIobs);
            if (iobsSpec != null) 
            {
                spec = spec.and(iobsSpec);
            }
        }

        return departmentRepo.findAll(spec, pageable);
    }
}