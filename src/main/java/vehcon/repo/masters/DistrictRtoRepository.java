package vehcon.repo.masters;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vehcon.models.masters.DistrictRto;

public interface DistrictRtoRepository extends JpaRepository<DistrictRto, Integer>, JpaSpecificationExecutor<DistrictRto> {

}
