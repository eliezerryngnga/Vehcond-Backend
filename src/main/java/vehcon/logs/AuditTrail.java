package vehcon.logs;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Data
@Table(name = "audit_logs", schema = "logs")
@Builder
@Entity
public class AuditTrail {
	
	@Id
	@GeneratedValue
	private long id;

	private String username;

	@Column(nullable = false)
	private String uri;

	@Lob
	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@Column(nullable = false)
	private String action;

	@Column(nullable = false)
	private String status;

	@Column(name = "ip_addr")
	private String ipAddress;

	@Column(nullable = false)
	private Date timestamp;
	
	@Column(name = "http_status")
	private Integer httpStatus;

}
