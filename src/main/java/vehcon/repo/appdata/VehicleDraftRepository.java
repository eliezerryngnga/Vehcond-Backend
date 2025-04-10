package vehcon.repo.appdata;

import org.springframework.data.jpa.repository.JpaRepository;

import vehcon.models.vehiclecondemnations.VehicleDraft;

public interface VehicleDraftRepository extends JpaRepository<VehicleDraft, String>{

}
