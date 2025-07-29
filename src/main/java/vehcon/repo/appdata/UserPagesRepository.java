package vehcon.repo.appdata;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vehcon.models.masters.UserPages;
import vehcon.models.masters.UserPagesId;

public interface UserPagesRepository extends JpaRepository<UserPages, UserPagesId>{

	List<UserPages> findByIdRoleCode(Integer roleCode);

	void deleteByIdRoleCode(Integer roleCode);
	
}
