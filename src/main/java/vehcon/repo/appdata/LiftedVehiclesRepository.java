package vehcon.repo.appdata;

import org.springframework.data.jpa.repository.JpaRepository;

import vehcon.models.appdata.LiftedVehicles;

public interface LiftedVehiclesRepository extends JpaRepository<LiftedVehicles, String> {

}
