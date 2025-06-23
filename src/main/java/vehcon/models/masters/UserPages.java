package vehcon.models.masters;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Data;
import vehcon.models.auth.Roles;

@Data
@Entity
@Table(name="userpages", schema="master")
public class UserPages {
	
	@EmbeddedId
	private UserPagesId id;
	
	@Column(name = "userpagecode")
	private String userPageCode;
	
	@ManyToOne
	@MapsId("roleCode")
    @JoinColumn(name = "rolecode", referencedColumnName = "rolecode") 
    private Roles role;

    @ManyToOne
    @MapsId("urlCode")
    @JoinColumn(name = "urlcode", referencedColumnName = "urlcode")  
    private PageUrl pageUrl;
}
