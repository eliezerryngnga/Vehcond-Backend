package vehcon.repo.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vehcon.models.auth.Roles;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Integer>{
	
	Optional<Roles> findByRole(String role);
}
