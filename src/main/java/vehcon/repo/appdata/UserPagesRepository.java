package vehcon.repo.appdata;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import vehcon.models.fetch.UserPages;

public interface UserPagesRepository extends JpaRepository<UserPages, Integer>{

	//@Query()
	List<UserPages> findUserPages(Integer rolecode);
}
