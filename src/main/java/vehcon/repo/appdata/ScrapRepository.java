package vehcon.repo.appdata;

import org.springframework.data.jpa.repository.JpaRepository;

import vehcon.models.appdata.Scrap;

public interface ScrapRepository extends JpaRepository<Scrap, String> {

}
