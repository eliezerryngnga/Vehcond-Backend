package vehcon.dto.masters;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DepartmentDTO {
	
	@NotBlank(message = "Cannot be blank")
	private String departmentName;
}
