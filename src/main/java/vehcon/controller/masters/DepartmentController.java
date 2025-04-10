package vehcon.controller.masters;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vehcon.exception.UnauthorizedException;
import vehcon.models.auth.User;
import vehcon.models.masters.Departments;
import vehcon.repo.auth.RolesRepository;
import vehcon.services.masters.DepartmentService;

@RestController
@RequestMapping("/department")
@RequiredArgsConstructor
public class DepartmentController {
	
	private final DepartmentService departmentService;
	
	private final RolesRepository rolesRepo;
	
	@GetMapping
	public List<Departments> getByDepartment(@AuthenticationPrincipal User user)
	{
		try
		{
			return departmentService.getByDepartment(user);
		}
		catch(UnauthorizedException ex)
		{
			throw ex;
		}
		catch(Exception e)
		{
			throw e;
		}
	}
}
