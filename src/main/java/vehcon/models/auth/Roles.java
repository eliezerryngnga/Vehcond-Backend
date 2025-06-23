package vehcon.models.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "roles", schema = "auth")
@Data
public class Roles {
	
	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
	private Integer rolecode;
	
	@Column(name = "rolename", unique = true)
	private String role;
	
	@Column(name = "description")
	private String description;
//	@ManyToMany(mappedBy = "roles")
//	@ToString.Exclude   // Prevent circular reference
//    @EqualsAndHashCode.Exclude  // Exclude from equals/hashCode to avoid issues
//    @JsonBackReference
//    private Set<Menu> menus;

}
