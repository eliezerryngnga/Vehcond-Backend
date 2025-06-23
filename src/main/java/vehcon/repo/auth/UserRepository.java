package vehcon.repo.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vehcon.models.auth.User;

public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User>{
	
	Optional<User> findByUsername(String username);
	Optional<User> findByUserAccess(boolean useraccess);

}
