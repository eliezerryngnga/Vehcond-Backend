package vehcon.models.masters;

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
	
	private String processname;
	
	private String processdescription;
	
	private String pageurl;

}