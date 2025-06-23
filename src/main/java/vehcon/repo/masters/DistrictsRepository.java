package vehcon.repo.masters;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vehcon.models.masters.Districts;

@Repository
public interface DistrictsRepository extends JpaRepository<Districts, Integer >, JpaSpecificationExecutor<Districts> {
//		List<Districts> findByDistrictCode(Integer districtcode);
}
