package vehcon.repo.appdata;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import vehcon.models.appdata.Vcctc;

public interface VccTcRepository extends JpaRepository<Vcctc, String>{
	
	Optional<Vcctc> findByApplicationCode(String applicationCode);

}
