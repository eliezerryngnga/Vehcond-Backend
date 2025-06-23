package vehcon.models.masters;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class UserPagesId implements Serializable{
	
	private static final long serialVersionUID = 1L; 

    @Column(name = "rolecode") 
    private Integer roleCode;   

    @Column(name = "urlcode")  
    private Integer urlCode;    

}
