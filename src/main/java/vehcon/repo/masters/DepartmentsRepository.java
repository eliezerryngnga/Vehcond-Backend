package vehcon.repo.masters;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vehcon.models.masters.Departments;

public interface DepartmentsRepository extends JpaRepository<Departments, Integer>, JpaSpecificationExecutor<Departments>{
		List<Departments> getByDepartmentCode(Integer departmentCode);
}
