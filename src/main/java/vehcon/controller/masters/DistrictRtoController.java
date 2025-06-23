package vehcon.controller.masters;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vehcon.dto.masters.DistrictRtoDTO;
import vehcon.exception.UnauthorizedException;
import vehcon.models.auth.User;
import vehcon.models.masters.DistrictRto;
import vehcon.services.masters.DistrictRtoService;

@RestController
@RequestMapping("/district-rto")
@RequiredArgsConstructor
public class DistrictRtoController {
	
	private final DistrictRtoService districtRtoService;
	
	@GetMapping
	public List<DistrictRto> fetchDistrictRto()
	{
		try
		{
			return districtRtoService.getDistrictRto();
		}
		catch(UnauthorizedException ex)
		{
			throw ex;
		}
		catch(Exception e)
		{
			throw e;
		}
	}
	
	@GetMapping("/list")
	public ResponseEntity<Page<DistrictRto>> fetchAllDistrictRto(
			@RequestParam(required = false) String searchTerm,
			Pageable pageable,
			@AuthenticationPrincipal User user
			)
	{
		try {
			Page<DistrictRto> districtRto = districtRtoService.getAllDistrictRto(searchTerm, pageable);
			return ResponseEntity.ok(districtRto);
		}
		catch(Exception e)
		{
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(null);
 		}
	}
	
	@PostMapping("/add-district-rto")
	public ResponseEntity<String> addDistrictRto(@RequestBody DistrictRtoDTO dto)
	{
		try
		{
			DistrictRto savedDistrictRto = districtRtoService.addDistrictRto(dto);
			return ResponseEntity.status(HttpStatus.CREATED)
					.body("District RTO " + savedDistrictRto.getRtoCode() + " created successfully");
		}
		catch(Exception e)
		{
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to create district rto: " + e.getMessage());
		}
	}
}
