package vehcon.repo.appdata;

import org.springframework.data.jpa.repository.JpaRepository;

import vehcon.models.appdata.PageUrl;

public interface PageUrlRepository extends JpaRepository<PageUrl, Integer> {
}
