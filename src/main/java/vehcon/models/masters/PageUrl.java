package vehcon.models.masters;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="pageurls", schema = "master")
public class PageUrl {
	
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	@Column(name="urlcode")
	private Integer urlCode;
	
	@Column(name="pageurl", length = 100)
	private String pageurl;
	
	@Column(name="subprocessname", length = 100)
	private String subProcessName;
	
	@Column(name="subprocessicon", length = 50)
	private String subProcessIcon;
	
	@Column(name="processname", length = 100)
	private String processName;
	
	@Column(name="processicon", length = 50)
	private String processIcon;
	
	@Column(name="showinmenu")
	private boolean showInMenu; 	

}
