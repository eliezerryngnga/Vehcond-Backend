package vehcon.services.masters;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vehcon.models.auth.User;
import vehcon.models.masters.Departments;
import vehcon.repo.auth.UserRepository;
import vehcon.repo.masters.DepartmentsRepository;

@Service
@RequiredArgsConstructor
public class DepartmentService {
	
	private final UserRepository userRepository;
	private final DepartmentsRepository departmentRepository;
	
	public List<Departments> getByDepartment(User user)
	{
		Integer departmentCode = user.getDepartmentCode();
		
		if(departmentCode != null)
		{
			return departmentRepository.getByDepartmentCode(departmentCode);
		}
		else
		{
			return List.of();
		}
	}
}