package vehcon.dto.appdata;

import lombok.Data;

@Data
public class MenuLink {
	private Integer urlCode;
    private String pageurl;
    private String subprocessname;
    private String subprocessicon;
    private String processname;
    private String processicon;
    private boolean showInMenu;
}
