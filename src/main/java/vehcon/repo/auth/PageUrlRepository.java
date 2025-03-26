package vehcon.repo.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vehcon.models.fetch.PageUrl;

@Repository
public interface PageUrlRepository extends JpaRepository<PageUrl, Integer> {
}
