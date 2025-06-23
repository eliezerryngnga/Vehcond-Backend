package vehcon.controller.masters;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vehcon.dto.masters.DepartmentDTO;
import vehcon.exception.UnauthorizedException;
import vehcon.models.auth.User;
import vehcon.models.masters.Departments;
import vehcon.services.masters.DepartmentService;

@RestController
@RequestMapping("/department")
@RequiredArgsConstructor
public class DepartmentController {
	
	private final DepartmentService departmentService;
	
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
	
	@GetMapping("/all-for-selection")
	public List<Departments> getAllDepartmentsForSelection()
	{
		try
		{
			return departmentService.getDepartments();
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
	
	@PostMapping("/add-department")
	public ResponseEntity<String> addDepartment(@RequestBody DepartmentDTO dto)
	{
		try
		{
			Departments savedDept = departmentService.addDepartment(dto);
			return ResponseEntity
					.status(HttpStatus.CREATED)
					.body("Department " + savedDept.getDepartmentName() + "created succesfully");
		}
		catch(Exception e)
		{
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to create department: " + e.getMessage());
		}
	}
	
	@GetMapping("/list")
    public ResponseEntity<Page<Departments>> listAllDepartments(
	            @RequestParam(required = false) String search,
	            @RequestParam(required = false) Integer departmentCode, 
	            @RequestParam(required = false) String departmentName, 
	            @RequestParam(required = false) Integer departmentCodeIobs, 
	            Pageable pageable, 
	            @AuthenticationPrincipal User user
    		) 
	{
        try 
        {
            Page<Departments> departmentsPage = departmentService.getAllDepartments(
                    search,
                    departmentCode,
                    departmentName,
                    departmentCodeIobs,
                    pageable
            );
            return ResponseEntity.ok(departmentsPage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(null);
        }
    }
}