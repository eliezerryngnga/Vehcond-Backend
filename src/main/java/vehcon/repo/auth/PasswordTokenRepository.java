package vehcon.repo.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import vehcon.models.auth.PasswordResetToken;

public interface PasswordTokenRepository extends JpaRepository<PasswordResetToken, Long>{
	
	Optional<PasswordResetToken> findByToken(String token);

}
