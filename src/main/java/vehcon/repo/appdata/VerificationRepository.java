package vehcon.repo.appdata;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import vehcon.models.appdata.Verification;

public interface VerificationRepository extends JpaRepository<Verification, String > {

	Optional<Verification> findByApplicationCode(String applicationCode);
	
//	boolean existsByApplicationCode(String applicationCode);

}
