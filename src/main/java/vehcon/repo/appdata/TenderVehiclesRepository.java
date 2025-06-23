package vehcon.repo.appdata;

import org.springframework.data.jpa.repository.JpaRepository;

import vehcon.models.appdata.TenderVehicles;

public interface TenderVehiclesRepository extends JpaRepository<TenderVehicles, String>{

}
