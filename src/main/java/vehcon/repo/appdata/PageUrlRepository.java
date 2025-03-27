package vehcon.repo.appdata;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vehcon.models.appdata.PageUrl;

@Repository
public interface PageUrlRepository extends JpaRepository<PageUrl, Integer> {
}
