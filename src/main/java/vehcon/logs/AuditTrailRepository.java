package vehcon.logs;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditTrailRepository extends JpaRepository<AuditTrail, Long>{
	
	Page<AuditTrail> findByTimestampBetween(LocalDate start, LocalDate end, Pageable pageable);

}
