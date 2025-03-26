package vehcon.models.fetch;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="pageurls", schema = "master")
public class PageUrl {
	
	@Id
	@Column(name="urlcode")
	private Integer urlCode;
	
	@Column(name="pageurl")
	private String pageurl;
	
	@Column(name="subprocessname")
	private String subProcessName;
	
	@Column(name="subprocessicon")
	private String subProcessIcon;
	
	@Column(name="processname")
	private String processName;
	
	@Column(name="processicon")
	private String processIcon;
	
	@Column(name="showinmenu")
	private boolean showInMenu;

}
