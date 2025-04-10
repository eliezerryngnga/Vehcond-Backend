package vehcon.repo.masters;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vehcon.models.masters.Departments;

public interface DepartmentsRepository extends JpaRepository<Departments, Integer>{
		List<Departments> getByDepartmentCode(Integer departmentCode);
}
