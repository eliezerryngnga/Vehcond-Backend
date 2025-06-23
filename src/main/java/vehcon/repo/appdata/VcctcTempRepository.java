package vehcon.repo.appdata;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import vehcon.models.appdata.VcctcTemp;

public interface VcctcTempRepository extends JpaRepository<VcctcTemp, String>{

	Optional<VcctcTemp> findByApplicationCode(String applicationCode);

}
