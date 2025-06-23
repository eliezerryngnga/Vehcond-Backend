package vehcon.repo.masters;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import vehcon.models.masters.PageUrl;

public interface PageUrlRepository extends JpaRepository<PageUrl, Integer> {
	
	
	Optional<PageUrl> findByUrlCode(Integer urlCode);
}
