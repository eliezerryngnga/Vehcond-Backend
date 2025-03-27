package vehcon.repo.appdata;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vehcon.models.appdata.UserPages;

public interface UserPagesRepository extends JpaRepository<UserPages, String>{

	List<UserPages> findByRolecode(Integer rolecode);
}
