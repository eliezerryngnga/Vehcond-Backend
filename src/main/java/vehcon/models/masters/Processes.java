package vehcon.models.masters;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "processes", schema="master")
@NoArgsConstructor
@AllArgsConstructor
public class Processes {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer processcode;
	
	@Column(length = 100)
	private String processname;
	
	@Column(length = 255)
	private String processdescription;
	
	@Column(length = 50)
	private String pageurl;

}