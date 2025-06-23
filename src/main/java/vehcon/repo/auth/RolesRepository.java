package vehcon.repo.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vehcon.models.auth.Roles;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Integer>, JpaSpecificationExecutor<Roles>{
	
	Optional<Roles> findByRolecode(Integer rolecode);
	Optional<Roles> findByRole(String role);
}
