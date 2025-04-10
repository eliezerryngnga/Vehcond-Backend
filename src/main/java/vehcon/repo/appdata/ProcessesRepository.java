package vehcon.repo.appdata;

import org.springframework.data.jpa.repository.JpaRepository;

import vehcon.models.masters.Processes;

public interface ProcessesRepository extends JpaRepository<Processes,Integer> {
	
}
