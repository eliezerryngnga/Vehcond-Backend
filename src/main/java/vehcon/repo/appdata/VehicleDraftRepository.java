package vehcon.repo.appdata;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vehcon.models.vehiclecondemnations.VehicleDraft;

public interface VehicleDraftRepository extends JpaRepository<VehicleDraft, String>, JpaSpecificationExecutor<VehicleDraft>{

	Optional<VehicleDraft> findByApplicationCode(String applicationCode);
}
