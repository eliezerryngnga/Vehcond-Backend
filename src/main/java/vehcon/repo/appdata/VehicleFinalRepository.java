package vehcon.repo.appdata;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vehcon.models.appdata.VehicleFinal;

public interface VehicleFinalRepository extends JpaRepository<VehicleFinal, String> {
	
	List<VehicleFinal> findByProccessCode(Integer proccessCode);


}
