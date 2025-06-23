package vehcon.repo.appdata;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vehcon.models.masters.Processes;

public interface ProcessesRepository extends JpaRepository<Processes,Integer>, JpaSpecificationExecutor<Processes> {
	
}
