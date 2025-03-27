package vehcon.models.appdata;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import vehcon.models.auth.Roles;

@Data
@Entity
@Table(name="userpages", schema="master")
public class UserPages {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "userpagecode")
	private String userPageCode;
	
	//@ManyToOne
    //@JoinColumn(name = "rolecode") 
    private Integer rolecode;

    @ManyToOne
    @JoinColumn(name = "urlcode")  
    private PageUrl pageUrl;
}

