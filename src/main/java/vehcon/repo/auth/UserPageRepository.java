package vehcon.repo.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vehcon.models.fetch.UserPages;

import java.util.List;

@Repository
public interface UserPageRepository extends JpaRepository<UserPages,Integer> {
	List<UserPages> findByRole(Integer rolecode);
}
